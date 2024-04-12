package com.example.onlinecomplaintmanagement;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onlinecomplaintmanagement.adapter.ShowAllComplaintAdapter;
import com.example.onlinecomplaintmanagement.model.UserModel;
import com.example.onlinecomplaintmanagement.model.complaint;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, LocationListener {

    private static final int CAMERA_PERMISSION_CODE = 101;
    String[] departments = {"Garbage", "Water", "Road"};

    private FloatingActionButton fab_createComplaint;
    private ProgressDialog progressDialog;
    private Uri imageUri;
    private String imageString;
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private UserModel model;
    private String department = "";
    private ImageView iv_complaintImage;
    private List<complaint> complaintList = new ArrayList<>();
    private RecyclerView rvComplaints;
    private ShowAllComplaintAdapter adapter;
    private LocationManager locationManager;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvComplaints = findViewById(R.id.rv_showAllFood);
        rvComplaints.setHasFixedSize(true);
        rvComplaints.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        progressDialog = new ProgressDialog(MainActivity.this);

        fab_createComplaint = findViewById(R.id.fab_createFood);
        fab_createComplaint.setOnClickListener(v -> getUser());

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Load existing complaints upon activity creation
        loadExistingComplaints();
    }

    private void loadExistingComplaints() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(RegisterActivity.COMPLAINT);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    complaintList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        complaint model = dataSnapshot.getValue(complaint.class);
                        complaintList.add(model);
                    }
                    adapter = new ShowAllComplaintAdapter(MainActivity.this, complaintList);
                    rvComplaints.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseError", "Error loading complaints: " + error.getMessage());
                }
            });
        }
    }

    private void getUser() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(RegisterActivity.USERS).child(firebaseUser.getUid());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    model = snapshot.getValue(UserModel.class);
                    if (model != null) {
                        createDialog();
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to get user data", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseError", "Error getting user data: " + error.getMessage());
                }
            });
        }
    }

    private void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.alert_add_complaint, null, false);
        builder.setView(view);

        EditText et_description = view.findViewById(R.id.et_description);
        iv_complaintImage = view.findViewById(R.id.iv_complaintImage);
        Spinner sp_department = view.findViewById(R.id.sp_department);
        Button btn_create = view.findViewById(R.id.btn_addComplaint);
        sp_department.setOnItemSelectedListener(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departments);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_department.setAdapter(adapter);

        AlertDialog alertDialog = builder.create();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Uploads");

        iv_complaintImage.setOnClickListener(v -> checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE));

        alertDialog.show();
        btn_create.setOnClickListener(v -> {
            String complaintDescription = et_description.getText().toString();
            String complaintImage = imageString;
            if (complaintDescription.isEmpty()) {
                et_description.setError("Empty description");
            } else {
                progressDialog.setMessage("Adding Your Complaint");
                progressDialog.setTitle("Adding...");
                progressDialog.setCanceledOnTouchOutside(false);
                createComplaint(complaintDescription, department, complaintImage);
                alertDialog.dismiss();
            }
        });
    }

    private void createComplaint(String complaintDescription, String department, String complaintImage) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = firebaseUser.getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(RegisterActivity.COMPLAINT);

        complaint newComplaint = new complaint();
        newComplaint.setDepartment(department);
        newComplaint.setId(userId);
        newComplaint.setUserName(model.getUserName());
        newComplaint.setStatus("pending");
        newComplaint.setReply("pending");
        newComplaint.setDescription(complaintDescription);
        newComplaint.setImage(complaintImage);
        newComplaint.setLatitude(latitude);
        newComplaint.setLongitude(longitude);

        // Set priority based on department
        int priority = getPriorityForDepartment(department);
        newComplaint.setPriority(priority);

        reference.push().setValue(newComplaint).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                // Reload the complaint list upon successful addition of new complaint
                loadExistingComplaints();
                Toast.makeText(MainActivity.this, "Complaint Added Successfully", Toast.LENGTH_SHORT).show();
                imageString = "";
            } else {
                Toast.makeText(MainActivity.this, "Complaint Creation Failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Log.e("FirebaseError", "Error creating complaint: " + e.getMessage());
            Toast.makeText(MainActivity.this, "Error creating complaint: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private int getPriorityForDepartment(String department) {
        int priority = 0;
        switch (department) {
            case "Road":
                priority = 1;
                break;
            case "Water":
                priority = 2;
                break;
            case "Garbage":
                priority = 3;
                break;
        }
        return priority;
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
        } else {
            if (permission.equals(Manifest.permission.CAMERA)) {
                openCamera();
            }
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_PERMISSION_CODE);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        department = departments[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        department = departments[0];
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_PERMISSION_CODE) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                iv_complaintImage.setImageBitmap(bitmap);
                imageUri = getImageUri(getApplicationContext(), bitmap);
                uploadImage();
            }
        }
    }

    private void uploadImage() {
        progressDialog.setMessage("Uploading Image...");
        progressDialog.show();

        if (imageUri != null) {
            StorageReference fileReference = FirebaseStorage.getInstance().getReference().child("uploads")
                    .child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            UploadTask uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return fileReference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    imageString = downloadUri.toString();
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("FirebaseError", "Upload failed: " + task.getException().getMessage());
                }
            });
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private Uri getImageUri(Context applicationContext, Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String path = MediaStore.Images.Media.insertImage(applicationContext.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }
}
