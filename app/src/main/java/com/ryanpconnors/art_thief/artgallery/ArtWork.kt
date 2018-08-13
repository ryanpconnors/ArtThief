package com.ryanpconnors.art_thief.artgallery

import java.util.UUID

/**
 * Created by Ryan Connors on 2/9/16.
 */
class ArtWork @JvmOverloads constructor(val id: UUID = UUID.randomUUID()) : Comparable<ArtWork> {

    var artThiefID: Int = 0
    var showId: String? = null
    var ordering: Int = 0

    var title: String? = null
    var artist: String? = null
    var media: String? = null
    var tags: String? = null

    var smallImageUrl: String? = null
    var largeImageUrl: String? = null

    var smallImagePath: String? = null
    var largeImagePath: String? = null

    var width: String? = null
    var height: String? = null

    var stars: Int = 0
        set(stars) = if (stars < 0 || stars > 5) {
            throw RuntimeException("Invalid number of ArtWork stars [ $stars ]")
        } else {
            field = stars
        }
    var isTaken: Boolean = false

    fun swapOrder(artWork: ArtWork) {
        val order = this.ordering
        this.ordering = artWork.ordering
        artWork.ordering = order
    }


    /**
     * @param obj Object to compare to this ArtWork object for equality.
     * @return false if obj is not an instance of the class ArtWork.
     * Returns true iff the given object shares the same artThiefId, showId,
     * title, artist, media, tags, smallImageUrl and largeImageUrl.
     * Otherwise returns false.
     *
     *
     * Note: The method does not take into account the 'uuid', 'smallImagePath', 'largeImagePath',
     * 'stars', 'taken' and 'ordering' fields.
     */
    override fun equals(obj: Any?): Boolean {

        if (obj !is ArtWork) {
            return false
        }
        val artWork = obj as ArtWork?
        return this.artThiefID == artWork!!.artThiefID &&
                this.showId == artWork.showId &&
                this.title == artWork.title &&
                this.artist == artWork.artist &&
                this.media == artWork.media &&
                this.tags == artWork.tags &&
                this.smallImageUrl == artWork.smallImageUrl &&
                this.largeImageUrl == artWork.largeImageUrl &&
                this.width == artWork.width &&
                this.height == artWork.height
    }

    /**
     * Determines whether this ArtWork object is ordered before or after the given ArtWork.
     *
     * @param @NotNull that - an ArtWork object.
     * @return the difference between this ArtWorks stars and obj
     * or in the case that the number of stars is equal, return the difference in ordering.
     */
    override fun compareTo(that: ArtWork): Int {
        return if (this.stars == that.stars) {
            this.ordering - that.ordering
        } else {
            this.stars - that.stars
        }
    }
}
