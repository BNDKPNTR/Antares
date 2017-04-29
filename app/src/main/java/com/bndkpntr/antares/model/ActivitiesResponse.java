package com.bndkpntr.antares.model;

import com.bndkpntr.antares.network.SoundCloudAPI;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ActivitiesResponse {
    @SerializedName("collection")
    public List<ActivitiesContent> contents;

    @SerializedName("next_href")
    public String nextHref;

    @SerializedName("future_href")
    public String futureHref;

    public List<Track> getTracks() {
        List<Track> tracks = new ArrayList<>();

        for (ActivitiesContent content : contents) {
            if (content.type.equals("track") || content.type.equals("track-repost")) {
                content.track.streamUrl += "?client_id=" + SoundCloudAPI.CLIENT_ID;
                tracks.add(content.track);
            }
        }

        return tracks;
    }
}
