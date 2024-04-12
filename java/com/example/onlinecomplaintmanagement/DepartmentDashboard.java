package com.example.onlinecomplaintmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinecomplaintmanagement.adapter.ShowAllComplaintAdapter;
import com.example.onlinecomplaintmanagement.model.complaint;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DepartmentDashboard extends AppCompatActivity {

    private RecyclerView rvDepartmentComplaints;
    private ShowAllComplaintAdapter adapter;
    private List<complaint> complaintList = new ArrayList<>();
    private Button btnAddComplaint; // Declare the Button variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_dashboard);

        rvDepartmentComplaints = findViewById(R.id.rv_department_complaints);
        rvDepartmentComplaints.setHasFixedSize(true);
        rvDepartmentComplaints.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ShowAllComplaintAdapter(this, complaintList);
        rvDepartmentComplaints.setAdapter(adapter);

        loadComplaintsFromMainActivity();

        btnAddComplaint = findViewById(R.id.btn_add_complaint); // Initialize the Button

        // Set OnClickListener for the button
        btnAddComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open new activity when button is clicked
                Intent intent = new Intent(DepartmentDashboard.this, NewComplaintActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadComplaintsFromMainActivity() {
        FirebaseDatabase.getInstance().getReference("complaints")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        complaintList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            complaint model = dataSnapshot.getValue(complaint.class);
                            if (model != null) {
                                complaintList.add(model);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DepartmentDashboard.this, "Failed to load complaints", Toast.LENGTH_SHORT).show();
                        Log.e("DepartmentDashboard", "Failed to load complaints: " + error.getMessage());
                    }
                });
    }
}