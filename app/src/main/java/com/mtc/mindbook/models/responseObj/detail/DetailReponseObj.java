package com.mtc.mindbook.models.responseObj.detail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DetailReponseObj {

    @SerializedName("data")
    @Expose
    private List<BookDetail> data = null;

    public List<BookDetail> getData() {
        return data;
    }

    public void setData(List<BookDetail> data) {
        this.data = data;
    }
}
