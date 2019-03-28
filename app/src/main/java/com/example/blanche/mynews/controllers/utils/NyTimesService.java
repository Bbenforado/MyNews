package com.example.blanche.mynews.controllers.utils;

import com.example.blanche.mynews.models.MostPopular.MostPopular;
import com.example.blanche.mynews.models.SearchArticles.SearchArticleObject;
import com.example.blanche.mynews.models.TopStories.TopStories;

import io.reactivex.Observable;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NyTimesService {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.nytimes.com/svc/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();

    // TOP STORIES REQUEST
    @GET("topstories/v2/{section}.json?api-key=oGSGqG1zng1eifGyyR58BPpegvnm6Ubw")
    Observable<TopStories> getArticleDependingOnSection(@Path("section") String section);


    //MOST POPULAR REQUEST
    @GET("mostpopular/v2/viewed/{period}.json?api-key=Uk6MBw4ODa402XNeA3u2QKHAvJY5FbAY")
    Observable<MostPopular> getMostPopularArticleDependingOnPeriod(@Path("period") int period);

    //SEARCH ARTICLE REQUEST
  //  @GET("search/v2/articlesearch.json?")
  //  Observable<SearchArticleObject> getArticleBySearch(@Query("begin_date") String beginDate,
  //                                                     @Query("end_date") String endDate,
  //                                                     @Query("fq=news_desk") String category,
  //                                                     @Query("q") String keyword,
   //                                                    @Query("api_key") String apikey);

    @GET("articlesearch.json?")
    Observable<SearchArticleObject> getArticleBySearch (@Query("begin_date") String beginDate,
                                           @Query("end_date") String endDate,
                                           @Query("fq") String category,
                                           @Query("q") String keyword,
                                           @Query("sort") String sort,
                                           @Query("api-key") String apikey);
    public static final Retrofit retrofitSearch = new Retrofit.Builder()
            .baseUrl("https://api.nytimes.com/svc/search/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();


    @GET("articlesearch.json?")
    Observable<SearchArticleObject> getArticleBySearchDependingOnCategory (@Query("fq") String category,
                                                        @Query("sort") String sort,
                                                        @Query("api-key") String apikey);
    public static final Retrofit retrofitSearchArt = new Retrofit.Builder()
            .baseUrl("https://api.nytimes.com/svc/search/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();

    @GET("articlesearch.json?")
    Observable<SearchArticleObject> getArticleBySearchDependingOnCategoryAndKeyword (@Query("fq") String category,
                                                                           @Query("q") String keyword,
                                                                           @Query("sort") String sort,
                                                                           @Query("api-key") String apikey);
    public static final Retrofit retrofitSearchCategory = new Retrofit.Builder()
            .baseUrl("https://api.nytimes.com/svc/search/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
}
