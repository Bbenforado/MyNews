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
import com.example.blanche.mynews.controllers.adapters.RecyclerViewAdapter;
import com.example.blanche.mynews.controllers.utils.ArticlesStreams;
import com.example.blanche.mynews.controllers.utils.ItemClickSupport;
import com.example.blanche.mynews.models.TopStories.TopStories;
import com.example.blanche.mynews.models.TopStories.TopStoriesMultimedia;
import com.example.blanche.mynews.models.TopStories.TopStoriesResult;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class PageFragment extends Fragment {

    public static final String KEY_POSITION = "position";
    public static final String KEY_ARTICLE = "key_article";
    public static final String ARTICLE_TITLE = "article_title";

    @BindView(R.id.fragment_page_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.fragment_page_swipe_container) SwipeRefreshLayout swipeRefreshLayout;
    private List<TopStoriesResult> topStoriesResultList;
    private List<TopStoriesMultimedia> topStoriesMultimedia;
    private RecyclerViewAdapter adapter;
    private Disposable disposable;
    Bundle bundle;

    //CONSTRUCTOR
    public PageFragment() {
        // Required empty public constructor
    }

    public static PageFragment newInstance(int position) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            View result = inflater.inflate(R.layout.fragment_page, container, false);
            int position = getArguments().getInt(KEY_POSITION, -1);
            ButterKnife.bind(this, result);

            configureRecyclerView();
            configureSwipeRefreshLayout();
            executeHttpRequestTopStories();
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
        this.topStoriesResultList = new ArrayList<>();
        this.adapter = new RecyclerViewAdapter(this.topStoriesResultList, Glide.with(this));
        this.recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void configureSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                executeHttpRequestTopStories();
            }
        });
    }

    private void configureOnClickRecyclerView() {
        ItemClickSupport.addTo(recyclerView, R.layout.fragment_page_item)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        TopStoriesResult article = adapter.getArticle(position);
                        //LAUNCH WEBVIEW ACTIVITY
                        bundle = new Bundle();
                        bundle.putString(KEY_ARTICLE, article.getUrl());
                        bundle.putString(ARTICLE_TITLE, article.getTitle());
                        Intent webviewActivity = new Intent(getContext(), WebviewActivity.class);
                        webviewActivity.putExtras(bundle);
                        startActivity(webviewActivity);
                    }
                });
    }
    //----------------------------
    //HTTP REQUEST RETROFIT + REACTIVE X
    //-----------------------------------------

    public void executeHttpRequestTopStories() {
        this.disposable =
                ArticlesStreams.streamFetchTopStoriesArticle("home").subscribeWith(new DisposableObserver<TopStories>() {
                    @Override
                    public void onNext(TopStories topStories) {
                        Log.e("TAG", "on nextTop");
                        updateUITopStories(topStories.getResults());
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
    //-----------------
    private void disposeWhenDestroy() {
        if(this.disposable != null && !this.disposable.isDisposed()) {
            this.disposable.dispose();
        }
    }
    //----------------------
    //UPDATE UI
    //---------------------
    private void updateUITopStories(List<TopStoriesResult> results) {
        swipeRefreshLayout.setRefreshing(false);
        topStoriesResultList.clear();
        topStoriesResultList.addAll(results);
        adapter.notifyDataSetChanged();
    }
    //----------------------

}
