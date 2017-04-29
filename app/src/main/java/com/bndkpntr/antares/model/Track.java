package com.bndkpntr.antares.model;

import com.google.gson.annotations.SerializedName;

public class Track {
    @SerializedName("id")
    public String id;

    @SerializedName("title")
    public String title;

    @SerializedName("stream_url")
    public String streamUrl;

    @SerializedName("artwork_url")
    public String artworkUrl;

    public Track(String id, String title, String streamUrl, String artworkUrl) {
        this.id = id;
        this.title = title;
        this.streamUrl = streamUrl;
        this.artworkUrl = artworkUrl;
    }
}
