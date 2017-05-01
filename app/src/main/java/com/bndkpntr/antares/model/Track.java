package com.bndkpntr.antares.model;

import com.bndkpntr.antares.network.SoundCloudAPI;
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

    public static Track tryGetNormalizedTrack(Track track) {
        if (track != null && track.streamUrl != null && track.artworkUrl != null) {
            if (!track.streamUrl.contains(SoundCloudAPI.QueryParams.CLIENT_ID)) {
                track.streamUrl += "?" + SoundCloudAPI.QueryParams.CLIENT_ID + "=" + SoundCloudAPI.CLIENT_ID;
            }
            track.artworkUrl = track.artworkUrl.replace("large", "t500x500");
            return track;
        }

        return null;
    }
}
