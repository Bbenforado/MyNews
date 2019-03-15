package com.example.blanche.mynews.controllers.utils;

import android.content.Context;
import android.content.Intent;

import com.example.blanche.mynews.models.MostPopular;
import com.example.blanche.mynews.models.TopStories.TopStories;

import io.reactivex.Observable;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface NyTimesService {

    // TOP STORIES REQUEST
    @GET("{section}.json?api-key=oGSGqG1zng1eifGyyR58BPpegvnm6Ubw")
    Observable<TopStories> getArticleDependingOnSection(@Path("section") String section);
    public static final Retrofit topStoriesRetrofit = new Retrofit.Builder()
            .baseUrl("https://api.nytimes.com/svc/topstories/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();

    //MOST POPULAR REQUEST
    @GET("{period}.json?api-key=Uk6MBw4ODa402XNeA3u2QKHAvJY5FbAY")
    Observable<MostPopular> getMostPopularArticleDependingOnPeriod(@Path("period") int period);
    public static final Retrofit mostPopularRetrofit = new Retrofit.Builder()
            .baseUrl("https://api.nytimes.com/svc/mostpopular/v2/viewed/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
}
