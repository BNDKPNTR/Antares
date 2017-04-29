package com.bndkpntr.antares.db;

final class DbConstants {
    public static final String DATABASE_NAME = "data.db";
    public static final int DATABASE_VERSION = 1;
    public static String DATABASE_CREATE_ALL = Tracks.DATABASE_CREATE;
    public static String DATABASE_DROP_ALL = Tracks.DATABASE_DROP;

    static class Tracks {
        static final String DATABASE_TABLE = "tracks";
        static final String KEY_ROWID = "_id";
        static final String KEY_TITLE = "title";
        static final String KEY_STREAM_URL = "streamUrl";
        static final String KEY_ARTWORK_URL = "artworkUrl";

        static final String DATABASE_CREATE =
                "create table if not exists " + DATABASE_TABLE + " ( "
                        + KEY_ROWID + " integer primary key autoincrement, "
                        + KEY_TITLE + " text not null, "
                        + KEY_STREAM_URL + " text, "
                        + KEY_ARTWORK_URL + " text"
                        + "); ";

        static final String DATABASE_DROP =
                "drop table if exists " + DATABASE_TABLE + "; ";
    }
}
