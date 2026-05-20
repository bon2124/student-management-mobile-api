package com.example.student_management.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

// Thêm implements Serializable để truyền dữ liệu giữa các Activity
public class Student implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("age")
    private String age;

    @SerializedName("email")
    private String email;

    @SerializedName("class")
    private String className;

    @SerializedName("address")
    private String address;

    @SerializedName("phone")
    private String phone;

    @SerializedName("date")
    private String birthday;

    @SerializedName("sex")
    private String gender;

    public Student() {}

    public Student(String id, String name, String age, String email, String className, String address, String phone, String birthday, String gender) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
        this.className = className;
        this.address = address;
        this.phone = phone;
        this.birthday = birthday;
        this.gender = gender;
    }

    // Getters
    public String getId()        { return id; }
    public String getName()      { return name; }
    public String getAge()       { return age; }
    public String getEmail()     { return email; }
    public String getClassName() { return className; }
    public String getAddress()   { return address; }
    public String getPhone()     { return phone; }
    public String getBirthday()  { return birthday; }
    public String getGender()    { return gender; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAge(String age) { this.age = age; }
    public void setEmail(String email) { this.email = email; }
    public void setClassName(String className) { this.className = className; }
    public void setAddress(String address) { this.address = address; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setBirthday(String birthday) { this.birthday = birthday; }
    public void setGender(String gender) { this.gender = gender; }
}