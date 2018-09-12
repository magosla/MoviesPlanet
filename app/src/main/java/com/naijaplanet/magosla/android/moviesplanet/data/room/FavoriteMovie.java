package com.naijaplanet.magosla.android.moviesplanet.data.room;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "favorite_movies")
public class FavoriteMovie  {
    @PrimaryKey
    private int id;

    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    private String overview;
    @ColumnInfo(name = "release_date",typeAffinity = ColumnInfo.TEXT)
    private String releaseDate;
    @ColumnInfo(name = "original_title",typeAffinity = ColumnInfo.TEXT)
    private String originalTitle;
    @ColumnInfo(name = "title",typeAffinity = ColumnInfo.TEXT)
    private String title;
    @ColumnInfo(name = "backdrop_path",typeAffinity = ColumnInfo.TEXT)
    private String backdropPath;
    @ColumnInfo(name = "poster_path",typeAffinity = ColumnInfo.TEXT)
    private String posterPath;
    @ColumnInfo(name = "popularity", typeAffinity = ColumnInfo.REAL)
    private Double popularity;
    @ColumnInfo(name = "vote_count", typeAffinity = ColumnInfo.INTEGER)
    private int voteCount;
    @ColumnInfo(name = "vote_average", typeAffinity = ColumnInfo.REAL)
    private Double voteAverage;
    @ColumnInfo(name = "video", typeAffinity = ColumnInfo.INTEGER)
    private boolean video;
    @ColumnInfo(name = "adult", typeAffinity = ColumnInfo.INTEGER)
    private boolean adult;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }
    public boolean getVideo(){
        return video;
    }
    public void setVideo(boolean video){
        this.video = video;
    }
    public boolean getAdult(){
        return adult;
    }
    public void setAdult(boolean adult){
        this.adult = adult;
    }
}
