package com.example.student_management.api;

import com.example.student_management.model.LoginResponse;
import com.example.student_management.model.Student;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    // 1. Lấy danh sách sinh viên
    @GET("get_students.php")
    Call<List<Student>> getAllStudents();

    // 2. Đăng nhập hệ thống
    @FormUrlEncoded
    @POST("login.php")
    Call<LoginResponse> login(
            @Field("username") String username,
            @Field("password") String password
    );

    // 3. Xóa sinh viên vĩnh viễn (Đã đồng bộ sang file delete_student.php và kiểu String id)
    @FormUrlEncoded
    @POST("delete_student.php")
    Call<okhttp3.ResponseBody> deleteStudent(
            @Field("id") String id
    );

    // 4. Cập nhật thông tin chi tiết sinh viên
    @FormUrlEncoded
    @POST("update_student.php")
    Call<okhttp3.ResponseBody> updateStudent(
            @Field("id")      String id,
            @Field("name")    String name,
            @Field("age")     String age,
            @Field("email")   String email,
            @Field("class")   String classCode,
            @Field("address") String address,
            @Field("phone")   String phone,
            @Field("date")    String date,
            @Field("sex")     String sex
    );

    // 5. Thêm sinh viên mới (Gửi Object JSON thô qua @Body)
    @POST("add_student.php")
    Call<ResponseBody> addStudent(@Body Student student);
}