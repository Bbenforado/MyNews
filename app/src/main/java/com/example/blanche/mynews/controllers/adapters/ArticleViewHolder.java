package com.example.blanche.mynews.controllers.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.manager.RequestManagerRetriever;
import com.bumptech.glide.request.RequestOptions;
import com.example.blanche.mynews.R;
import com.example.blanche.mynews.models.MostPopular.MostPopularMedia;
import com.example.blanche.mynews.models.MostPopular.MostPopularMediaDatum;
import com.example.blanche.mynews.models.MostPopular.MostPopularResult;
import com.example.blanche.mynews.models.SearchArticles.SearchArticle;
import com.example.blanche.mynews.models.SearchArticles.SearchArticleMultimedium;
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
        } else {
            imageView.setBackgroundResource(R.drawable.no_image_available);
        }
    }

    public void updateWithMostPopularArticle (MostPopularResult result, RequestManager glide) {
        textViewTitle.setText(result.getTitle());
        textViewSection.setText(result.getSection());
        textViewDate.setText(result.getPublishedDate());
        if (result.getMedia().size() != 0) {
            MostPopularMedia mostPopularMedia = result.getMedia().get(0);
            MostPopularMediaDatum imageData = mostPopularMedia.getMediaMetadata().get(0);
            glide.load(imageData.getUrl()).apply(RequestOptions.noTransformation()).into(imageView);
        } else {
            imageView.setBackgroundResource(R.drawable.no_image_available);
        }
    }

    public void updateWithSearchedArticle (SearchArticle article, RequestManager glide) {
        textViewTitle.setText(article.getHeadline().getMain());
        if(article.getPubDate() != null) {
            textViewDate.setText(article.getPubDate().substring(0, 10));
        }
        if(article.getMultimedia().size() != 0) {
            SearchArticleMultimedium multimedium = article.getMultimedia().get(0);
            String url = "https://static01.nyt.com/" + multimedium.getUrl();
            glide.load(url).apply(RequestOptions.noTransformation()).into(imageView);
        } else {
            imageView.setBackgroundResource(R.drawable.no_image_available);
        }
    }

}
