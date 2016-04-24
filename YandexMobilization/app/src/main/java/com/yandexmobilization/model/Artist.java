package com.yandexmobilization.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.OnJsonParseComplete;
import com.bluelinelabs.logansquare.annotation.OnPreJsonSerialize;

import java.io.Serializable;
import java.util.List;

//информация об артисте
@JsonObject
public class Artist implements Serializable {

    @JsonField
    long id;

    @JsonField
    String name;

    @JsonField
    List<String> genres;

    @JsonField
    long tracks;

    @JsonField
    long albums;

    @JsonField
    String link;

    @JsonField
    String description;

    @JsonField
    Cover cover;

    @OnJsonParseComplete
    void onParseComplete() {

    }

    @OnPreJsonSerialize
    void onPreSerialize() {

    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getGenres() {
        return genres;
    }

    public long getTracks() {
        return tracks;
    }

    public long getAlbums() {
        return albums;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public Cover getCover() {
        return cover;
    }
}
