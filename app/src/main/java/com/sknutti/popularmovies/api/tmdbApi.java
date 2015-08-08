package com.sknutti.popularmovies.api;

import com.sknutti.popularmovies.model.Movie;
import com.sknutti.popularmovies.model.MovieList;
import com.sknutti.popularmovies.model.ReviewList;
import com.sknutti.popularmovies.model.TrailerList;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by sknutti on 8/3/15.
 */
public interface tmdbApi {

    // http://api.themoviedb.org/3/discover/movie
//    @GET("/discover/movie")
//    void getTopMovies(@Query("sort_by") String sort, @Query("api_key") String apiKey, Callback<MovieList> response);
    @GET("/discover/movie")
    void getTopMovies(@Query("api_key") String apiKey, Callback<MovieList> response);

    // http://api.themoviedb.org/3/movie/135397
    @GET("/movie/{id}")
    void getMovieDetails(@Path("id") int id, @Query("api_key") String apiKey, Callback<Movie> response);

    // http://api.themoviedb.org/3/movie/135397/videos
    @GET("/movie/{id}/videos")
    void getMovieTrailers(@Path("id") int id, @Query("api_key") String apiKey, Callback<TrailerList> response);

    // http://api.themoviedb.org/3/movie/135397/reviews
    @GET("/movie/{id}/reviews")
    void getMovieReviews(@Path("id") int id, @Query("api_key") String apiKey, Callback<ReviewList> response);
}
