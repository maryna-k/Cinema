package com.example.android.moviesapp.review;


public class Review {

    private String reviewId;
    private String author;
    private String reviewContent;

    public Review(String id, String author, String reviewContent) {
        this.reviewId = id;
        this.author = author;
        this.reviewContent = reviewContent;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public void setReviewContent(String content) {
        this.reviewContent = content;
    }

    public void setReviewId(String id) {
        this.reviewId = id;
    }

    public String getReviewId() {
        return reviewId;
    }

    @Override
    public String toString() {
        return "Review{" +
                "reviewId='" + reviewId + '\'' +
                ", author='" + author + '\'' +
                ", content='" + reviewContent + '\'' +
                '}';
    }
}
