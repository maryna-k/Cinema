package com.example.android.moviesapp.review;


import java.io.Serializable;

public class Review implements Serializable{

    private String reviewId;
    private String author;
    private String reviewContent;

    public Review(){}

    public Review(String id, String author, String reviewContent) {
        this.reviewId = id;
        this.author = author;
        this.reviewContent = reviewContent;
    }

    public Review(Review original){
        reviewId = original.reviewId;
        author = original.author;
        reviewContent = original.reviewContent;
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
