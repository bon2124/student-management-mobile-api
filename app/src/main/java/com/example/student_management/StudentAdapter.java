package com.example.student_management;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.student_management.model.Student;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    private Context context;
    private List<Student> danhSach;
    private OnItemClickListener listener;

    // Interface dùng để bắt sự kiện click vào từng dòng sinh viên
    public interface OnItemClickListener {
        void onItemClick(Student student);
    }

    public StudentAdapter(Context context, List<Student> danhSach, OnItemClickListener listener) {
        this.context = context;
        this.danhSach = danhSach;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_student, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Student student = danhSach.get(position);

        // Đổ dữ liệu text cơ bản lên view
        holder.tvName.setText(student.getName());

        // ĐỒNG BỘ NODE.JS: Thay getId() thành getStudentCode() để hiển thị mã SV đẹp (ví dụ: SV001)
        holder.tvId.setText("MSSV: " + student.getStudentCode());
        holder.tvClass.setText("Lớp: " + student.getClassName());

        // XỬ LÝ LẤY CHỮ CÁI ĐẦU TIÊN CỦA TỪ CUỐI CÙNG TRONG TÊN
        String fullName = student.getName();
        String firstLetter = "?";

        if (fullName != null && !fullName.trim().isEmpty()) {
            fullName = fullName.trim();
            int lastSpaceIndex = fullName.lastIndexOf(" ");

            if (lastSpaceIndex != -1) {
                // Trường hợp tên có nhiều từ (Ví dụ: "Lê Tuấn Anh" -> lấy "Anh")
                String lastName = fullName.substring(lastSpaceIndex + 1);
                if (!lastName.isEmpty()) {
                    firstLetter = String.valueOf(lastName.charAt(0)).toUpperCase();
                }
            } else {
                // Trường hợp tên chỉ có đúng 1 từ (Ví dụ: "Khoa" -> lấy chữ "K")
                firstLetter = String.valueOf(fullName.charAt(0)).toUpperCase();
            }
        }

        // Hiển thị chữ cái đã bóc tách lên khung tròn avatar
        holder.tvAvatar.setText(firstLetter);

        // Đổ màu nền ngẫu nhiên/tuần hoàn cho vòng tròn avatar theo vị trí item
        String[] colors = {"#E53935", "#8E24AA", "#1E88E5", "#00897B", "#F4511E", "#6D4C41"};
        if (holder.tvAvatar.getBackground() != null) {
            holder.tvAvatar.getBackground().setTint(
                    android.graphics.Color.parseColor(colors[position % colors.length])
            );
        }

        // Bắt sự kiện click vào item để chuyển sang trang chi tiết
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(student);
            }
        });
    }

    @Override
    public int getItemCount() {
        return danhSach != null ? danhSach.size() : 0;
    }

    // Hàm cập nhật lại danh sách dữ liệu và làm mới giao diện khi tìm kiếm (Local Search)
    public void updateList(List<Student> newList) {
        this.danhSach = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAvatar, tvName, tvId, tvClass;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tvAvatar);
            tvName   = itemView.findViewById(R.id.tvName);
            tvId     = itemView.findViewById(R.id.tvId);
            tvClass  = itemView.findViewById(R.id.tvClass);
        }
    }
}