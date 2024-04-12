package com.example.onlinecomplaintmanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onlinecomplaintmanagement.R;
import com.example.onlinecomplaintmanagement.model.complaint;

import java.util.List;

public class ShowAllComplaintAdapter extends RecyclerView.Adapter<ShowAllComplaintAdapter.ViewHolder> {

    private Context mContext;
    private List<complaint> mList;

    public ShowAllComplaintAdapter(Context mContext, List<complaint> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.complaint_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        complaint complaint = mList.get(position);
        Glide.with(mContext).load(complaint.getImage()).into(holder.iv_complaintImage);
        holder.tv_department.setText(complaint.getDepartment());
        holder.tv_description.setText(complaint.getDescription());
        holder.tv_status.setText("Status: " + complaint.getStatus());
        holder.tv_reply.setText("Reply: " + complaint.getReply());
        holder.tv_priority.setText("Priority: " + complaint.getPriority()); // Display priority
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_complaintImage;
        TextView tv_department, tv_description, tv_status, tv_reply, tv_priority;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_complaintImage = itemView.findViewById(R.id.iv_complaintImage);
            tv_department = itemView.findViewById(R.id.tv_department);
            tv_description = itemView.findViewById(R.id.tv_complaintDescription);
            tv_status = itemView.findViewById(R.id.tv_complaintStatus);
            tv_reply = itemView.findViewById(R.id.tv_complaintReply);
            tv_priority = itemView.findViewById(R.id.tv_complaintPriority); // Initialize priority view
        }
    }
}
