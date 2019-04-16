package com.example.blanche.mynews.controllers.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.blanche.mynews.R;
import com.example.blanche.mynews.controllers.adapters.RecyclerViewAdapterThirdFragment;
import com.example.blanche.mynews.controllers.utils.ArticlesStreams;
import com.example.blanche.mynews.controllers.utils.ItemClickSupport;
import com.example.blanche.mynews.models.SearchArticles.SearchArticle;
import com.example.blanche.mynews.models.SearchArticles.SearchArticleObject;
import com.example.blanche.mynews.models.SearchArticles.SearchArticleResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

/**
 * ArticlesByCategoryActivity is an activity that displays the articles, depending on which category the user clicked on,
 * in the navigation drawer
 */
public class ArticlesByCategoryActivity extends AppCompatActivity {

    private List<SearchArticle> searchArticleList;
    private RecyclerViewAdapterThirdFragment adapter;
    public static final String KEY_BUTTON = "key_button";
    public static final String KEY_ARTICLE = "key_article";
    public static final String ARTICLE_TITLE = "article_title";
    private Bundle bundle;

    @BindView(R.id.activity_art_articles_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.activity_art_articles_swipe_container) SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_by_articles);

        ButterKnife.bind(this);
        configureRecyclerView();
        configureSwipeRefreshLayout();
        bundle = getIntent().getExtras();
        configureToolbar();
        executeHttpRequest(null, null, bundle.getString(KEY_BUTTON), null);
        configureOnClickRecyclerView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setPreferencesToNull();
    }

    //---------------
    //CONFIGURATION
    //------------------
    private void configureRecyclerView() {
        this.searchArticleList = new ArrayList<>();
        this.adapter = new RecyclerViewAdapterThirdFragment(this.searchArticleList, Glide.with(this));
        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void configureSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                executeHttpRequest(null, null, bundle.getString(KEY_BUTTON), null);
            }
        });
    }

    private void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //get a support actionbar corresponding to this toolbar
        ActionBar actionBar = getSupportActionBar();
        //enable the up button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(setFirstLetterUppercase(bundle.getString(KEY_BUTTON)));
    }

    private void configureOnClickRecyclerView() {
        ItemClickSupport.addTo(recyclerView, R.layout.fragment_page_item)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        SearchArticle article = adapter.getArticle(position);
                        //LAUNCH WEBVIEW ACTIVITY
                        bundle = new Bundle();
                        bundle.putString(KEY_ARTICLE, article.getWebUrl());
                        bundle.putString(ARTICLE_TITLE, article.getHeadline().getMain());
                        Intent webviewActivity = new Intent(getApplicationContext(), WebviewActivity.class);
                        webviewActivity.putExtras(bundle);
                        startActivity(webviewActivity);
                    }
                });
    }

    //----------------------
    //HTTP REQUEST RETROFIT & REACTIVE X
    //------------------------------------

    /**
     * go get articles for the given params
     * @param beginDate
     * @param endDate
     * @param category
     * @param keyword
     */
    private void executeHttpRequest(String beginDate, String endDate, String category, String keyword) {
        Disposable disposable = ArticlesStreams.streamFetchSearchedArticle(beginDate, endDate, category, keyword, "newest", "TL8pNgjOXgnrDvkaCjdUI0N2AIvOGdyS").subscribeWith(new DisposableObserver<SearchArticleObject>() {
            @Override
            public void onNext(SearchArticleObject searchArticleObject) {
                Log.e("TAG", "on nextTop");
                SearchArticleResponse response = searchArticleObject.getResponse();
                updateUIWithArtArticles(response.getArticles());
            }

            @Override
            public void onError(Throwable e) {
                Log.e("TAG", "erreur");
            }

            @Override
            public void onComplete() {
                Log.e("TAG", "on complete");
            }
        });
    }

    //-------------------------
    //UPDATE UI
    //-----------------------
    private void updateUIWithArtArticles(List<SearchArticle> searchArticleList) {
        swipeRefreshLayout.setRefreshing(false);
        this.searchArticleList.clear();
        this.searchArticleList.addAll(searchArticleList);
        adapter.notifyDataSetChanged();
    }

    //--------------------
    private void setPreferencesToNull() {
        bundle.putString(KEY_BUTTON, null);
    }

    public String setFirstLetterUppercase(String string) {
        String result = string.substring(0, 1).toUpperCase() + string.substring(1);
        return result;
    }
}
