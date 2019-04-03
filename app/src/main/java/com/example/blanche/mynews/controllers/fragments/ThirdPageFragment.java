package com.example.blanche.mynews.controllers.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.blanche.mynews.R;
import com.example.blanche.mynews.controllers.activities.WebviewActivity;
import com.example.blanche.mynews.controllers.adapters.RecyclerViewAdapterSecondFragment;
import com.example.blanche.mynews.controllers.adapters.RecyclerViewAdapterThirdFragment;
import com.example.blanche.mynews.controllers.utils.ArticlesStreams;
import com.example.blanche.mynews.controllers.utils.ItemClickSupport;
import com.example.blanche.mynews.models.MostPopular.MostPopular;
import com.example.blanche.mynews.models.MostPopular.MostPopularResult;
import com.example.blanche.mynews.models.SearchArticles.SearchArticle;
import com.example.blanche.mynews.models.SearchArticles.SearchArticleObject;
import com.example.blanche.mynews.models.TopStories.TopStoriesResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

/**
 * A simple {@link Fragment} subclass.
 */
public class ThirdPageFragment extends Fragment {

    public static final String KEY_POSITION = "position";
    public static final String KEY_ARTICLE = "key_article";
    public static final String ARTICLE_TITLE = "article_title";

    @BindView(R.id.fragment_third_second_page_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.fragment_third_page_swipe_container) SwipeRefreshLayout swipeRefreshLayout;

    private List<SearchArticle> searchArticleList;
    private RecyclerViewAdapterThirdFragment adapter;
    private Disposable disposable;
    Bundle bundle;

    //CONSTRUCTOR
    public ThirdPageFragment() {
        // Required empty public constructor
    }

    public static ThirdPageFragment newInstance(int position) {
        ThirdPageFragment fragment = new ThirdPageFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_third_page, container, false);
        int position = getArguments().getInt(KEY_POSITION, -1);
        ButterKnife.bind(this, result);
        configureRecyclerView();
        configureSwipeRefreshLayout();
        executeHttpRequestSearchArticle();
        configureOnClickRecyclerView();
        return result;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposeWhenDestroy();
    }

    //------------------------
    //CONFIGURATION
    //-------------------------
    private void configureRecyclerView() {
        this.searchArticleList = new ArrayList<>();
        this.adapter = new RecyclerViewAdapterThirdFragment(this.searchArticleList, Glide.with(this));
        this.recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void configureSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                executeHttpRequestSearchArticle();
            }
        });
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
                        Intent webviewActivity = new Intent(getContext(), WebviewActivity.class);
                        webviewActivity.putExtras(bundle);
                        startActivity(webviewActivity);
                    }
                });
    }

    //----------------------------
    //HTTP REQUEST RETROFIT + REACTIVE X
    //-----------------------------------------

    public void executeHttpRequestSearchArticle() {
        this.disposable =
                ArticlesStreams.streamFetchSearchedArticle(null, null, "arts", null,"newest",  "TL8pNgjOXgnrDvkaCjdUI0N2AIvOGdyS")
                        .subscribeWith(new DisposableObserver<SearchArticleObject>() {

                    @Override
                    public void onNext(SearchArticleObject searchArticleObject) {
                        Log.e("TAG", "on next");
                       updateUISearchArticle(searchArticleObject.getResponse().getArticles());
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

    //-----------------
    private void disposeWhenDestroy() {
        if(this.disposable != null && !this.disposable.isDisposed()) {
            this.disposable.dispose();
        }
    }

    //----------------------
    //UPDATE UI
    //---------------------

    private void updateUISearchArticle(List<SearchArticle> results) {
        swipeRefreshLayout.setRefreshing(false);
        searchArticleList.clear();
        searchArticleList.addAll(results);
        adapter.notifyDataSetChanged();
    }

}
