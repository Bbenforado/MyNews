package com.example.blanche.mynews.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MostPopular {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("copyright")
    @Expose
    private String copyright;
    @SerializedName("num_results")
    @Expose
    private Integer numResults;
    @SerializedName("results")
    @Expose
    private List<MostPopularResult> results = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public Integer getNumResults() {
        return numResults;
    }

    public void setNumResults(Integer numResults) {
        this.numResults = numResults;
    }

    public List<MostPopularResult> getMostPopularResults() {
        return results;
    }

    public void setMostPopularResult(List<MostPopularResult> results) {
        this.results = results;
    }
}
