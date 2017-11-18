package com.github.felixgail.gplaymusic.model.snippets;

import com.github.felixgail.gplaymusic.util.deserializer.AutogenEnumDeserializer;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;

import java.util.Optional;

public class ArtRef {
  @Expose
  private String url;
  @Expose
  private String aspectRatio;
  @Expose
  private Autogen autogen;

  public String getUrl() {
    return url;
  }

  public String getAspectRatio() {
    return aspectRatio;
  }

  public Autogen isAutogen() {
    return autogen;
  }

  @JsonAdapter(AutogenEnumDeserializer.class)
  public enum Autogen {
    TRUE,
    FALSE
  }

}
