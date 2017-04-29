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
                if (content.track.streamUrl != null && content.track.artworkUrl != null) {
                    if (!content.track.streamUrl.contains("client_id")) {
                        content.track.streamUrl += "?client_id=" + SoundCloudAPI.CLIENT_ID;
                    }
                    content.track.artworkUrl = content.track.artworkUrl.replace("large", "t500x500");
                    tracks.add(content.track);
                }
            }
        }

        return tracks;
    }
}
