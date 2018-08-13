package com.ryanpconnors.art_thief.database

/**
 * Created by Ryan Connors on 2/12/16.
 */
class ArtWorkDbSchema {

    /**
     * SQLite database table - Info
     */
    object InfoTable {

        /**
         * Name of this database table - info
         */
        val NAME = "info"

        /**
         * Columns in the database table Info
         */
        object Cols {
            val DATE_LAST_UPDATED = "date_last_updated"
            val SHOW_YEAR = "show_year"
            val DATA_VERSION = "data_version"
        }
    }

    /**
     * SQLite database table - SortArtwork
     */
    object SortArtworkTable {

        /**
         * Name of this database table - sort_artwork
         */
        val NAME = "sort_artwork"

        /**
         * Columns in the database table sort_artwork
         */
        object Cols {
            val RATING = "rating"
            val SORTED = "sorted"
        }
    }

    /**
     * SQLite database table - Artworks
     */
    object ArtWorkTable {

        /**
         * Name of this database table - artWorks
         */
        val NAME = "artWorks"

        /**
         * Database column identifiers - artworks
         */
        object Cols {
            val UUID = "uuid"
            val ART_THIEF_ID = "art_thief_id"
            val SHOW_ID = "show_id"
            val ORDERING = "ordering"
            val TITLE = "title"
            val ARTIST = "artist"
            val MEDIA = "media"
            val TAGS = "tags"
            val SMALL_IMAGE_URL = "small_image_url"
            val SMALL_IMAGE_PATH = "small_image_path"
            val LARGE_IMAGE_URL = "large_image_url"
            val LARGE_IMAGE_PATH = "large_image_path"
            val WIDTH = "width"
            val HEIGHT = "height"
            val STARS = "stars"
            val TAKEN = "taken"
        }
    }
}
