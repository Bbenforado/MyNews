package com.example.blanche.mynews.controllers.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.blanche.mynews.R;
import com.example.blanche.mynews.controllers.adapters.RecyclerViewAdapter;
import com.example.blanche.mynews.controllers.utils.ArticlesStreams;
import com.example.blanche.mynews.models.TopStories.TopStories;
import com.example.blanche.mynews.models.TopStories.TopStoriesResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class PageFragment extends Fragment {

    public static final String KEY_POSITION = "position";
    public static final String KEY_TEXT = "text";

    @BindView(R.id.fragment_page_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.fragment_page_swipe_container) SwipeRefreshLayout swipeRefreshLayout;

    private List<TopStoriesResult> topStoriesResultList;
    private RecyclerViewAdapter adapter;
    private Disposable disposable;

    //CONSTRUCTOR
    public PageFragment() {
        // Required empty public constructor
    }


    public static PageFragment newInstance(int position, String text) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_POSITION, position);
        args.putString(KEY_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_page, container, false);
       // textView = result.findViewById(R.id.fragment_textview);

        int position = getArguments().getInt(KEY_POSITION, -1);
        String text = getArguments().getString(KEY_TEXT, null);

      //  textView.setText(text);
        //TRIES
        ButterKnife.bind(this, result);
        configureRecyclerView();
        configureSwipeRefreshLayout(position);
        executeHttpRequest(position);
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
        this.adapter = new RecyclerViewAdapter(this.topStoriesResultList);
        this.recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void configureSwipeRefreshLayout(final int position) {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                executeHttpRequest(position);
            }
        });
    }


    //----------------------------
    //HTTP REQUEST RETROFIT + REACTIVE X
    //-----------------------------------------
    public void executeHttpRequest(int position) {
        switch (position) {
            case 0:
                executeHttpRequestTopStories();
                break;
            case 1:
                //executeHttpRequestMostPopular();
                break;
            case 2:
                //executeHttpRequestArts();
                break;
                default:
                    break;
        }
    }

    public void executeHttpRequestTopStories() {
        this.disposable =
                ArticlesStreams.streamFetchTopStoriesArticle("home").subscribeWith(new DisposableObserver<TopStories>() {
                    @Override
                    public void onNext(TopStories topStories) {
                        Log.e("TAG", "on next");
                        updateUITopStories(topStories.getResults());
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
    private void updateUITopStories(List<TopStoriesResult> results) {
        swipeRefreshLayout.setRefreshing(false);
        topStoriesResultList.clear();
        topStoriesResultList.addAll(results);
        adapter.notifyDataSetChanged();
    }

  //  private void updateUIMostPopular(List<MostPopularResult> results) {

   // }

   // private void updateUIArts(List<ArtsResult> results) {

   // }

}
