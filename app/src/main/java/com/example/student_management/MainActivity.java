package com.example.student_management;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student_management.api.RetrofitClient;
import com.example.student_management.model.Student;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView      recyclerView;
    private StudentAdapter    adapter;
    private List<Student>     danhSachGoc = new ArrayList<>();
    private EditText          etSearch;
    private TextView          tvCount;
    private ImageView         fabAdd;

    // Định nghĩa các Request Code để phân biệt các màn hình quay về
    private static final int REQUEST_CODE_ADD    = 100;
    private static final int REQUEST_CODE_DETAIL = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ giao diện mới chuẩn bo góc phẳng
        recyclerView = findViewById(R.id.recyclerView);
        etSearch     = findViewById(R.id.etSearch);
        tvCount      = findViewById(R.id.tvCount);
        fabAdd       = findViewById(R.id.fabAdd);

        // Cấu hình RecyclerView hiển thị danh sách theo chiều dọc
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo Adapter và xử lý sự kiện click vào từng Sinh viên
        adapter = new StudentAdapter(this, danhSachGoc, student -> {
            Intent intent = new Intent(MainActivity.this, StudentDetailActivity.class);
            intent.putExtra("CHOSEN_STUDENT", student);
            // 🛠️ ĐÃ SỬA: Chuyển sang dùng startActivityForResult để lắng nghe tín hiệu Xóa/Sửa từ DetailActivity trả về
            startActivityForResult(intent, REQUEST_CODE_DETAIL);
        });
        recyclerView.setAdapter(adapter);

        // Lắng nghe sự kiện thanh tìm kiếm thay đổi văn bản liên tục (Local Search)
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                timKiem(s.toString());
            }
        });

        // Nút thêm sinh viên mới (+)
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddStudentActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD);
        });

        // Khởi động ứng dụng -> Load danh sách sinh viên từ Server XAMPP về ngay
        layDanhSach();
    }

    // Hàm gọi Web API PHP lấy danh sách sinh viên mới nhất
    private void layDanhSach() {
        tvCount.setText("Đang tải...");

        Call<List<Student>> call = RetrofitClient.getInstance()
                .getApiService()
                .getAllStudents();

        call.enqueue(new Callback<List<Student>>() {
            @Override
            public void onResponse(Call<List<Student>> call, Response<List<Student>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    danhSachGoc.clear();
                    danhSachGoc.addAll(response.body());
                    adapter.updateList(danhSachGoc);
                    tvCount.setText("Tổng: " + danhSachGoc.size() + " sinh viên");
                } else {
                    tvCount.setText("❌ Không thể lấy dữ liệu từ server");
                }
            }

            @Override
            public void onFailure(Call<List<Student>> call, Throwable t) {
                tvCount.setText("❌ Lỗi kết nối");
                Toast.makeText(MainActivity.this,
                        "Lỗi kết nối server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Xử lý bộ lọc tìm kiếm cục bộ (Local offline search)
    private void timKiem(String keyword) {
        if (keyword.isEmpty()) {
            adapter.updateList(danhSachGoc);
            tvCount.setText("Tổng: " + danhSachGoc.size() + " sinh viên");
            return;
        }

        List<Student> ketQua = new ArrayList<>();
        String query = keyword.toLowerCase().trim();

        for (Student s : danhSachGoc) {
            // Kiểm tra điều kiện phòng hờ các trường dữ liệu bị trống (null) từ MySQL
            boolean matchesName = s.getName() != null && s.getName().toLowerCase().contains(query);
            boolean matchesId = s.getId() != null && s.getId().contains(query);
            boolean matchesClass = s.getClassName() != null && s.getClassName().toLowerCase().contains(query);

            if (matchesName || matchesId || matchesClass) {
                ketQua.add(s);
            }
        }
        adapter.updateList(ketQua);
        tvCount.setText("Tìm thấy: " + ketQua.size() + " sinh viên");
    }

    // 🛠️ HÀM NHẬN PHẢN HỒI: Tự động kích hoạt khi màn hình Add hoặc Detail đóng lại với tín hiệu thành công
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Bất kể là vừa Thêm mới (REQUEST_CODE_ADD) hay xem Chi tiết rồi Xóa/Sửa (REQUEST_CODE_DETAIL)
        // Cứ hễ bên kia báo sửa đổi thành công (RESULT_OK) -> Tự động tải lại danh sách mới nhất từ Database
        if (resultCode == RESULT_OK) {
            layDanhSach();
        }
    }
}