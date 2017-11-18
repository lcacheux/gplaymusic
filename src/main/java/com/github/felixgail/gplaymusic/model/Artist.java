package com.github.felixgail.gplaymusic.model;

import com.github.felixgail.gplaymusic.api.GPlayMusic;
import com.github.felixgail.gplaymusic.model.enums.ResultType;
import com.github.felixgail.gplaymusic.model.responses.Result;
import com.github.felixgail.gplaymusic.model.snippets.ArtRef;
import com.github.felixgail.gplaymusic.model.snippets.Attribution;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public class Artist implements Result, Serializable {
  public final static ResultType RESULT_TYPE = ResultType.ARTIST;

  @Expose
  private String name;
  @Expose
  @SerializedName("artistArtRef")
  private String artistArtUrl;
  @Expose
  private List<ArtRef> artistArtRefs;
  @Expose
  private String artistId;
  @Expose
  @SerializedName("artist_bio_attribution")
  private Attribution artistBioAttribution;
  @Expose
  private String artistBio;
  @Expose
  @SerializedName("related_artists")
  private List<Artist> relatedArtists;
  @Expose
  @SerializedName("total_albums")
  private int totalAlbums;
  @Expose
  private List<Track> topTracks;
  @Expose
  private List<Album> albums;

  public Artist(@NotNull String name) {
    this.name = name;
  }

  public List<Artist> getRelatedArtists() {
    return relatedArtists;
  }

  public int getTotalAlbums() {
    return totalAlbums;
  }

  public List<Track> getTopTracks() {
    return topTracks;
  }

  public String getName() {
    return name;
  }

  public String getArtistArtUrl() {
    return artistArtUrl;
  }

  public List<ArtRef> getArtistArtRefs() {
    return artistArtRefs;
  }

  public String getArtistId() {
    return artistId;
  }

  public Attribution getArtistBioAttribution() {
    return artistBioAttribution;
  }

  public String getArtistBio() {
    return artistBio;
  }

  public List<Album> getAlbums() {
    return albums;
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof Artist) && ((Artist) o).getArtistId().equals(this.artistId);
  }

  @Override
  public ResultType getResultType() {
    return RESULT_TYPE;
  }

  /**
   * Fetches for an artist by {@code artistID}.
   *
   * @param artistID      {@link Artist#getArtistId()} of the artist searched for.
   * @param includeAlbums whether albums of the artist shall be included in the response.
   * @param numTopTracks  response includes up to provided number of most heard songs in response
   * @param numRelArtist  response includes up to provided number of similar artist in response
   * @return An executable call which returns an artist on execution.
   */
  public static Artist getArtist(String artistID, boolean includeAlbums, int numTopTracks, int numRelArtist)
      throws IOException {
    return GPlayMusic.getApiInstance().getService().getArtist(artistID, includeAlbums, numTopTracks, numRelArtist)
        .execute().body();
  }
}
