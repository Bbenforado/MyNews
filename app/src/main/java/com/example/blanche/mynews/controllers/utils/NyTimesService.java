package com.example.blanche.mynews.controllers.utils;

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

}
