package com.github.felixgail.gplaymusic.model.responses;

import android.text.TextUtils;

import com.github.felixgail.gplaymusic.model.Album;
import com.github.felixgail.gplaymusic.model.Artist;
import com.github.felixgail.gplaymusic.model.Playlist;
import com.github.felixgail.gplaymusic.model.PodcastSeries;
import com.github.felixgail.gplaymusic.model.Station;
import com.github.felixgail.gplaymusic.model.Track;
import com.github.felixgail.gplaymusic.model.Video;
import com.github.felixgail.gplaymusic.model.listennow.Situation;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

//TODO: Add result scores
public class SearchResponse implements Serializable {

  @Expose
  private List<Result> entries = new LinkedList<>();

  public SearchResponse() {
    entries = new LinkedList<>();
  }

  public List<Result> getEntries() {
    return entries;
  }

  public List<Track> getTracks() {
    List<Track> result = new ArrayList<>();
    for (Result r : entries) {
      if (r instanceof Track) {
        result.add((Track) r);
      }
    }
    return result;
  }

  public List<Artist> getArtists() {
    List<Artist> result = new ArrayList<>();
    for (Result r : entries) {
      if (r instanceof Artist) {
        result.add((Artist) r);
      }
    }
    return result;
  }

  public List<Album> getAlbums() {
    List<Album> result = new ArrayList<>();
    for (Result r : entries) {
      if (r instanceof Album) {
        result.add((Album) r);
      }
    }
    return result;
  }

  public List<Playlist> getPlaylists() {
    List<Playlist> result = new ArrayList<>();
    for (Result r : entries) {
      if (r instanceof Playlist) {
        result.add((Playlist) r);
      }
    }
    return result;
  }

  public List<Station> getStations() {
    List<Station> result = new ArrayList<>();
    for (Result r : entries) {
      if (r instanceof Station) {
        result.add((Station) r);
      }
    }
    return result;
  }

  public List<Situation> getSituations() {
    List<Situation> result = new ArrayList<>();
    for (Result r : entries) {
      if (r instanceof Situation) {
        result.add((Situation) r);
      }
    }
    return result;
  }

  public List<Video> getVideos() {
    List<Video> result = new ArrayList<>();
    for (Result r : entries) {
      if (r instanceof Video) {
        result.add((Video) r);
      }
    }
    return result;
  }

  public List<PodcastSeries> getPodcastSeries() {
    List<PodcastSeries> result = new ArrayList<>();
    for (Result r : entries) {
      if (r instanceof PodcastSeries) {
        result.add((PodcastSeries) r);
      }
    }
    return result;
  }

  public String string() {
    return "SearchResults:\n\t" +
            TextUtils.join("\n\t", entries);
  }
}
