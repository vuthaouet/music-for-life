package com.mtc.musicForLife.models.responseObj.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResponseObj {

    @SerializedName("data")
    @Expose
    private List<SearchItem> data = null;

    public List<SearchItem> getData() {
        return data;
    }

    public void setData(List<SearchItem> data) {
        this.data = data;
    }
}
