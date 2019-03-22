package com.example.blanche.mynews.controllers.activities;

import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    private SharedPreferences preferences;
    private List<SearchArticle> searchArticleList;
    private RecyclerViewAdapterThirdFragment adapter;
    @BindView(R.id.activity_search_articles_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.activity_search_articles_swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    public static final String KEYWORD = "keyword";
    public static final String BEGIN_DATE = "begin_date";
    public static final String END_DATE = "end_date";
    public static final String APP_PREFERENCES = "appPreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_articles);

        ButterKnife.bind(this);
        configureRecyclerView();

        preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        String category = preferences.getString(KEYWORD, null);
        executeHttpRequest(category);
        configureSwipeRefreshLayout(category);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setPreferencesToNull();
    }

    //----------------------------
    //CONFIGURATION
    //----------------------------
    private void configureRecyclerView() {
        this.searchArticleList = new ArrayList<>();
        this.adapter = new RecyclerViewAdapterThirdFragment(this.searchArticleList, Glide.with(this));
        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void configureSwipeRefreshLayout(final String category) {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                executeHttpRequest(category);
            }
        });
    }

    //--------------------
    //HTTP REQUEST
    //-------------------------
    private void executeHttpRequest(String category) {
        this.disposable =
                ArticlesStreams.streamFetchSearchedArticleByCategory(category, "newest", "TL8pNgjOXgnrDvkaCjdUI0N2AIvOGdyS").subscribeWith(new DisposableObserver<SearchArticleObject>() {
                    @Override
                    public void onNext(SearchArticleObject searchArticleObject) {
                        Log.e("TAG", "on nextTop");
                        SearchArticleResponse response = searchArticleObject.getResponse();
                        updateUIWithArticles(response.getArticles());
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

    private void executeHttpRequestWithDates() {
        this.disposable =
                ArticlesStreams.streamFetchSearchedArticle("", "","arts", "","newest", "TL8pNgjOXgnrDvkaCjdUI0N2AIvOGdyS").subscribeWith(new DisposableObserver<SearchArticleObject>() {
                    @Override
                    public void onNext(SearchArticleObject searchArticleObject) {
                        Log.e("TAG", "on nextTop");
                        SearchArticleResponse response = searchArticleObject.getResponse();
                        updateUIWithArticles(response.getArticles());
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
    private void setPreferencesToNull() {
        preferences.edit().putString(KEYWORD, null).apply();
    }
}
