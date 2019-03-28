package com.example.blanche.mynews.controllers.utils;

import com.example.blanche.mynews.models.MostPopular.MostPopular;
import com.example.blanche.mynews.models.SearchArticles.SearchArticleObject;
import com.example.blanche.mynews.models.TopStories.TopStories;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class ArticlesStreams {

    public  static NyTimesService nyTimesService = NyTimesService.retrofit.create(NyTimesService.class);

    public static Observable<TopStories> streamFetchTopStoriesArticle(String section) {
        return nyTimesService.getArticleDependingOnSection(section)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static Observable<MostPopular> streamFetchMostPopularArticle(int period) {
        return nyTimesService.getMostPopularArticleDependingOnPeriod(period)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

   // public static Observable<SearchArticleObject> streamFetchSearchedArticle(String beginDate, String endDate, String category, String keyword, String apikey) {
     //   return nyTimesService.getArticleBySearch(beginDate, endDate, category, keyword, apikey)
     //           .subscribeOn(Schedulers.io())
     //           .observeOn(AndroidSchedulers.mainThread())
     //           .timeout(10, TimeUnit.SECONDS);
   // }

    public static Observable<SearchArticleObject> streamFetchSearchedArticle(String beginDate, String endDate, String category, String keyword, String sort, String apikey) {
        NyTimesService articleService = NyTimesService.retrofitSearch.create(NyTimesService.class);
        return articleService.getArticleBySearch(beginDate, endDate, category, keyword, sort, apikey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }


    public static Observable<SearchArticleObject> streamFetchSearchedArticleByCategory(String category, String sort, String apikey) {
        NyTimesService artArticleService = NyTimesService.retrofitSearchArt.create(NyTimesService.class);
        return artArticleService.getArticleBySearchDependingOnCategory(category, sort, apikey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static Observable<SearchArticleObject> streamFetchSearchedArticleByCategoryAndKeyWord(String category, String keyword, String sort, String apikey) {
        NyTimesService artArticleService = NyTimesService.retrofitSearchCategory.create(NyTimesService.class);
        return artArticleService.getArticleBySearchDependingOnCategoryAndKeyword(category, keyword, sort, apikey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }
}
