package com.bndkpntr.antares.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bndkpntr.antares.network.SoundCloudAPI;
import com.google.gson.annotations.SerializedName;

public class Track implements Parcelable {
    @SerializedName("id")
    public String id;

    @SerializedName("title")
    public String title;

    @SerializedName("stream_url")
    public String streamUrl;

    @SerializedName("artwork_url")
    public String artworkUrl;

    @SerializedName("duration")
    public int duration;

    public Track(String id, String title, String streamUrl, String artworkUrl, int duration) {
        this.id = id;
        this.title = title;
        this.streamUrl = streamUrl;
        this.artworkUrl = artworkUrl;
        this.duration = duration;
    }

    protected Track(Parcel in) {
        id = in.readString();
        title = in.readString();
        streamUrl = in.readString();
        artworkUrl = in.readString();
        duration = in.readInt();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(streamUrl);
        dest.writeString(artworkUrl);
        dest.writeInt(duration);
    }

    public static final Creator<Track> CREATOR = new Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };
}
