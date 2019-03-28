package com.example.blanche.mynews.controllers.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.blanche.mynews.R;
import com.example.blanche.mynews.controllers.adapters.RecyclerViewAdapterThirdFragment;
import com.example.blanche.mynews.controllers.utils.ArticlesStreams;
import com.example.blanche.mynews.models.SearchArticles.SearchArticle;
import com.example.blanche.mynews.models.SearchArticles.SearchArticleObject;
import com.example.blanche.mynews.models.SearchArticles.SearchArticleResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class SearchArticlesActivity extends AppCompatActivity {

    private Disposable disposable;
    private List<SearchArticle> searchArticleList;
    private RecyclerViewAdapterThirdFragment adapter;

    @BindView(R.id.activity_search_articles_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.activity_search_articles_swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    public static final String KEYWORD = "keyword";
    public static final String BEGIN_DATE = "begin_date";
    public static final String END_DATE = "end_date";
    public static final String ARTS = "arts";
    public static final String POLITICS = "politics";
    public static final String BUSINESS = "business";
    public static final String SPORTS = "sports";
    public static final String ENTREPRENEURS = "entrepreneurs";
    public static final String TRAVEL = "travel";
    public static final String APP_PREFERENCES = "appPreferences";

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_articles);

        ButterKnife.bind(this);
        configureRecyclerView();
        configureToolbar();
        preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        String category = getCategories();
        String keyword = preferences.getString(KEYWORD, null);
        String beginDate = preferences.getString(BEGIN_DATE, null);
        String endDate = preferences.getString(END_DATE, null);

        executeHttpRequestWithDates(beginDate, endDate, keyword, category);
        configureSwipeRefreshLayout(beginDate, endDate, keyword, category);
    }

    //----------------------------
    //CONFIGURATION
    //----------------------------

    private void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
        //get a support actionbar corresponding to this toolbar
        ActionBar actionBar = getSupportActionBar();
        //enable the up button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Search");
    }

    private void configureRecyclerView() {
        this.searchArticleList = new ArrayList<>();
        this.adapter = new RecyclerViewAdapterThirdFragment(this.searchArticleList, Glide.with(this));
        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void configureSwipeRefreshLayout(final  String beginDate, final String endDate, final String keyword, final String category) {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                executeHttpRequestWithDates(beginDate, endDate, keyword, category);
            }
        });
    }

    //--------------------
    //HTTP REQUESTS
    //-------------------------
    private void executeHttpRequestWithDates(String beginDate, String endDate, String category, String keyword) {
        this.disposable =
                ArticlesStreams.streamFetchSearchedArticle(beginDate, endDate, category, keyword,"newest", "TL8pNgjOXgnrDvkaCjdUI0N2AIvOGdyS").subscribeWith(new DisposableObserver<SearchArticleObject>() {
                    @Override
                    public void onNext(SearchArticleObject searchArticleObject) {
                        Log.e("TAG", "on nextTop");
                        SearchArticleResponse response = searchArticleObject.getResponse();
                        updateUIWithArticles(response.getArticles());
                        if (response.getArticles().size() == 0) {
                            displayAlertDialog();
                        }

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

    //--------------------------
    //UPDATE UI
    //----------------------------
    private void updateUIWithArticles(List<SearchArticle> results) {
        swipeRefreshLayout.setRefreshing(false);
        this.searchArticleList.clear();
        this.searchArticleList.addAll(results);
        adapter.notifyDataSetChanged();
    }

    //-----------------------
    private String getCategories() {
        StringBuilder stringBuilder = new StringBuilder();
        if (preferences.getString(ARTS, null) != null) {
            stringBuilder.append(" " +'"' + preferences.getString(ARTS, null) + '"');
        }
        if (preferences.getString(POLITICS, null) != null) {
            stringBuilder.append(" " +'"' + preferences.getString(POLITICS, null) + '"');
        }
        if (preferences.getString(BUSINESS, null) != null) {
            stringBuilder.append(" " +'"' + preferences.getString(BUSINESS, null) + '"');
        }
        if (preferences.getString(SPORTS, null) != null) {
            stringBuilder.append(" " +'"' + preferences.getString(SPORTS, null) + '"');
        }
        if (preferences.getString(ENTREPRENEURS, null) != null) {
            stringBuilder.append(" " +'"' + preferences.getString(ENTREPRENEURS, null) + '"');
        }
        if (preferences.getString(TRAVEL, null) != null) {
            stringBuilder.append(" " +'"' + preferences.getString(TRAVEL, null) + '"');
        }
        String categories = stringBuilder.toString();
        return categories;
    }

    private void displayAlertDialog() {
        new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle("No Article found...")
                .setMessage("Please, try again with other filters :)")
                .setNegativeButton("Got it", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent searchActivity = new Intent(getApplicationContext(), SearchActivity.class);
                        startActivity(searchActivity);
                    }
                })
                .setIcon(R.drawable.ic_error)
                .show();
    }
}
