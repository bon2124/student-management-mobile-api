package com.example.student_management.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Student implements Serializable {

    @SerializedName("_id")
    private String id;

    @SerializedName("studentCode")
    private String studentCode;

    @SerializedName("fullName")
    private String name;

    @SerializedName("age")
    private String age;

    @SerializedName("email")
    private String email;

    @SerializedName("className")
    private String className;

    @SerializedName("address")
    private String address;

    @SerializedName("phone")
    private String phone;

    @SerializedName("date")
    private String birthday;

    @SerializedName("sex")
    private String gender;

    @SerializedName("gpa")
    private String gpa;

    // Constructor trống bắt buộc cho GSON
    public Student() {}

    // Constructor đầy đủ
    public Student(String id, String name, String age, String email, String className,
                   String address, String phone, String birthday, String gender) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
        this.className = className;
        this.address = address;
        this.phone = phone;
        this.birthday = birthday;
        this.gender = gender;
        this.studentCode = id;
    }

    // GETTERS
    public String getId()          { return id; }
    public String getStudentCode() { return studentCode; }
    public String getName()        { return name; }
    public String getAge()         { return age; }
    public String getEmail()       { return email; }
    public String getClassName()   { return className; }
    public String getAddress()     { return address; }
    public String getPhone()       { return phone; }
    public String getBirthday()    { return birthday; }
    public String getGender()      { return gender; }
    public String getGpa()         { return gpa; }

    // SETTERS
    public void setId(String id)                   { this.id = id; }
    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }
    public void setName(String name)               { this.name = name; }
    public void setAge(String age)                 { this.age = age; }
    public void setEmail(String email)             { this.email = email; }
    public void setClassName(String c)             { this.className = c; }
    public void setAddress(String a)               { this.address = a; }
    public void setPhone(String p)                 { this.phone = p; }
    public void setBirthday(String b)              { this.birthday = b; }
    public void setGender(String g)                { this.gender = g; }
    public void setGpa(String gpa)                 { this.gpa = gpa; }
}