package com.github.felixgail.gplaymusic.model;

import com.fasterxml.uuid.Generators;
import com.github.felixgail.gplaymusic.api.GPlayMusic;
import com.github.felixgail.gplaymusic.api.GPlayServiceTools;
import com.github.felixgail.gplaymusic.cache.Cache;
import com.github.felixgail.gplaymusic.cache.PrivatePlaylistEntriesCache;
import com.github.felixgail.gplaymusic.model.enums.ResultType;
import com.github.felixgail.gplaymusic.model.requests.SharedPlaylistRequest;
import com.github.felixgail.gplaymusic.model.requests.mutations.Mutation;
import com.github.felixgail.gplaymusic.model.requests.mutations.MutationFactory;
import com.github.felixgail.gplaymusic.model.requests.mutations.Mutator;
import com.github.felixgail.gplaymusic.model.responses.Result;
import com.github.felixgail.gplaymusic.model.snippets.ArtRef;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

//TODO: Split into Public and Private Playlist. What to do about magic playlists?
public class Playlist implements Result, Serializable {
  public final static ResultType RESULT_TYPE = ResultType.PLAYLIST;
  public final static String BATCH_URL = "playlistbatch";
  private static PrivatePlaylistEntriesCache cache = new PrivatePlaylistEntriesCache();

  @Expose
  private String name;
  @Expose
  private PlaylistType type;
  @Expose
  private String shareToken;
  @Expose
  private String description;
  @Expose
  private String ownerName;
  @Expose
  private String ownerProfilePhotoUrl;
  @Expose
  private String lastModifiedTimestamp;
  @Expose
  private String recentTimestamp;
  @Expose
  private boolean accessControlled;
  @Expose
  private boolean deleted;
  @Expose
  private String creationTimestamp;
  @Expose
  private String id;
  @Expose
  @SerializedName("albumArtRef")
  private List<ArtRef> artRef;
  @Expose
  private String explicitType;
  @Expose
  private String contentType;
  @Expose
  private PlaylistShareState shareState;

  private Playlist(String name, String id, PlaylistShareState shareState, String description, PlaylistType type,
                   String lastModifiedTimestamp, String creationTimestamp) {
    this.name = name;
    this.id = id;
    this.shareState = shareState;
    this.description = description;
    this.type = type;
    this.lastModifiedTimestamp = lastModifiedTimestamp;
    this.creationTimestamp = creationTimestamp;
  }

  public Playlist(String id) throws IOException {
    Playlist remote = null;
    for (Playlist p : GPlayMusic.getApiInstance().listPlaylists()) {
      if (p.getId().equals(id)) {
        remote = p;
        break;
      }
    }
    if (remote != null) {
      this.name = remote.name;
      this.id = remote.id;
      this.shareState = remote.shareState;
      this.description = remote.description;
      this.shareToken = remote.shareToken;
      this.lastModifiedTimestamp = remote.lastModifiedTimestamp;
      this.creationTimestamp = remote.creationTimestamp;
      this.recentTimestamp = remote.recentTimestamp;
      this.ownerName = remote.ownerName;
      this.ownerProfilePhotoUrl = remote.ownerProfilePhotoUrl;
      this.type = remote.type;
      this.accessControlled = remote.accessControlled;
      this.deleted = remote.deleted;
      this.artRef = remote.artRef;
      this.explicitType = remote.explicitType;
      this.contentType = remote.contentType;
    } else {
      throw new IllegalArgumentException("This user is not subscribed to any playlist with that id.");
    }
  }

  /**
   * Creates a new playlist.
   *
   * @param name        Name of the playlist. <b>Doesn't</b> have to be unique
   * @param description Optional. A description for the playlist.
   * @param shareState  share state of the playlist. defaults to {@link PlaylistShareState#PRIVATE} on null.
   * @return The newly created Playlist. Warning: Playlist is not filled yet and timestamps are not valid
   * (Systemtime@Request != Servertime@Creation)
   * @throws IOException
   */
  public static Playlist create(String name, String description, PlaylistShareState shareState)
      throws IOException {
    Mutator mutator = new Mutator(MutationFactory.getAddPlaylistMutation(name, description, shareState));
    String systemTime = Long.toString(System.currentTimeMillis());
    MutationResponse response = GPlayServiceTools.makeBatchCall(GPlayMusic.getApiInstance().getService(), BATCH_URL, mutator);
    String id = response.getItems().get(0).getId();
    return new Playlist(name, id, (shareState == null ? PlaylistShareState.PRIVATE : shareState),
        description, PlaylistType.USER_GENERATED, systemTime, systemTime);
  }

  /**
   * {@link PlaylistEntry}s from private Playlists can only be fetched as whole.
   * To shorten wait times, collected entries are cached.
   * Please consider updating the cache (asynchronously) when using the api over a long period of time, or when
   * new entries could be added to the playlists during runtime.
   * <br>
   * If outside access to the library is expected during runtime, disabling caching via {@link #setUseCache(boolean)}
   * should also be considered.
   */
  public static void updateCache() throws IOException {
    cache.update();
  }

  /**
   * Enables/Disables caching of {@link PlaylistEntry}s from private playlists.
   */
  public static void setUseCache(boolean useCache) {
    cache.setUseCache(useCache);
  }

  public static Cache<PlaylistEntry> getCache() {
    return cache;
  }

  public String getName() {
    return name;
  }

  public PlaylistType getType() {
    if (type == null) {
      return PlaylistType.USER_GENERATED;
    }
    return type;
  }

  public String getShareToken() {
    return shareToken;
  }

  public String getDescription() {
    return description;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public String getOwnerProfilePhotoUrl() {
    return ownerProfilePhotoUrl;
  }

  public String getLastModifiedTimestamp() {
    return lastModifiedTimestamp;
  }

  public String getRecentTimestamp() {
    return recentTimestamp;
  }

  public boolean isAccessControlled() {
    return accessControlled;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public String getCreationTimestamp() {
    return creationTimestamp;
  }

  public String getId() {
    return id;
  }

  public List<ArtRef> getArtRef() {
    return artRef;
  }

  public String getExplicitType() {
    return explicitType;
  }

  public String getContentType() {
    return contentType;
  }

  public PlaylistShareState getShareState() {
    if (shareState == null) {
      return PlaylistShareState.PRIVATE;
    }
    return shareState;
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof Playlist) &&
        ((this.shareToken != null &&
            ((Playlist) o).shareToken != null &&
            this.shareToken.equals(((Playlist) o).getShareToken())) ||
            (this.id != null && ((Playlist) o).getId() != null &&
                this.getId().equals(((Playlist) o).getId())));
  }

  @Override
  public ResultType getResultType() {
    return RESULT_TYPE;
  }

  public void delete() throws IOException {
    GPlayMusic.getApiInstance().deletePlaylists(this);
  }

  /**
   * Adds {@link Track}s to this playlist.
   *
   * @param tracks Array of tracks to be added
   * @throws IOException
   */
  public void addTracks(List<Track> tracks)
      throws IOException {
    List<PlaylistEntry> playlistEntries = new LinkedList<>();
    Mutator mutator = new Mutator();
    UUID last = null;
    UUID current = Generators.timeBasedGenerator().generate();
    UUID next = Generators.timeBasedGenerator().generate();
    for (Track track : tracks) {
      Mutation currentMutation = MutationFactory.
          getAddPlaylistEntryMutation(this, track, last, current, next);
      mutator.addMutation(currentMutation);
      last = current;
      current = next;
      next = Generators.timeBasedGenerator().generate();
    }
    MutationResponse response = GPlayServiceTools.makeBatchCall(GPlayMusic.getApiInstance().getService(), PlaylistEntry.BATCH_URL, mutator);
    Playlist.updateCache();
  }

  /**
   * see javadoc at {@link #addTracks(List)}.
   */
  public void addTracks(Track... tracks) throws IOException {
    addTracks(Arrays.asList(tracks));
  }

  /**
   * Returns the contents (as a list of PlaylistEntries) for this playlist.
   *
   * @param maxResults only applicable for shared playlist, otherwise ignored.
   *                   Sets the amount of entries that should be returned.
   *                   Valid range between 0 and 1000. Invalid values will default to 1000.
   * @throws IOException
   */
  public List<PlaylistEntry> getContents(int maxResults)
      throws IOException {
    if (!getType().equals(PlaylistType.SHARED)) {
      //python implementation suggests that this should also work for magic playlists
      return getContentsForUserGeneratedPlaylist(maxResults);
    }
    return getContentsForSharedPlaylist(maxResults);
  }

  private List<PlaylistEntry> getContentsForUserGeneratedPlaylist(int maxResults)
      throws IOException {
    List<PlaylistEntry> result = new ArrayList<>();
    for (PlaylistEntry entry : cache.get()) {
      if (entry.getPlaylistId().equals(getId()) && !entry.isDeleted()) {
        result.add(entry);
      }
    }
    Collections.sort(result);
    if (maxResults > 0) {
      result = result.subList(0, maxResults);
    }

    return result;
  }

  private List<PlaylistEntry> getContentsForSharedPlaylist(int maxResults)
      throws IOException {
    SharedPlaylistRequest requestBody = new SharedPlaylistRequest(this, maxResults);
    return GPlayMusic.getApiInstance().getService().listSharedPlaylistEntries(requestBody).execute().body().toList();
  }

  public void removeEntries(List<PlaylistEntry> entries) throws IOException {
    GPlayMusic.getApiInstance().deletePlaylistEntries(entries);
  }

  public void removeEntries(PlaylistEntry... entries) throws IOException {
    removeEntries(Arrays.asList(entries));
  }

  public enum PlaylistType implements Serializable {
    @SerializedName("SHARED")
    SHARED,
    //TODO: find out what a magic playlist is. last hint: i don't have a magic playlist. next idea: crawl python implementation
    @SerializedName("MAGIC")
    MAGIC,
    @SerializedName("USER_GENERATED")
    USER_GENERATED
  }

  public enum PlaylistShareState implements Serializable {
    @SerializedName("PRIVATE")
    PRIVATE,
    @SerializedName("PUBLIC")
    PUBLIC
  }
}
