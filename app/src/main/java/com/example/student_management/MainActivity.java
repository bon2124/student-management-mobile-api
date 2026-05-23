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

    private RecyclerView   recyclerView;
    private StudentAdapter adapter;
    private List<Student>  danhSachGoc = new ArrayList<>();
    private EditText       etSearch;
    private TextView       tvCount;
    private ImageView      fabAdd;

    private static final int REQUEST_CODE_ADD    = 100;
    private static final int REQUEST_CODE_DETAIL = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        etSearch     = findViewById(R.id.etSearch);
        tvCount      = findViewById(R.id.tvCount);
        fabAdd       = findViewById(R.id.fabAdd);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new StudentAdapter(this, new ArrayList<>(), student -> {
            Intent intent = new Intent(MainActivity.this, StudentDetailActivity.class);
            intent.putExtra("CHOSEN_STUDENT", student);
            startActivityForResult(intent, REQUEST_CODE_DETAIL);
        });
        recyclerView.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                timKiem(s.toString());
            }
        });

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddStudentActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD);
        });

        layDanhSach();
    }

    private void layDanhSach() {
        tvCount.setText("Đang tải dữ liệu...");

        Call<List<Student>> call = RetrofitClient.getInstance()
                .getApiService()
                .getAllStudents();

        call.enqueue(new Callback<List<Student>>() {
            @Override
            public void onResponse(Call<List<Student>> call, Response<List<Student>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    danhSachGoc.clear();
                    danhSachGoc.addAll(response.body());

                    if (etSearch.getText().toString().trim().isEmpty()) {
                        adapter.updateList(new ArrayList<>(danhSachGoc));
                        tvCount.setText("Tổng: " + danhSachGoc.size() + " sinh viên");
                    } else {
                        timKiem(etSearch.getText().toString());
                    }
                } else if (response.code() == 401 || response.code() == 403) {
                    // Token hết hạn → về màn đăng nhập
                    Toast.makeText(MainActivity.this, "Phiên đăng nhập hết hạn!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                } else {
                    tvCount.setText("❌ Không thể lấy dữ liệu từ server");
                }
            }

            @Override
            public void onFailure(Call<List<Student>> call, Throwable t) {
                tvCount.setText("❌ Lỗi kết nối API");
                Toast.makeText(MainActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void timKiem(String keyword) {
        if (keyword.isEmpty()) {
            adapter.updateList(new ArrayList<>(danhSachGoc));
            tvCount.setText("Tổng: " + danhSachGoc.size() + " sinh viên");
            return;
        }

        List<Student> ketQua = new ArrayList<>();
        String query = keyword.toLowerCase().trim();

        for (Student s : danhSachGoc) {
            boolean matchesName  = s.getName() != null && s.getName().toLowerCase().contains(query);
            boolean matchesCode  = s.getStudentCode() != null && s.getStudentCode().toLowerCase().contains(query);
            boolean matchesClass = s.getClassName() != null && s.getClassName().toLowerCase().contains(query);
            if (matchesName || matchesCode || matchesClass) ketQua.add(s);
        }
        adapter.updateList(ketQua);
        tvCount.setText("Tìm thấy: " + ketQua.size() + " sinh viên");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (etSearch != null) etSearch.setText("");
            layDanhSach();
        }
    }
}