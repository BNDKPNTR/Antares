package com.bndkpntr.antares.model;

import com.google.gson.annotations.SerializedName;

public class ActivitiesContent {
    @SerializedName("origin")
    public Track track;

    @SerializedName("tags")
    public String tags;

    @SerializedName("created_at")
    public String createdAt;

    @SerializedName("type")
    public String type;
}
