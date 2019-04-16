package com.example.blanche.mynews.controllers.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.RequestManager;
import com.example.blanche.mynews.R;
import com.example.blanche.mynews.models.SearchArticles.SearchArticle;

import java.util.List;

public class RecyclerViewAdapterThirdFragment extends RecyclerView.Adapter<ArticleViewHolder>{

    private List<SearchArticle> searchArticleList;
    private RequestManager glide;

    public RecyclerViewAdapterThirdFragment(List<SearchArticle> searchArticleList, RequestManager glide) {
        this.searchArticleList = searchArticleList;
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
        articleViewHolder.updateWithSearchedArticle(this.searchArticleList.get(position), this.glide);
    }

    @Override
    public int getItemCount() {
        return searchArticleList.size();
    }

    public SearchArticle getArticle(int position) {
        return this.searchArticleList.get(position);
    }
}
