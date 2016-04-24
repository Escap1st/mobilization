package com.yandexmobilization.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.OnJsonParseComplete;
import com.bluelinelabs.logansquare.annotation.OnPreJsonSerialize;

import java.io.Serializable;

//ссылки на изображения
@JsonObject
public class Cover implements Serializable{

    @JsonField
    String small;

    @JsonField
    String big;

    @OnJsonParseComplete
    void onParseComplete() {

    }

    @OnPreJsonSerialize
    void onPreSerialize() {

    }

    public String getSmall() {
        return small;
    }

    public String getBig() {
        return big;
    }
}
