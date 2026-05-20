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

    private Context         context;
    private List<Student>   danhSach;
    private OnItemClickListener listener;

    // Interface click
    public interface OnItemClickListener {
        void onItemClick(Student student);
    }

    public StudentAdapter(Context context, List<Student> danhSach, OnItemClickListener listener) {
        this.context   = context;
        this.danhSach  = danhSach;
        this.listener  = listener;
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

        holder.tvName.setText(student.getName());
        holder.tvId.setText("MSSV: " + student.getId());
        holder.tvClass.setText("Lớp: " + student.getClassName());

        // Avatar chữ cái đầu
        String firstChar = student.getName() != null && !student.getName().isEmpty()
                ? String.valueOf(student.getName().charAt(0)).toUpperCase()
                : "?";
        holder.tvAvatar.setText(firstChar);

        // Màu avatar ngẫu nhiên theo vị trí
        // Màu avatar ngẫu nhiên theo vị trí
        String[] colors = {"#E53935", "#8E24AA", "#1E88E5",
                "#00897B", "#F4511E", "#6D4C41"}; // <-- Đã đổi thành String[]
        holder.tvAvatar.getBackground().setTint(
                android.graphics.Color.parseColor(colors[position % colors.length])
        );

        // Click item
        holder.itemView.setOnClickListener(v -> listener.onItemClick(student));
    }

    @Override
    public int getItemCount() {
        return danhSach != null ? danhSach.size() : 0;
    }

    // Cập nhật danh sách khi search
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