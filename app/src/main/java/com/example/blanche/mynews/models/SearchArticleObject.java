package com.example.blanche.mynews.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchArticleObject {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("response")
    @Expose
    private SearchArticleResponse response;

    public String getStatus() {
        return status;
    }

    public SearchArticleResponse getResponse() {
        return response;
    }
}
