package com.example.blanche.mynews.controllers.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.blanche.mynews.R;
import com.example.blanche.mynews.models.TopStoriesResult;

import butterknife.BindView;
import butterknife.ButterKnife;

class ArticleViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.fragment_page_item_title) TextView textView;
    @BindView(R.id.fragment_page_item_image) ImageView imageView;
    @BindView(R.id.fragment_page_item_text) TextView textViewMainText;

    //CONSTRUCTOR

    public ArticleViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    //METHODS


    public void updateWithArticle (TopStoriesResult result) {
        this.textView.setText(result.getTitle());
        this.textViewMainText.setText(result.getUrl());
    }
}
