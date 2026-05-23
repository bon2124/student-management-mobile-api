package com.example.student_management;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.student_management.api.RetrofitClient;
import com.example.student_management.model.Student;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentDetailActivity extends AppCompatActivity {

    private Student currentStudent;
    private TextView tvNameLarge, tvIdSmall, tvDetailAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail);

        tvNameLarge    = findViewById(R.id.tvNameLarge);
        tvIdSmall      = findViewById(R.id.tvIdSmall);
        tvDetailAvatar = findViewById(R.id.tvDetailAvatar);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        currentStudent = (Student) getIntent().getSerializableExtra("CHOSEN_STUDENT");

        if (currentStudent != null) {
            hienThiDuLieu();
        } else {
            Toast.makeText(this, "Không tìm thấy dữ liệu sinh viên!", Toast.LENGTH_SHORT).show();
        }

        findViewById(R.id.btnEdit).setOnClickListener(v -> {
            if (currentStudent != null) moDialogCapNhat();
        });

        findViewById(R.id.btnDelete).setOnClickListener(v -> {
            if (currentStudent != null) xacNhanXoa();
        });
    }

    private void hienThiDuLieu() {
        tvNameLarge.setText(currentStudent.getName() != null ? currentStudent.getName() : "Chưa có tên");
        tvIdSmall.setText(currentStudent.getStudentCode());

        String fullName = currentStudent.getName();
        String firstLetter = "?";
        if (fullName != null && !fullName.trim().isEmpty()) {
            fullName = fullName.trim();
            int lastSpaceIndex = fullName.lastIndexOf(" ");
            if (lastSpaceIndex != -1) {
                String lastName = fullName.substring(lastSpaceIndex + 1);
                if (!lastName.isEmpty()) firstLetter = String.valueOf(lastName.charAt(0)).toUpperCase();
            } else {
                firstLetter = String.valueOf(fullName.charAt(0)).toUpperCase();
            }
        }
        tvDetailAvatar.setText(firstLetter);

        setupRow(R.id.rowId,       R.drawable.ic_badge,    "Mã sinh viên (ID)",      currentStudent.getStudentCode());
        setupRow(R.id.rowName,     R.drawable.ic_person,   "Họ và tên",              currentStudent.getName());
        setupRow(R.id.rowAge,      R.drawable.ic_cake,     "Tuổi",                   currentStudent.getAge());
        setupRow(R.id.rowEmail,    R.drawable.ic_mail,     "Email",                  currentStudent.getEmail());
        setupRow(R.id.rowClass,    R.drawable.ic_school,   "Lớp / Khoa",             currentStudent.getClassName());
        setupRow(R.id.rowAddress,  R.drawable.ic_home,     "Địa chỉ",                currentStudent.getAddress());
        setupRow(R.id.rowPhone,    R.drawable.ic_phone,    "Số điện thoại",          currentStudent.getPhone());
        setupRow(R.id.rowBirthday, R.drawable.ic_calendar, "Ngày sinh",              currentStudent.getBirthday());
        setupRow(R.id.rowGender,   R.drawable.ic_gender,   "Giới tính",              currentStudent.getGender());
        setupRow(R.id.rowGpa,      R.drawable.ic_badge,    "GPA (Điểm TB tích lũy)", currentStudent.getGpa());
    }

    private void setupRow(int layoutId, int iconRes, String label, String value) {
        View row = findViewById(layoutId);
        if (row != null) {
            ((ImageView) row.findViewById(R.id.imgRowIcon)).setImageResource(iconRes);
            ((TextView)  row.findViewById(R.id.tvRowLabel)).setText(label);
            ((TextView)  row.findViewById(R.id.tvRowValue)).setText(
                    value != null && !value.isEmpty() ? value : "---");
        }
    }

    private void xacNhanXoa() {
        String targetId = (currentStudent.getId() != null && !currentStudent.getId().isEmpty())
                ? currentStudent.getId() : currentStudent.getStudentCode();

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa sinh viên " + currentStudent.getName() + " không?")
                .setPositiveButton("Xóa", (dialog, which) ->
                        RetrofitClient.getInstance().getApiService().deleteStudent(targetId)
                                .enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if (response.isSuccessful()) {
                                            Toast.makeText(StudentDetailActivity.this, "🎉 Xóa sinh viên thành công!", Toast.LENGTH_SHORT).show();
                                            setResult(RESULT_OK);
                                            finish();
                                        } else {
                                            Toast.makeText(StudentDetailActivity.this, "❌ Server từ chối lệnh xóa!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Toast.makeText(StudentDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                )
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void moDialogCapNhat() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chỉnh sửa thông tin");

        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        final EditText edtName    = createPopupEditText("Họ và tên",               currentStudent.getName());
        final EditText edtAge     = createPopupEditText("Tuổi",                    currentStudent.getAge());
        final EditText edtEmail   = createPopupEditText("Email",                   currentStudent.getEmail());
        final EditText edtClass   = createPopupEditText("Lớp / Khoa",              currentStudent.getClassName());
        final EditText edtAddress = createPopupEditText("Địa chỉ",                 currentStudent.getAddress());
        final EditText edtPhone   = createPopupEditText("Số điện thoại",           currentStudent.getPhone());
        final EditText edtDate    = createPopupEditText("Ngày sinh",               currentStudent.getBirthday());
        final EditText edtSex     = createPopupEditText("Giới tính (Nam/Nữ)",      currentStudent.getGender());
        final EditText edtGpa     = createPopupEditText("GPA (Điểm TB tích lũy)",  currentStudent.getGpa() != null ? currentStudent.getGpa() : "");

        layout.addView(edtName);
        layout.addView(edtAge);
        layout.addView(edtEmail);
        layout.addView(edtClass);
        layout.addView(edtAddress);
        layout.addView(edtPhone);
        layout.addView(edtDate);
        layout.addView(edtSex);
        layout.addView(edtGpa);

        android.widget.ScrollView scrollView = new android.widget.ScrollView(this);
        scrollView.addView(layout);
        builder.setView(scrollView);

        builder.setPositiveButton("Lưu thay đổi", (dialog, which) -> {
            String updatedName    = edtName.getText().toString().trim();
            String updatedAge     = edtAge.getText().toString().trim();
            String updatedEmail   = edtEmail.getText().toString().trim();
            String updatedClass   = edtClass.getText().toString().trim();
            String updatedAddress = edtAddress.getText().toString().trim();
            String updatedPhone   = edtPhone.getText().toString().trim();
            String updatedDate    = edtDate.getText().toString().trim();
            String updatedSex     = edtSex.getText().toString().trim();
            String updatedGpa     = edtGpa.getText().toString().trim();

            if (updatedName.isEmpty()) {
                Toast.makeText(this, "Tên không được để trống!", Toast.LENGTH_SHORT).show();
                return;
            }

            Student updateData = new Student();
            updateData.setId(currentStudent.getId());
            updateData.setStudentCode(currentStudent.getStudentCode());
            updateData.setName(updatedName);
            updateData.setAge(updatedAge);
            updateData.setEmail(updatedEmail);
            updateData.setClassName(updatedClass);
            updateData.setAddress(updatedAddress);
            updateData.setPhone(updatedPhone);
            updateData.setBirthday(updatedDate);
            updateData.setGender(updatedSex);
            updateData.setGpa(updatedGpa);

            String requestPathId = (currentStudent.getId() != null && !currentStudent.getId().isEmpty())
                    ? currentStudent.getId() : currentStudent.getStudentCode();

            RetrofitClient.getInstance().getApiService().updateStudent(requestPathId, updateData)
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(StudentDetailActivity.this, "🎉 Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                                currentStudent.setName(updatedName);
                                currentStudent.setAge(updatedAge);
                                currentStudent.setEmail(updatedEmail);
                                currentStudent.setClassName(updatedClass);
                                currentStudent.setAddress(updatedAddress);
                                currentStudent.setPhone(updatedPhone);
                                currentStudent.setBirthday(updatedDate);
                                currentStudent.setGender(updatedSex);
                                currentStudent.setGpa(updatedGpa);
                                hienThiDuLieu();
                                setResult(RESULT_OK);
                            } else {
                                Toast.makeText(StudentDetailActivity.this, "❌ Cập nhật thất bại! Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(StudentDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        builder.setNegativeButton("Hủy bỏ", null);
        builder.show();
    }

    private EditText createPopupEditText(String hint, String text) {
        EditText edt = new EditText(this);
        edt.setHint(hint);
        edt.setText(text != null ? text : "");
        edt.setPadding(10, 30, 10, 30);
        return edt;
    }
}