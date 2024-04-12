package com.example.onlinecomplaintmanagement;

import android.content.Context;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinecomplaintmanagement.model.complaint;

import java.util.List;

public class ToggleButtonkaAdapter extends RecyclerView.Adapter<ToggleButtonkaAdapter.ViewHolder> {

    private Context mContext;
    private List<complaint> mList;

    public ToggleButtonkaAdapter(Context mContext, List<complaint> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_complaint_list_new, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        complaint complaint = mList.get(position);

        holder.tvComplaintDetails.setText(complaint.getDescription());
        holder.tvPriority.setText("Priority: " + complaint.getPriority()); // Set priority
        holder.toggleStatus.setChecked(complaint.isSolved());

        // Handle toggle button state change
        holder.toggleStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Update the solved status of the complaint
            complaint.setSolved(isChecked);

            if (isChecked) {
                // Send SMS if the toggle button is checked
                sendSMS("8452050946", "Complaint has been marked as solved.");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvComplaintDetails;
        TextView tvPriority;
        ToggleButton toggleStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvComplaintDetails = itemView.findViewById(R.id.tvComplaintDescription);
            tvPriority = itemView.findViewById(R.id.tvComplaintPriority);
            toggleStatus = itemView.findViewById(R.id.toggleStatus);
        }
    }

    // Method to send SMS
    private void sendSMS(String phoneNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }
}