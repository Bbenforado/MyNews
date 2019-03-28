package com.example.blanche.mynews.controllers.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.blanche.mynews.controllers.adapters.RecyclerViewAdapterThirdFragment;
import com.example.blanche.mynews.models.SearchArticles.SearchArticle;
import com.example.blanche.mynews.models.SearchArticles.SearchArticleObject;
import com.example.blanche.mynews.models.SearchArticles.SearchArticleResponse;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class MyAlarmReceiver extends BroadcastReceiver {

    Bundle bundle;
    public static final String ARTS_NOTIFICATION = "arts";
    public static final String POLITICS_NOTIFICATION = "politics";
    public static final String BUSINESS_NOTIFICATION = "business";
    public static final String SPORTS_NOTIFICATION = "sports";
    public static final String ENTREPRENEURS_NOTIFICATION = "entrepreneurs";
    public static final String TRAVEL_NOTIFICATION = "travel";
    public static final String KEYWORD_NOTIFICATION = "keyword";
    public static final String APP_PREFERENCES = "appPreferences";
    SharedPreferences preferences;
    private Disposable disposable;
    int size;


    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("it's working");
        preferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        String keyword = preferences.getString(KEYWORD_NOTIFICATION, null);
        System.out.println("keyword ===== " + keyword);
        String categories = getCategories();
        System.out.println("categories == " + categories);

        if (keyword == null || categories == null) {
            size = 0;
            System.out.println("print size here: " + size);

        }
        executeHttpRequestSearchArticle(context, categories, keyword);
    }


    public void executeHttpRequestSearchArticle(final Context context, String category, String keyword) {
        this.disposable =
                ArticlesStreams.streamFetchSearchedArticleByCategoryAndKeyWord(category, keyword, "newest",  "TL8pNgjOXgnrDvkaCjdUI0N2AIvOGdyS")
                        .subscribeWith(new DisposableObserver<SearchArticleObject>() {

                            @Override
                            public void onNext(SearchArticleObject searchArticleObject) {
                                size = 0;
                                Log.e("TAG", "on next");
                                SearchArticleResponse searchArticleResponse = searchArticleObject.getResponse();

                                size = searchArticleResponse.getArticles().size();
                                System.out.println("size = " + size);
                                displayToastMessage(context, size);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("TAG", Log.getStackTraceString(e));
                            }

                            @Override
                            public void onComplete() {
                                Log.e("TAG", "on complete");
                            }
                        });
    }

    private void displayToastMessage(Context context, int size) {
        if (size >0) {
            Toast.makeText(context, "We found " + size + " articles!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "No articles found! :(", Toast.LENGTH_SHORT).show();
        }
    }



    private String getCategories() {
        StringBuilder stringBuilder = new StringBuilder();
        if (preferences.getString(ARTS_NOTIFICATION, null) != null) {
            stringBuilder.append(" " + '"' + preferences.getString(ARTS_NOTIFICATION, null) + '"');
        }
        if (preferences.getString(POLITICS_NOTIFICATION, null) != null) {
            stringBuilder.append(" " + '"' + preferences.getString(POLITICS_NOTIFICATION, null) + '"');
        }
        if (preferences.getString(BUSINESS_NOTIFICATION, null) != null) {
            stringBuilder.append(" " + '"' + preferences.getString(BUSINESS_NOTIFICATION, null) + '"');
        }
        if (preferences.getString(SPORTS_NOTIFICATION, null) != null) {
            stringBuilder.append(" " + '"' + preferences.getString(SPORTS_NOTIFICATION, null) + '"');
        }
        if (preferences.getString(ENTREPRENEURS_NOTIFICATION, null)!= null) {
            stringBuilder.append(" " + '"' + preferences.getString(ENTREPRENEURS_NOTIFICATION, null) + '"');
        }
        if (preferences.getString(TRAVEL_NOTIFICATION, null) != null) {
            stringBuilder.append(" " + '"' + preferences.getString(TRAVEL_NOTIFICATION, null) + '"');
        }
        String categories = stringBuilder.toString();
        return categories;
    }



}
