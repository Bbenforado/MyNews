package com.example.blanche.mynews.controllers.fragments;


import android.content.Intent;
import android.net.Uri;
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
import com.example.blanche.mynews.controllers.utils.ArticlesStreams;
import com.example.blanche.mynews.controllers.utils.ItemClickSupport;
import com.example.blanche.mynews.models.MostPopular.MostPopular;
import com.example.blanche.mynews.models.MostPopular.MostPopularResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

/**
 * A simple {@link Fragment} subclass.
 */
public class SecondPageFragment extends Fragment {

    @BindView(R.id.fragment_second_page_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.fragment_second_page_swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;
    private List<MostPopularResult> mostPopularResultList;
    private RecyclerViewAdapterSecondFragment adapter;
    private Disposable disposable;
    public static final String KEY_ARTICLE = "key_article";
    public static final String ARTICLE_TITLE = "article_title";
    public static final String KEY_POSITION = "position";
    Bundle bundle;

    //CONSTRUCTOR
    public SecondPageFragment() {
        // Required empty public constructor
    }

    public static SecondPageFragment newInstance(int position) {
        SecondPageFragment fragment = new SecondPageFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_second_page, container, false);

        ButterKnife.bind(this, result);
        configureRecyclerView();
        configureSwipeRefreshLayout();
        executeHttpRequestMostPopular();
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
        this.mostPopularResultList = new ArrayList<>();
        this.adapter = new RecyclerViewAdapterSecondFragment(this.mostPopularResultList, Glide.with(this));
        this.recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void configureSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                executeHttpRequestMostPopular();
            }
        });
    }

    private void configureOnClickRecyclerView() {
        ItemClickSupport.addTo(recyclerView, R.layout.fragment_page_item)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        MostPopularResult article = adapter.getArticle(position);
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
    //HTTP REQUEST
    //-----------------------------------------
    public void executeHttpRequestMostPopular() {
        this.disposable =
                ArticlesStreams.streamFetchMostPopularArticle(7).subscribeWith(new DisposableObserver<MostPopular>() {

                    @Override
                    public void onNext(MostPopular mostPopular) {
                        Log.e("TAG", "on next");
                        updateUIMostPopular(mostPopular.getMostPopularResults());
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
    private void updateUIMostPopular(List<MostPopularResult> results) {
        swipeRefreshLayout.setRefreshing(false);
        mostPopularResultList.clear();
        mostPopularResultList.addAll(results);
        adapter.notifyDataSetChanged();
    }
}
