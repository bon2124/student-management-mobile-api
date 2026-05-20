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

import org.json.JSONObject; // Bổ sung thư viện bóc tách JSON trực tiếp

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

        // Ánh xạ các trường Header Profile
        tvNameLarge    = findViewById(R.id.tvNameLarge);
        tvIdSmall      = findViewById(R.id.tvIdSmall);
        tvDetailAvatar = findViewById(R.id.tvDetailAvatar);

        // Nút Quay lại
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Nhận đối tượng Student truyền từ MainActivity
        currentStudent = (Student) getIntent().getSerializableExtra("CHOSEN_STUDENT");

        if (currentStudent != null) {
            hienThiDuLieu();
        }

        // Xử lý chức năng SỬA khi nhấn vào biểu tượng cây bút
        findViewById(R.id.btnEdit).setOnClickListener(v -> {
            if (currentStudent != null) {
                moDialogCapNhat();
            }
        });

        // Xử lý chức năng XÓA khi nhấn vào biểu tượng thùng rác
        findViewById(R.id.btnDelete).setOnClickListener(v -> {
            if (currentStudent != null) {
                xacNhanXoa();
            }
        });
    }

    // Hàm hiển thị dữ liệu sinh viên lên giao diện bo góc phẳng
    private void hienThiDuLieu() {
        tvNameLarge.setText(currentStudent.getName());
        tvIdSmall.setText(currentStudent.getId());

        if (currentStudent.getName() != null && !currentStudent.getName().isEmpty()) {
            String letter = currentStudent.getName().trim().substring(0, 1).toUpperCase();
            tvDetailAvatar.setText(letter);
        }

        setupRow(R.id.rowId, R.drawable.ic_badge, "Mã sinh viên (ID)", currentStudent.getId());
        setupRow(R.id.rowName, R.drawable.ic_person, "Họ và tên", currentStudent.getName());
        setupRow(R.id.rowAge, R.drawable.ic_cake, "Tuổi", currentStudent.getAge());
        setupRow(R.id.rowEmail, R.drawable.ic_mail, "Email", currentStudent.getEmail());
        setupRow(R.id.rowClass, R.drawable.ic_school, "Lớp / Khoa", currentStudent.getClassName());
        setupRow(R.id.rowAddress, R.drawable.ic_home, "Địa chỉ", currentStudent.getAddress());
        setupRow(R.id.rowPhone, R.drawable.ic_phone, "Số điện thoại", currentStudent.getPhone());
        setupRow(R.id.rowBirthday, R.drawable.ic_calendar, "Ngày sinh", currentStudent.getBirthday());
        setupRow(R.id.rowGender, R.drawable.ic_gender, "Giới tính", currentStudent.getGender());
    }

    private void setupRow(int layoutId, int iconRes, String label, String value) {
        View row = findViewById(layoutId);
        if (row != null) {
            ((ImageView) row.findViewById(R.id.imgRowIcon)).setImageResource(iconRes);
            ((TextView) row.findViewById(R.id.tvRowLabel)).setText(label);
            ((TextView) row.findViewById(R.id.tvRowValue)).setText(value != null && !value.isEmpty() ? value : "---");
        }
    }

    // HÀM XỬ LÝ XÓA SINH VIÊN (Sử dụng ResponseBody có bóc tách JSON)
    private void xacNhanXoa() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa sinh viên " + currentStudent.getName() + " không?")
                .setPositiveButton("Xóa", (dialog, which) -> {

                    RetrofitClient.getInstance().getApiService().deleteStudent(currentStudent.getId())
                            .enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        try {
                                            // Đọc chuỗi string thô từ server trả về
                                            String strJson = response.body().string();
                                            JSONObject jsonObject = new JSONObject(strJson);
                                            boolean success = jsonObject.optBoolean("success", false);
                                            String message = jsonObject.optString("message", "Không có thông báo");

                                            if (success) {
                                                Toast.makeText(StudentDetailActivity.this, "Xóa sinh viên thành công vĩnh viễn!", Toast.LENGTH_SHORT).show();
                                                setResult(RESULT_OK); // Gửi tín hiệu reload danh sách cho MainActivity
                                                finish();
                                            } else {
                                                // Nếu SQL chạy lỗi hoặc không tìm thấy ID, hiển thị chính xác lỗi từ PHP
                                                Toast.makeText(StudentDetailActivity.this, "Thất bại: " + message, Toast.LENGTH_LONG).show();
                                            }
                                        } catch (Exception e) {
                                            Toast.makeText(StudentDetailActivity.this, "Lỗi bóc tách phản hồi dữ liệu", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(StudentDetailActivity.this, "Server từ chối lệnh xóa hoặc phản hồi trống!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Toast.makeText(StudentDetailActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // HÀM MỞ HỘP THOẠI ĐỂ SỬA THÔNG TIN NHANH (Sử dụng ResponseBody có bóc tách JSON)
    private void moDialogCapNhat() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chỉnh sửa thông tin");

        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        final EditText edtName = createPopupEditText("Họ và tên", currentStudent.getName());
        final EditText edtAge = createPopupEditText("Tuổi", currentStudent.getAge());
        final EditText edtEmail = createPopupEditText("Email", currentStudent.getEmail());
        final EditText edtClass = createPopupEditText("Lớp / Khoa", currentStudent.getClassName());
        final EditText edtAddress = createPopupEditText("Địa chỉ", currentStudent.getAddress());
        final EditText edtPhone = createPopupEditText("Số điện thoại", currentStudent.getPhone());
        final EditText edtDate = createPopupEditText("Ngày sinh", currentStudent.getBirthday());
        final EditText edtSex = createPopupEditText("Giới tính (Nam/Nữ)", currentStudent.getGender());

        layout.addView(edtName); layout.addView(edtAge); layout.addView(edtEmail);
        layout.addView(edtClass); layout.addView(edtAddress); layout.addView(edtPhone);
        layout.addView(edtDate); layout.addView(edtSex);

        android.widget.ScrollView scrollView = new android.widget.ScrollView(this);
        scrollView.addView(layout);
        builder.setView(scrollView);

        builder.setPositiveButton("Lưu thay đổi", (dialog, which) -> {
            String updatedName = edtName.getText().toString().trim();
            String updatedAge = edtAge.getText().toString().trim();
            String updatedEmail = edtEmail.getText().toString().trim();
            String updatedClass = edtClass.getText().toString().trim();
            String updatedAddress = edtAddress.getText().toString().trim();
            String updatedPhone = edtPhone.getText().toString().trim();
            String updatedDate = edtDate.getText().toString().trim();
            String updatedSex = edtSex.getText().toString().trim();

            if (updatedName.isEmpty()) {
                Toast.makeText(this, "Tên không được để trống!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gọi API cập nhật lên server PHP
            RetrofitClient.getInstance().getApiService().updateStudent(
                    currentStudent.getId(), updatedName, updatedAge, updatedEmail,
                    updatedClass, updatedAddress, updatedPhone, updatedDate, updatedSex
            ).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            // Đọc chuỗi string thô từ server trả về
                            String strJson = response.body().string();
                            JSONObject jsonObject = new JSONObject(strJson);
                            boolean success = jsonObject.optBoolean("success", false);
                            String message = jsonObject.optString("message", "Không có thông báo");

                            if (success) {
                                Toast.makeText(StudentDetailActivity.this, "Đã lưu thay đổi lên Database!", Toast.LENGTH_SHORT).show();

                                // Cập nhật cục bộ hiển thị ngay lập tức
                                currentStudent.setName(updatedName);
                                currentStudent.setAge(updatedAge);
                                currentStudent.setEmail(updatedEmail);
                                currentStudent.setClassName(updatedClass);
                                currentStudent.setAddress(updatedAddress);
                                currentStudent.setPhone(updatedPhone);
                                currentStudent.setBirthday(updatedDate);
                                currentStudent.setGender(updatedSex);

                                hienThiDuLieu();
                                setResult(RESULT_OK); // Đánh dấu để MainActivity biết để tự động load lại danh sách ngoài
                            } else {
                                // Nếu câu lệnh SQL ở file PHP bị crash hoặc trượt bản ghi, in thông báo lỗi thực tế lên
                                Toast.makeText(StudentDetailActivity.this, "Cập nhật thất bại: " + message, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(StudentDetailActivity.this, "Lỗi giải mã JSON phản hồi", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(StudentDetailActivity.this, "Lỗi kết nối hoặc SQL từ Server!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(StudentDetailActivity.this, "Lỗi kết nối mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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