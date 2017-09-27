package com.alperencan.booklisting.android.model;

import android.graphics.Bitmap;

/**
 * A {@link Volume} object contains information related to a single book.
 */

public class Volume {

    /** Title of the book */
    private String title;

    /** Authors of the book */
    private String[] authors;

    /** URL string of the cover image for the book */
    private String coverImageUrl;

    /** Cover image of the book */
    private Bitmap coverImage;

    /**
     * Constructs a new {@link Volume} object.
     *
     * @param title is the title of the book
     * @param authors is the array of authors for the book
     * @param coverImageUrl is the URL string of the cover image for the book
     */
    public Volume(String title, String[] authors, String coverImageUrl) {
        this.title = title;
        this.authors = authors;
        this.coverImageUrl = coverImageUrl;
    }

    /**
     * @return the title of the book
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the array of authors for the book
     */
    public String[] getAuthors() {
        return authors;
    }

    /**
     * @return the URL string of the cover image for the book
     */
    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    /**
     * @return the cover image of the book
     */
    public Bitmap getCoverImage() {
        return coverImage;
    }

    /**
     * @param coverImage
     */
    public void setCoverImage(Bitmap coverImage) {
        this.coverImage = coverImage;
    }
}