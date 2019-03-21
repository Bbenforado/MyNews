package com.example.blanche.mynews.controllers.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.blanche.mynews.R;
import com.example.blanche.mynews.models.MostPopularMedia;
import com.example.blanche.mynews.models.MostPopularMediaDatum;
import com.example.blanche.mynews.models.MostPopularResult;
import com.example.blanche.mynews.models.TopStories.TopStoriesMultimedia;
import com.example.blanche.mynews.models.TopStories.TopStoriesResult;
import butterknife.BindView;
import butterknife.ButterKnife;

class ArticleViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.fragment_page_item_title) TextView textViewTitle;
    @BindView(R.id.fragment_page_item_image) ImageView imageView;
    @BindView(R.id.fragment_page_item_section) TextView textViewSection;
    @BindView(R.id.fragment_page_item_date) TextView textViewDate;

    //CONSTRUCTOR

    public ArticleViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    //METHODS

    public void updateWithTopStoriesArticle (TopStoriesResult result, RequestManager glide) {
        this.textViewTitle.setText(result.getTitle());
        this.textViewDate.setText(result.getPublishedDate().substring(0,10));
        if(result.getSubsection().isEmpty()) {
            this.textViewSection.setText(result.getSection());
        } else {
            this.textViewSection.setText(result.getSection() + " > " + result.getSubsection());
        }
        if(result.getMultimedia().size() != 0 ) {
            TopStoriesMultimedia topStoriesMultimedia = result.getMultimedia().get(0);
            glide.load(topStoriesMultimedia.getUrl()).apply(RequestOptions.noTransformation()).into(imageView);
        }
    }

    public void updateWithMostPopularArticle (MostPopularResult result, RequestManager glide) {
        textViewTitle.setText(result.getTitle());
        textViewSection.setText(result.getSection());
        textViewDate.setText(result.getPublishedDate());
        MostPopularMedia mostPopularMedia = result.getMedia().get(0);
        MostPopularMediaDatum imageData = mostPopularMedia.getMediaMetadata().get(0);
        glide.load(imageData.getUrl()).apply(RequestOptions.noTransformation()).into(imageView);
    }
}
