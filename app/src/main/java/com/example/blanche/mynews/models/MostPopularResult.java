package com.example.blanche.mynews.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MostPopularResult {
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("adx_keywords")
    @Expose
    private String adxKeywords;
    @SerializedName("column")
    @Expose
    private Object column;
    @SerializedName("section")
    @Expose
    private String section;
    @SerializedName("byline")
    @Expose
    private String byline;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("abstract")
    @Expose
    private String _abstract;
    @SerializedName("published_date")
    @Expose
    private String publishedDate;
    @SerializedName("source")
    @Expose
    private String source;
    @SerializedName("id")
    @Expose
    private float id;
    @SerializedName("asset_id")
    @Expose
    private float assetId;
    @SerializedName("views")
    @Expose
    private Integer views;
    // @SerializedName("des_facet")
    //  @Expose
    //   private List<String> desFacet = null;
    //   @SerializedName("org_facet")
    // @Expose
    //   private List<String> orgFacet = null;
    // @SerializedName("per_facet")
    // @Expose
    //  private List<String> perFacet = null;
    //  @SerializedName("geo_facet")
    // @Expose
    // private String geoFacet;
    @SerializedName("media")
    @Expose
    private List<MostPopularMedia> media = null;
    @SerializedName("uri")
    @Expose
    private String uri;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAdxKeywords() {
        return adxKeywords;
    }

    public void setAdxKeywords(String adxKeywords) {
        this.adxKeywords = adxKeywords;
    }

    public Object getColumn() {
        return column;
    }

    public void setColumn(Object column) {
        this.column = column;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getByline() {
        return byline;
    }

    public void setByline(String byline) {
        this.byline = byline;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbstract() {
        return _abstract;
    }

    public void setAbstract(String _abstract) {
        this._abstract = _abstract;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public float getId() {
        return id;
    }

    public void setId(float id) {
        this.id = id;
    }

    public float getAssetId() {
        return assetId;
    }

    public void setAssetId(float assetId) {
        this.assetId = assetId;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    //   public List<String> getDesFacet() {
    //       return desFacet;
    //  }

    //public void setDesFacet(List<String> desFacet) {
    //  this.desFacet = desFacet;
    // }

    //  public List<String> getOrgFacet() {
    //      return orgFacet;
    //  }

//    public void setOrgFacet(List<String> orgFacet) {
    //       this.orgFacet = orgFacet;
    //  }

    //public List<String> getPerFacet() {
    //     return perFacet;
    // }

    //  public void setPerFacet(List<String> perFacet) {
    //     this.perFacet = perFacet;
    // }

    //  public String getGeoFacet() {
    //      return geoFacet;
    //  }

    //   public void setGeoFacet(String geoFacet) {
    //     this.geoFacet = geoFacet;
    //  }

    public List<MostPopularMedia> getMedia() {
        return media;
    }

    //  public void setMedia(List<MostPopular.Medium> media) {
    //     this.media = media;
    // }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

}
