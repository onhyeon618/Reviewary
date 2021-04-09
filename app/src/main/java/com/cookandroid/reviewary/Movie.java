package com.cookandroid.reviewary;

public class Movie {

    int _id;
    String image;
    String name;
    String director;
    String actor;
    String genre;
    String rating;
    String date;
    String place;
    String impressiveSentence;
    String review;

    public Movie(int _id, String image, String name, String director, String actor, String genre,
                 String rating, String date, String place, String impressiveSentence, String review) {
        this._id = _id;
        this.image = image;
        this.name = name;
        this.director = director;
        this.actor = actor;
        this.genre = genre;
        this.rating = rating;
        this.date = date;
        this.place = place;
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

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
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

    public String getDate() {
        return date;
    }

    public void setDate(String readDate) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
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
