package com.example.student_management.api;

import com.example.student_management.model.LoginRequest;
import com.example.student_management.model.LoginResponse;
import com.example.student_management.model.Student;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    // 1. Lấy danh sách sinh viên (trả thẳng mảng)
    @GET("students")
    Call<List<Student>> getAllStudents();

    // 2. Đăng nhập
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    // 3. Xóa sinh viên
    @DELETE("students/{id}")
    Call<ResponseBody> deleteStudent(@Path("id") String id);

    // 4. Cập nhật sinh viên
    @PUT("students/{id}")
    Call<ResponseBody> updateStudent(@Path("id") String id, @Body Student student);

    // 5. Thêm sinh viên
    @POST("students")
    Call<ResponseBody> addStudent(@Body Student student);
}