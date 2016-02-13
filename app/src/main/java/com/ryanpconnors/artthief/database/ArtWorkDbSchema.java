package com.ryanpconnors.artthief.database;

/**
 * Created by Ryan Connors on 2/12/16.
 */
public class ArtWorkDbSchema {

    public static final class InfoTable {

        public static final String NAME = "info";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String SHOW_YEAR = "show_year";
            public static final String DATA_VERSION = "data_version";
        }

    }

    public static final class ArtWorkTable {

        public static final String NAME = "artWorks";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String ART_THIEF_ID = "art_thief_id";
            public static final String SHOW_ID = "show_id";
            public static final String TITLE = "title";
            public static final String ARTIST = "artist";
            public static final String MEDIA = "media";
            public static final String TAGS = "tags";
            public static final String LARGE_IMAGE_URL = "large_image_url";
            public static final String SMALL_IMAGE_URL = "small_image_url";
            public static final String LARGE_IMAGE = "large_image";
            public static final String SMALL_IMAGE = "small_image";
            public static final String STARS = "stars";
            public static final String TAKEN = "taken";
        }
    }
}
