package com.cookandroid.reviewary;

public class Book {

    int _id;
    String image;
    String name;
    String author;
    String publisher;
    String genre;
    String rating;
    String readDate;
    String impressiveSentence;
    String review;

    public Book(int _id, String image, String name, String author, String publisher, String genre, String rating, String readDate,
                String impressiveSentence, String review) {
        this._id = _id;
        this.image = image;
        this.name = name;
        this.author = author;
        this.publisher = publisher;
        this.genre = genre;
        this.rating = rating;
        this.readDate = readDate;
        this.impressiveSentence = impressiveSentence;
        this.review = review;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getImage() { return image; }

    public void setImage(String image) { this.image = image; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReadDate() {
        return readDate;
    }

    public void setReadDate(String readDate) {
        this.readDate = readDate;
    }

    public String getImpressiveSentence() {
        return impressiveSentence;
    }

    public void setImpressiveSentence(String impressiveSentence) {
        this.impressiveSentence = impressiveSentence;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

}

