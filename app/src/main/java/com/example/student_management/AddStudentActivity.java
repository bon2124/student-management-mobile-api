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

    private EditText etId, etName, etAge, etEmail, etAddress, etPhone, etGpa;
    private Spinner spClass;
    private TextView tvBirthday;
    private RadioGroup rgGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        etId       = findViewById(R.id.etStudentId);
        etName     = findViewById(R.id.etName);
        etAge      = findViewById(R.id.etAge);
        etEmail    = findViewById(R.id.etEmail);
        spClass    = findViewById(R.id.spClass);
        etAddress  = findViewById(R.id.etAddress);
        etPhone    = findViewById(R.id.etPhone);
        tvBirthday = findViewById(R.id.tvBirthday);
        rgGender   = findViewById(R.id.rgGender);
        etGpa      = findViewById(R.id.etGpa);

        String[] arraysClass = {"Chọn lớp / khoa", "CNTT 1", "CNTT 2", "Chất lượng cao", "An toàn thông tin", "Hệ thống nhúng"};
        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arraysClass);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spClass.setAdapter(classAdapter);

        findViewById(R.id.layoutBirthday).setOnClickListener(v -> moBoChonNgay());
        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());
        findViewById(R.id.btnSave).setOnClickListener(v -> thucHienLuuData());
    }

    private void moBoChonNgay() {
        final Calendar c = Calendar.getInstance();
        int mYear  = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay   = c.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            String standardDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
            tvBirthday.setText(standardDate);
            tvBirthday.setTextColor(android.graphics.Color.parseColor("#1F2937"));
        }, mYear, mMonth, mDay).show();
    }

    private void thucHienLuuData() {
        String studentCode = etId.getText().toString().trim();
        String name        = etName.getText().toString().trim();
        String age         = etAge.getText().toString().trim();
        String email       = etEmail.getText().toString().trim();
        String className   = spClass.getSelectedItem().toString();
        String address     = etAddress.getText().toString().trim();
        String phone       = etPhone.getText().toString().trim();
        String birthday    = tvBirthday.getText().toString().trim();
        String gpa         = etGpa.getText().toString().trim();

        // Validate
        if (studentCode.isEmpty()) { etId.setError("Nhập mã sinh viên!"); return; }
        if (name.isEmpty())        { etName.setError("Nhập họ tên!"); return; }
        if (className.equals("Chọn lớp / khoa")) {
            Toast.makeText(this, "Vui lòng chọn một lớp cụ thể!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (birthday.equals("Chọn ngày sinh")) birthday = "";

        int idChecked = rgGender.getCheckedRadioButtonId();
        RadioButton rbSelected = findViewById(idChecked);
        String gender = (rbSelected != null) ? rbSelected.getText().toString() : "Nam";

        // Tạo Student với đúng studentCode
        Student newStudent = new Student();
        newStudent.setStudentCode(studentCode);
        newStudent.setName(name);
        newStudent.setAge(age);
        newStudent.setEmail(email);
        newStudent.setClassName(className);
        newStudent.setAddress(address);
        newStudent.setPhone(phone);
        newStudent.setBirthday(birthday);
        newStudent.setGender(gender);
        newStudent.setGpa(gpa);

        findViewById(R.id.btnSave).setEnabled(false);

        Call<ResponseBody> call = RetrofitClient.getInstance().getApiService().addStudent(newStudent);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                findViewById(R.id.btnSave).setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(AddStudentActivity.this, "🎉 Thêm sinh viên thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AddStudentActivity.this, "❌ Mã sinh viên đã tồn tại hoặc lỗi dữ liệu!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                findViewById(R.id.btnSave).setEnabled(true);
                Toast.makeText(AddStudentActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}