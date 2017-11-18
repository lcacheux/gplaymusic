package com.github.felixgail.gplaymusic.model;

import android.provider.MediaStore;

import com.github.felixgail.gplaymusic.api.GPlayMusic;
import com.github.felixgail.gplaymusic.cache.LibraryTrackCache;
import com.github.felixgail.gplaymusic.exceptions.NetworkException;
import com.github.felixgail.gplaymusic.model.enums.Provider;
import com.github.felixgail.gplaymusic.model.enums.ResultType;
import com.github.felixgail.gplaymusic.model.enums.StreamQuality;
import com.github.felixgail.gplaymusic.model.enums.SubscriptionType;
import com.github.felixgail.gplaymusic.model.requests.IncrementPlaycountRequest;
import com.github.felixgail.gplaymusic.model.responses.Result;
import com.github.felixgail.gplaymusic.model.snippets.ArtRef;
import com.github.felixgail.gplaymusic.util.language.Language;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Track extends Signable implements Result, Serializable {
  public final static ResultType RESULT_TYPE = ResultType.TRACK;
  private static Gson gsonPrettyPrinter = new GsonBuilder().setPrettyPrinting().create();
  private static LibraryTrackCache libraryTrackCache = new LibraryTrackCache();

  //TODO: Not all Attributes added (eg. PrimaryVideo, ID? where is id used).
  @Expose
  private String title;
  @Expose
  private String artist;
  @Expose
  private String composer;
  @Expose
  private String album;
  @Expose
  private String albumArtist;
  @Expose
  private int year;
  @Expose
  private int trackNumber;
  @Expose
  private String genre;
  @Expose
  private String durationMillis;
  @Expose
  private List<ArtRef> albumArtRef;
  @Expose
  private List<ArtRef> artistArtRef;
  @Expose
  private int discNumber;
  @Expose
  private String estimatedSize;
  @Expose
  private String trackType;
  @Expose
  private String storeId;
  @Expose
  private String albumId;
  @Expose
  private List<String> artistId;
  @Expose
  private String nid;
  @Expose
  private boolean trackAvailableForSubscription;
  @Expose
  private boolean trackAvailableForPurchase;
  @Expose
  private boolean albumAvailableForPurchase;
  @Expose
  private String explicitType;
  @Expose
  private int playCount;
  @Expose
  private String rating;
  @Expose
  private int beatsPerMinute;
  @Expose
  private String clientId;
  @Expose
  private String comment;
  @Expose
  private int totalTrackCount;
  @Expose
  private int totalDiscCount;
  @Expose
  private String lastRatingChangeTimestamp;
  @Expose
  private String lastModifiedTimestamp;
  @Expose
  private String contentType;
  @Expose
  private String creationTimestamp;
  @Expose
  private String recentTimestamp;
  @Expose
  @SerializedName("id")
  private String uuid;
  @Expose
  @SerializedName("primaryVideo")
  private Video video;

  private String sessionToken;

  //This attribute is only set when the track is retrieved from a station.
  @Expose
  @SerializedName("wentryid")
  private String wentryID;

  public Track(@NotNull String id, @NotNull String title, @NotNull String artist, @NotNull String album,
               int trackNumber, long durationMillis, int discNumber, long estimatedSize, @NotNull String albumId,
               @NotNull String contentType) {
    this.title = title;
    this.artist = artist;
    this.album = album;
    this.albumArtist = "";
    this.durationMillis = String.valueOf(durationMillis);
    this.trackNumber = trackNumber;
    this.discNumber = discNumber;
    this.estimatedSize = String.valueOf(estimatedSize);
    this.albumId = albumId;
    this.storeId = id;
    this.contentType = contentType;
  }

  public static Track getTrack(String trackID) throws IOException {
    Track track = null;
    if (trackID.startsWith("T")) {
      track = GPlayMusic.getApiInstance().getService().fetchTrack(trackID).execute().body();
    } else {
      track = libraryTrackCache.find(trackID);
      if (track == null) {
        throw new IllegalArgumentException(String.format("No track with id '%s' found.", trackID));
      }
    }
    if (track == null || track.getID() == null) {
      throw new IOException(String.format("'%s' did not return a valid track", trackID));
    }
    return track;
  }

  public String getTitle() {
    return title;
  }

  public String getArtist() {
    return artist;
  }

  public String getComposer() {
    return composer;
  }

  public String getAlbum() {
    return album;
  }

  public String getAlbumArtist() {
    return albumArtist;
  }

  public int getYear() {
    return year;
  }

  public int getTrackNumber() {
    return trackNumber;
  }

  //TODO: Return genre instead of string
  public String getGenre() {
    return genre;
  }

  public Long getDurationMillis() {
    return Long.parseLong(durationMillis);
  }

  public List<ArtRef> getAlbumArtRef() {
    return albumArtRef;
  }

  public List<ArtRef> getArtistArtRef() {
    return artistArtRef;
  }

  public int getDiscNumber() {
    return discNumber;
  }

  public long getEstimatedSize() {
    return Long.parseLong(estimatedSize);
  }

  public String getTrackType() {
    return trackType;
  }

  /**
   * Returns how often the song has been played. Not valid, when song has been fetched via
   * {@link Track#getTrack(String)} as the server response does not contain this key.
   */
  public int getPlayCount() {
    return playCount;
  }

  public String getRating() {
    return rating;
  }

  public int getBeatsPerMinute() {
    return beatsPerMinute;
  }

  public String getClientId() {
    return clientId;
  }

  public String getComment() {
    return comment;
  }

  @Override
  public String getID() {
    if (getStoreId() != null) {
      return getStoreId();
    } else if (getUuid() != null) {
      return getUuid();
    } else {
      throw new NullPointerException("Track contains neither StoreID nor UUID.");
    }
  }

  public String getStoreId() {
    return storeId;
  }

  public String getAlbumId() {
    return albumId;
  }

  public List<String> getArtistId() {
    return artistId;
  }

  public String getNid() {
    return nid;
  }

  public boolean isTrackAvailableForSubscription() {
    return trackAvailableForSubscription;
  }


  public boolean isTrackAvailableForPurchase() {
    return trackAvailableForPurchase;
  }

  public boolean isAlbumAvailableForPurchase() {
    return albumAvailableForPurchase;
  }

  public String getExplicitType() {
    return explicitType;
  }

  public String getWentryID() {
    return wentryID;
  }

  public int getTotalTrackCount() {
    return totalTrackCount;
  }

  public int getTotalDiscCount() {
    return totalDiscCount;
  }

  public String getLastRatingChangeTimestamp() {
    return lastRatingChangeTimestamp;
  }

  public String getLastModifiedTimestamp() {
    return lastModifiedTimestamp;
  }

  public String getContentType() {
    return contentType;
  }

  public String getCreationTimestamp() {
    return creationTimestamp;
  }

  public String getRecentTimestamp() {
    return recentTimestamp;
  }

  public String getUuid() {
    return uuid;
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof Track) && ((Track) o).getID().equals(this.getID());
  }

  @Override
  public Signature getSignature() {
    return super.createSignature(this.getID());
  }

  @Override
  public ResultType getResultType() {
    return RESULT_TYPE;
  }

  /**
   * Returns a URL to download the song in set quality.
   * URL will only be valid for 1 minute.
   * You will likely need to handle redirects.
   * <br>
   * <b>Please note that this function is available for Subscribers only.
   * On free accounts use {@link #getStationTrackURL(StreamQuality)}.</b>
   *
   * @param quality quality of the stream
   * @return temporary url to the title
   * @throws IOException Throws an IOException on severe failures (no internet connection...)
   *                     or a {@link NetworkException} on request failures.
   */
  @Override
  public URL getStreamURL(StreamQuality quality)
      throws IOException {
    if (GPlayMusic.getApiInstance().getConfig().getSubscription() == SubscriptionType.FREE) {
      throw new IOException(Language.get("users.free.NotAllowed"));
    }
    return urlFetcher(quality, Provider.STREAM, EMPTY_MAP);
  }

  /**
   * Fetch the URL for a track from a free Station.
   * Make sure this Track was returned by a {@link Station}. Otherwise an {@link IOException} will be thrown.
   * <br>
   * <b>Subscribers will be redirected to {@link #getStreamURL(StreamQuality)}</b>
   *
   * @param quality - quality of the stream
   * @return a url to download songs from.
   * @throws IOException on severe failures (no internet connection...)
   *                     or a {@link NetworkException} on request failures.
   */
  public URL getStationTrackURL(StreamQuality quality)
      throws IOException {
    if (GPlayMusic.getApiInstance().getConfig().getSubscription() == SubscriptionType.ALL_ACCESS) {
      return getStreamURL(quality);
    }
    if (getWentryID() == null || getWentryID().isEmpty()) {
      throw new IOException(Language.get("track.InvalidWentryID"));
    }
    if (getSessionToken() == null) {
      throw new IOException(Language.get("station.InvalidSessionToken"));
    }
    Map<String, String> map = new HashMap<>();
    map.putAll(STATION_MAP);
    map.put("wentryid", getWentryID());
    map.put("sesstok", getSessionToken());
    return urlFetcher(quality, Provider.STATION, map);
  }

  /**
   * Increments the playcount of this song by {@code count}.
   *
   * @param count amount of plays that will be added to the current count.
   * @return whether the incrementation was successful.
   */
  public boolean incrementPlaycount(int count) throws IOException {
    MutationResponse response = GPlayMusic.getApiInstance().getService().incremetPlaycount(
        new IncrementPlaycountRequest(count, this)).execute().body();
    if (response.checkSuccess()) {
      playCount += count;
      return true;
    }
    return false;
  }

  public String string() {
    return gsonPrettyPrinter.toJson(this);
  }

  /**
   * Downloads the song to the provided path. Existing files will be replaced.
   */
  /*public void download(StreamQuality quality, Path path) throws IOException {
    Files.copy(getStationTrackURL(quality).openStream(), path, StandardCopyOption.REPLACE_EXISTING);
  }*/

  /**
   * Library tracks can only be fetched as whole. To shorten wait times, collected songs are cached.
   * Please consider updating the cache (asynchronously) when using the library over a long period of time, or when
   * new songs could be added to the library during runtime.
   * <br>
   * If outside access to the library is expected during runtime, disabling caching via {@link #useCache(boolean)}
   * should also be considered.
   */
  public static void updateCache() throws IOException {
    libraryTrackCache.update();
  }

  /**
   * Enables/Disables caching of library tracks.
   */
  public static void useCache(boolean useCache) {
    libraryTrackCache.setUseCache(useCache);
  }

  void setSessionToken(String token) {
    this.sessionToken = token;
  }

  private String getSessionToken() {
    return sessionToken;
  }

  public Video getVideo() {
    return video;
  }
}
