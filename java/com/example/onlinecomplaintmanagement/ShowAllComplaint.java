package com.example.onlinecomplaintmanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinecomplaintmanagement.model.complaint;

import java.util.List;

public class ShowAllComplaint extends RecyclerView.Adapter<ShowAllComplaint.ComplaintViewHolder> {

    private Context mContext;
    private List<complaint> mComplaintList;

    public ShowAllComplaint(Context context, List<complaint> complaintList) {
        mContext = context;
        mComplaintList = complaintList;
    }

    @NonNull
    @Override
    public ComplaintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.complaint_list_item, parent, false);
        return new ComplaintViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintViewHolder holder, int position) {
        complaint currentComplaint = mComplaintList.get(position);
        holder.bind(currentComplaint);
    }

    @Override
    public int getItemCount() {
        return mComplaintList.size();
    }

    public class ComplaintViewHolder extends RecyclerView.ViewHolder {
        private TextView departmentTextView;
        private TextView descriptionTextView;
        private TextView statusTextView;
        private TextView replyTextView;

        public ComplaintViewHolder(@NonNull View itemView) {
            super(itemView);
            departmentTextView = itemView.findViewById(R.id.tv_department);
            descriptionTextView = itemView.findViewById(R.id.tv_complaintDescription);
            statusTextView = itemView.findViewById(R.id.tv_complaintStatus);
            replyTextView = itemView.findViewById(R.id.tv_complaintReply);
        }

        public void bind(complaint currentComplaint) {
            departmentTextView.setText(currentComplaint.getDepartment());
            descriptionTextView.setText(currentComplaint.getDescription());
            statusTextView.setText("Status: " + currentComplaint.getStatus());
            replyTextView.setText("Reply: " + currentComplaint.getReply());
        }
    }
}
