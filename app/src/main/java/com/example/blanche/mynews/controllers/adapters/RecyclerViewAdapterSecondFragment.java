package com.example.blanche.mynews.controllers.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.example.blanche.mynews.R;
import com.example.blanche.mynews.models.MostPopularResult;
import com.example.blanche.mynews.models.TopStories.TopStoriesResult;

import java.util.List;

public class RecyclerViewAdapterSecondFragment extends RecyclerView.Adapter<ArticleViewHolder> {

    private List<MostPopularResult> mostPopularResults;
    private RequestManager glide;

    public RecyclerViewAdapterSecondFragment(List<MostPopularResult> mostPopularResults, RequestManager glide) {
        this.mostPopularResults = mostPopularResults;
        this.glide = glide;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_page_item, viewGroup, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder articleViewHolder, int position) {
        articleViewHolder.updateWithMostPopularArticle(this.mostPopularResults.get(position), this.glide);
    }

    @Override
    public int getItemCount() {
        return mostPopularResults.size();
    }

    public MostPopularResult getArticle(int position) {
        return this.mostPopularResults.get(position);
    }
}
