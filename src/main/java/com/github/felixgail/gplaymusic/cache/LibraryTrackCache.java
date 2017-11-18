package com.github.felixgail.gplaymusic.cache;

import com.github.felixgail.gplaymusic.api.GPlayMusic;
import com.github.felixgail.gplaymusic.model.PagingHandler;
import com.github.felixgail.gplaymusic.model.Track;
import com.github.felixgail.gplaymusic.model.requests.PagingRequest;
import com.github.felixgail.gplaymusic.model.responses.ListResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LibraryTrackCache extends Cache<Track> {
  private PagingHandler<Track> pagingHandler;

  public LibraryTrackCache() {
    pagingHandler = new PagingHandler<Track>() {
      @Override
      public ListResult<Track> getChunk(String nextPageToken) throws IOException {
        return GPlayMusic.getApiInstance().getService()
            .listTracks(new PagingRequest(nextPageToken, -1)).execute().body();
      }

      @Override
      public List<Track> next() throws IOException {
        List<Track> result = new ArrayList<>();
        for (Track t : super.next()) {
          if (t.getStoreId() == null && t.getUuid() == null) {
            result.add(t);
          }
        }
        return result;
      }
    };
  }

  @Override
  public void update() throws IOException {
    List<Track> trackList = pagingHandler.getAll();
    setCache(trackList);
  }

  public Track find(String trackID) throws IOException {
    if (!isUseCache()) {
      pagingHandler.reset();
    } else {
      for (Track t : getCurrentCache()) {
        if (t.getID().equals(trackID)) {
          return t;
        }
      }
    }
    while (pagingHandler.hasNext()) {
      List<Track> tracks = pagingHandler.next();
      if (tracks.size() > 0) {
        if (isUseCache()) {
          add(tracks);
        }
        for (Track t : tracks) {
          if (t.getID().equals(trackID)) {
            return t;
          }
        }
      }
    }
    return null;
  }
}
