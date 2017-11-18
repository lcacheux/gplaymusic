package com.github.felixgail.gplaymusic.model.responses;

import com.github.felixgail.gplaymusic.model.Genre;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public class GenreResponse implements Serializable {
  @Expose
  private List<Genre> genres;

  public List<Genre> getGenres() {
    return genres;
  }
}
