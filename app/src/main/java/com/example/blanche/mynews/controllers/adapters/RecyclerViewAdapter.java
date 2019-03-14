package com.example.blanche.mynews.controllers.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.blanche.mynews.R;
import com.example.blanche.mynews.models.TopStoriesResult;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<ArticleViewHolder> {

    private List<TopStoriesResult> topStoriesResults;

    public RecyclerViewAdapter(List<TopStoriesResult> topStoriesResults) {
        this.topStoriesResults = topStoriesResults;
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
        articleViewHolder.updateWithArticle(this.topStoriesResults.get(position));
    }

    @Override
    public int getItemCount() {
        return topStoriesResults.size();
    }
}
