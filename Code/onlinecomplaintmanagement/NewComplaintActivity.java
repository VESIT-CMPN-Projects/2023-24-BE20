package com.example.onlinecomplaintmanagement;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinecomplaintmanagement.model.complaint;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NewComplaintActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_REQUEST_CODE = 123;

    private RecyclerView rvComplaintList;
    private ToggleButtonkaAdapter adapter;
    private List<complaint> complaintList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_list_new);

        rvComplaintList = findViewById(R.id.rvComplaintList);
        rvComplaintList.setHasFixedSize(true);
        rvComplaintList.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ToggleButtonkaAdapter(this, complaintList);
        rvComplaintList.setAdapter(adapter);

        loadComplaintsFromFirebase();
    }

    private void loadComplaintsFromFirebase() {
        FirebaseDatabase.getInstance().getReference("complaints")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        complaintList.clear();
                        int count = 1;
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            complaint model = dataSnapshot.getValue(complaint.class);
                            if (model != null) {
                                model.setComplaintNumber("Complaint Number " + count);
                                complaintList.add(model);
                                count++;
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle database error
                    }
                });
    }

    // Method to send SMS
    private void sendSMS(String phoneNumber, String message) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // Request permission if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
            return;
        }

        // Use SmsManager to send SMS
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, send the SMS
                sendSMS("YOUR_STATIC_PHONE_NUMBER", "Complaint has been marked as solved.");
            } else {
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}