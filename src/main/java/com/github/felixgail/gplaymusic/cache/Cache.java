package com.github.felixgail.gplaymusic.cache;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class Cache<T> {
  private List<T> cache = new LinkedList<>();
  private boolean ready = false;
  private boolean useCache = true;


  public Cache(boolean initialize) throws IOException {
    if (initialize) {
      initialize();
    }
  }

  public Cache() {
  }

  public abstract void update() throws IOException;

  protected void setCache(List<T> newCache) {
    this.cache = Collections.synchronizedList(newCache);
  }

  public void add(Collection<T> items) {
    this.cache.addAll(items);
  }

  public void add(T item) {
    this.cache.add(item);
  }

  public void remove(T item) {
    this.cache.remove(item);
  }

  public void remove(Collection<T> items) {
    this.cache.removeAll(items);
  }

  protected List<T> getCurrentCache() {
    return cache;
  }

  public void setUseCache(boolean useCache) {
    this.useCache = useCache;
    if (!useCache) {
      ready = false;
    }
  }

  public boolean isUseCache() {
    return useCache;
  }

  public List<T> getAll() throws IOException {
    initialize();
    return cache;
  }

  public List<T> get() throws IOException {
    initialize();
    return cache;
  }

  public void initialize() throws IOException {
    if (!ready) {
      update();
      if (useCache) {
        ready = true;
      }
    }
  }


}
