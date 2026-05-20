package com.example.student_management;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.student_management.api.RetrofitClient;
import com.example.student_management.model.Student;

import java.util.Calendar;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddStudentActivity extends AppCompatActivity {

    private EditText etId, etName, etAge, etEmail, etAddress, etPhone;
    private Spinner spClass;
    private TextView tvBirthday;
    private RadioGroup rgGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        // 1. Ánh xạ toàn bộ view theo thiết kế mới
        etId       = findViewById(R.id.etStudentId);
        etName     = findViewById(R.id.etName);
        etAge      = findViewById(R.id.etAge);
        etEmail    = findViewById(R.id.etEmail);
        spClass    = findViewById(R.id.spClass);
        etAddress  = findViewById(R.id.etAddress);
        etPhone    = findViewById(R.id.etPhone);
        tvBirthday = findViewById(R.id.tvBirthday);
        rgGender   = findViewById(R.id.rgGender);

        // 2. Cấu hình danh sách thả xuống mang danh sách Lớp/Khoa
        String[] arraysClass = {"Chọn lớp / khoa", "CNTT 1", "CNTT 2", "Chất lượng cao", "An toàn thông tin", "Hệ thống nhúng"};
        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arraysClass);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spClass.setAdapter(classAdapter);

        // 3. Xử lý sự kiện click chọn ngày sinh bằng biểu đồ Lịch
        findViewById(R.id.layoutBirthday).setOnClickListener(v -> moBoChonNgay());

        // 4. Bắt sự kiện nút Mũi tên quay lại (Hủy bỏ)
        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());

        // 5. Bắt sự kiện nút Tích chọn lưu dữ liệu (V góc phải)
        findViewById(R.id.btnSave).setOnClickListener(v -> thucHienLuuData());
    }

    private void moBoChonNgay() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    String standardDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                    tvBirthday.setText(standardDate);
                    tvBirthday.setTextColor(android.graphics.Color.parseColor("#1F2937")); // Đổi màu chữ sang tối khi đã chọn
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void thucHienLuuData() {
        String id        = etId.getText().toString().trim();
        String name      = etName.getText().toString().trim();
        String age       = etAge.getText().toString().trim();
        String email     = etEmail.getText().toString().trim();
        String className = spClass.getSelectedItem().toString();
        String address   = etAddress.getText().toString().trim();
        String phone     = etPhone.getText().toString().trim();
        String birthday  = tvBirthday.getText().toString();

        // Kiểm tra dữ liệu hợp lệ đầu vào
        if (id.isEmpty()) { etId.setError("Nhập mã sinh viên!"); return; }
        if (name.isEmpty()) { etName.setError("Nhập họ tên!"); return; }
        if (className.equals("Chọn lớp / khoa")) {
            Toast.makeText(this, "Vui lòng chọn một lớp cụ thể!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (birthday.equals("Chọn ngày sinh")) { birthday = ""; }

        // Lấy dữ liệu giới tính từ RadioGroup
        int idChecked = rgGender.getCheckedRadioButtonId();
        RadioButton rbSelected = findViewById(idChecked);
        String gender = (rbSelected != null) ? rbSelected.getText().toString() : "Nam";

        // Gói đối tượng Student truyền JSON qua Body
        Student newStudent = new Student(id, name, age, email, className, address, phone, birthday, gender);

        findViewById(R.id.btnSave).setEnabled(false); // Khóa nút tạm thời

        Call<ResponseBody> call = RetrofitClient.getInstance().getApiService().addStudent(newStudent);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                findViewById(R.id.btnSave).setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(AddStudentActivity.this, "🎉 Thêm sinh viên thành công vĩnh viễn!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // Gửi tín hiệu reload danh sách ra ngoài MainActivity
                    finish();
                } else {
                    Toast.makeText(AddStudentActivity.this, "❌ Trùng mã sinh viên (ID) hoặc lỗi Database!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                findViewById(R.id.btnSave).setEnabled(true);
                Toast.makeText(AddStudentActivity.this, "Lỗi kết nối mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}