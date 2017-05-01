package com.bndkpntr.antares.model;

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

    public List<ActivitiesContent> getContents() {
        List<ActivitiesContent> result = new ArrayList<>();

        for (ActivitiesContent content : contents) {
            if (content.type.equals("track") || content.type.equals("track-repost")) {
                Track normalizedTrack = Track.tryGetNormalizedTrack(content.track);
                if (normalizedTrack != null) {
                    result.add(content);
                }
            }
        }

        return result;
    }
}
