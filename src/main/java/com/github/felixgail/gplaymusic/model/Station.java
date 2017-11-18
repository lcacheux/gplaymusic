package com.github.felixgail.gplaymusic.model;

import com.github.felixgail.gplaymusic.api.GPlayMusic;
import com.github.felixgail.gplaymusic.api.GPlayServiceTools;
import com.github.felixgail.gplaymusic.exceptions.NetworkException;
import com.github.felixgail.gplaymusic.model.enums.ResultType;
import com.github.felixgail.gplaymusic.model.requests.ListStationTracksRequest;
import com.github.felixgail.gplaymusic.model.requests.mutations.MutationFactory;
import com.github.felixgail.gplaymusic.model.requests.mutations.Mutator;
import com.github.felixgail.gplaymusic.model.responses.Result;
import com.github.felixgail.gplaymusic.model.snippets.ArtRef;
import com.github.felixgail.gplaymusic.model.snippets.StationSeed;
import com.github.felixgail.gplaymusic.util.language.Language;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Station implements Result, Serializable {
  public final static ResultType RESULT_TYPE = ResultType.STATION;
  public final static String BATCH_URL = "radio/editstation";
  private final static Gson gson = new GsonBuilder().setPrettyPrinting().create();

  @Expose
  private String name;
  @Expose
  private String imageUrl;
  @Expose
  private boolean deleted;
  @Expose
  private String lastModifiedTimestamp;
  @Expose
  private String recentTimestamp;
  @Expose
  private String clientId;
  @Expose
  private String sessionToken;
  @Expose
  private StationSeed seed;
  @Expose
  private List<StationSeed> stationSeeds;
  @Expose
  private String id;
  @Expose
  private String description;
  @Expose
  private List<Track> tracks = new LinkedList<>();
  @Expose
  @SerializedName("imageUrls")
  private List<ArtRef> imageArtRefs;
  @Expose
  private List<ArtRef> compositeArtRefs;
  @Expose
  private List<String> contentTypes;
  @Expose
  private String byline;

  public Station(@NotNull final String name, @NotNull final StationSeed seed, final List<Track> tracks) {
    this.name = name;
    this.seed = seed;
    this.tracks = tracks;
  }

  public Station(@NotNull final String name, @NotNull final StationSeed seed) {
    this(name, seed, null);
  }

  /**
   * Creates a new Station.
   *
   * @param seed          a seed to build the station upon.
   * @param name          name of the new station
   * @param includeTracks whether the response should
   * @return Returns the newly created station
   * @throws IOException
   */
  public static Station create(final StationSeed seed, final String name, final boolean includeTracks)
      throws IOException {
    final Mutator mutator = new Mutator(MutationFactory.getAddStationMutation(name, seed, includeTracks));
    final MutationResponse response = GPlayServiceTools.makeBatchCall(GPlayMusic.getApiInstance().getService(), BATCH_URL, mutator);
    MutationResponse.Item item = response.getItems().get(0);
    if (item.hasStationKey()) {
      return item.getStation();
    }
    throw new NetworkException(400, Language.get("station.create.NetworkException"));
  }

  public String getName() {
    return name;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public String getLastModifiedTimestamp() {
    return lastModifiedTimestamp;
  }

  public String getRecentTimestamp() {
    return recentTimestamp;
  }

  public String getClientId() {
    return clientId;
  }

  public StationSeed getSeed() {
    return seed;
  }

  public List<StationSeed> getStationSeeds() {
    return stationSeeds;
  }

  public String getId() throws IOException {
    if (id != null) {
      return id;
    }
    if (getSeed() != null) {
      Station createOrGet = Station.create(getSeed(), getName(), false);
      this.id = createOrGet.id;
      this.clientId = createOrGet.clientId;
      return id;
    }
    throw new NullPointerException("Radio does not contain ID or Seeds");
  }

  public String getDescription() {
    return description;
  }

  /**
   * Get Tracks for this Station.<br>
   * <b>
   * Keep in mind that this can return an empty list, if this station is created on an empty playlist.
   * </b>
   *
   * @param recentlyPlayed     a list of tracks that have recently been played. tracks from this list will,
   *                           <b>most of the time</b>,
   *                           be excluded from the response. For some reason this is sometimes ignored by the server.
   *                           Use {@code forceRemoveDoubles} to remove doubles returned by the server.
   * @param newCall            true if a new call shall be dispatched. false if the list from a previous call is to be returned.
   *                           Careful: Will return an empty list if no call has been made.
   * @param forceRemoveDoubles see {@code recentlyPlayed}. Force remove doubles returned by the server.
   * @return A list of 25 tracks for this station.
   */
  public List<Track> getTracks(List<Track> recentlyPlayed, boolean newCall, boolean forceRemoveDoubles)
      throws IOException {
    if (!newCall) {
      return tracks;
    }
    ListStationTracksRequest request = new ListStationTracksRequest(this, 25, recentlyPlayed);
    Station returnedStation = GPlayMusic.getApiInstance().getService().getFilledStations(request)
        .execute().body().toList().get(0);
    List<Track> trackOptional = returnedStation.tracks;
    sessionToken = returnedStation.sessionToken;
    List<Track> tracks = trackOptional;
    for (Track t : tracks) {
      t.setSessionToken(sessionToken);
    }
    if (forceRemoveDoubles) {
      Iterator<Track> iter = tracks.iterator();
      while (iter.hasNext()) {
        Track track = iter.next();
        for (Track recent : recentlyPlayed) {
          if (track.getID().equals(recent.getID())) {
            iter.remove();
          }
        }
      }
    }
    return tracks;
  }

  public List<ArtRef> getImageArtRefs() {
    return imageArtRefs;
  }

  public List<ArtRef> getCompositeArtRefs() {
    return compositeArtRefs;
  }

  public List<String> getContentTypes() {
    return contentTypes;
  }

  public String getByline() {
    return byline;
  }

  public String getSessionToken() {
    return sessionToken;
  }

  @Override
  public ResultType getResultType() {
    return RESULT_TYPE;
  }

  public void delete()
      throws IOException {
    GPlayMusic.getApiInstance().deleteStations(this);
  }

  public String string() {
    return gson.toJson(this);
  }
}
