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

public class Complaint extends RecyclerView.Adapter<Complaint.ViewHolder> {

        private Context mContext;
        private List<complaint> mComplaintList;

        public Complaint(Context context, List<complaint> complaintList) {
                mContext = context;
                mComplaintList = complaintList;
        }

    public Complaint(String s, String s1, boolean b) {
    }


    @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.complaint_list_item, parent, false);
                return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                complaint complaint = mComplaintList.get(position);
                holder.bind(complaint);
        }

        @Override
        public int getItemCount() {
                return mComplaintList.size();
        }

        public void setComplaintNumber(String s) {
        }

    public String getDescription() {
        String description = null;
        return null;
    }

    public boolean isSolved() {
        return false;
    }

    public void setSolved(boolean isChecked) {
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

                private TextView tvDepartment;
                private TextView tvDescription;
                private TextView tvStatus;
                private String complaintNumber;

                public ViewHolder(@NonNull View itemView) {
                        super(itemView);

                        tvDepartment = itemView.findViewById(R.id.tv_department);
                        //tvDescription = itemView.findViewById(R.id.tv_description);
                        //tvStatus = itemView.findViewById(R.id.tv_status);
                }

                public void bind(complaint complaint) {
                        tvDepartment.setText(complaint.getDepartment());
                        tvDescription.setText(complaint.getDescription());
                        tvStatus.setText(complaint.getStatus());
                }


        }
}