package com.example.blanche.mynews.controllers.activities;

import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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

public class ArticlesByCategoryActivity extends AppCompatActivity {

    private List<SearchArticle> searchArticleList;
    private RecyclerViewAdapterThirdFragment adapter;
    private Disposable disposable;
    public static final String KEY_BUTTON = "key_button";
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
        executeHttpRequest(bundle.getString(KEY_BUTTON));
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
                executeHttpRequest(bundle.getString(KEY_BUTTON));
            }
        });
    }

    //----------------------
    //HTTP REQUEST RETROFIT & REACTIVE X
    //------------------------------------

    public void executeHttpRequest(String category) {
        this.disposable =
                ArticlesStreams.streamFetchSearchedArticleByCategory(category, "newest", "TL8pNgjOXgnrDvkaCjdUI0N2AIvOGdyS").subscribeWith(new DisposableObserver<SearchArticleObject>() {
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


}
