package com.bndkpntr.antares.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Playlist {
    @SerializedName("id")
    public String id;

    @SerializedName("title")
    public String title;

    @SerializedName("artwork_url")
    public String artworkUrl;

    @SerializedName("tracks")
    public List<Track> tracks;

    public Playlist(String id, String title, String artworkUrl, List<Track> tracks) {
        this.id = id;
        this.title = title;
        this.artworkUrl = artworkUrl;
        this.tracks = tracks;
    }

    public static Playlist tryGetNormalizedPlaylist(Playlist playlist) {
        if (playlist != null) {
            if (playlist.artworkUrl == null) {
                if (playlist.tracks != null && playlist.tracks.size() > 0 && playlist.tracks.get(0).artworkUrl != null) {
                    playlist.artworkUrl = playlist.tracks.get(0).artworkUrl;
                } else {
                    return null;
                }
            }

            playlist.artworkUrl = playlist.artworkUrl.replace("large", "t500x500");
            return playlist;
        }

        return null;
    }

    public static Playlist tryGetNormalizedPlaylistAndTracks(Playlist playlist) {
        Playlist normalizedPlaylist = Playlist.tryGetNormalizedPlaylist(playlist);
        if (normalizedPlaylist != null) {
            for (int i = 0; i < normalizedPlaylist.tracks.size(); ++i) {
                Track track = Track.tryGetNormalizedTrack(normalizedPlaylist.tracks.get(i));
                if (track != null) {
                    normalizedPlaylist.tracks.set(i, track);
                } else {
                    return null;
                }
            }

            return normalizedPlaylist;
        }

        return null;
    }
}