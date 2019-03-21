package com.example.blanche.mynews.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchArticleResponse {
    @SerializedName("docs")
    @Expose
    private List<SearchArticle> articles = null;

    public List<SearchArticle> getArticles() {
        return articles;
    }

    public void setArticles(List<SearchArticle> articles) {
        this.articles = articles;
    }
}
