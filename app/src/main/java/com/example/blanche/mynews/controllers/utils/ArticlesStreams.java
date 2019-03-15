package com.example.blanche.mynews.controllers.utils;

import com.example.blanche.mynews.models.MostPopular;
import com.example.blanche.mynews.models.TopStories.TopStories;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class ArticlesStreams {

    public static Observable<TopStories> streamFetchTopStoriesArticle(String section) {
        NyTimesService nyTimesService = NyTimesService.topStoriesRetrofit.create(NyTimesService.class);
        return nyTimesService.getArticleDependingOnSection(section)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static Observable<MostPopular> streamFetchMostPopularArticle(int period) {
        NyTimesService nyTimesService = NyTimesService.mostPopularRetrofit.create(NyTimesService.class);
        return nyTimesService.getMostPopularArticleDependingOnPeriod(period)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

}
