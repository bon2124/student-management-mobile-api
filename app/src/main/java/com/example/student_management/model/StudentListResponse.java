package com.example.student_management.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class StudentListResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("total")
    private int total;

    @SerializedName("page")
    private int page;

    @SerializedName("totalPages")
    private int totalPages;

    @SerializedName("data")
    private List<Student> data;

    public boolean isSuccess()      { return success; }
    public int getTotal()           { return total; }
    public int getPage()            { return page; }
    public int getTotalPages()      { return totalPages; }
    public List<Student> getData()  { return data; }
}