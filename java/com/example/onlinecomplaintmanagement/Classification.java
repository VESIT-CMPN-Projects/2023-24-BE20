package com.example.onlinecomplaintmanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class Classification extends AppCompatActivity {

    private TextView result, confidence;
    private ImageView imageView;
    private Button picture;
    private int imageSize = 224;
    private Interpreter tflite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classification);

        result = findViewById(R.id.result);
        confidence = findViewById(R.id.confidence);
        imageView = findViewById(R.id.imageView);
        picture = findViewById(R.id.takepicture);

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });

        // Load the TensorFlow Lite model when the activity is created
        try {
            tflite = new Interpreter(loadModelFile(), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private void classifyImage(Bitmap image) {
        // Preprocess the input image
        Bitmap resizedImage = Bitmap.createScaledBitmap(image, imageSize, imageSize, true);
        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(imageSize * imageSize * 3 * 4); // 4 bytes per float
        inputBuffer.order(ByteOrder.nativeOrder());
        inputBuffer.rewind();
        for (int y = 0; y < imageSize; y++) {
            for (int x = 0; x < imageSize; x++) {
                int px = resizedImage.getPixel(x, y);
                // Normalize pixel values and add to input buffer
                inputBuffer.putFloat(((px >> 16) & 0xFF) / 255.0f);
                inputBuffer.putFloat(((px >> 8) & 0xFF) / 255.0f);
                inputBuffer.putFloat((px & 0xFF) / 255.0f);
            }
        }

        // Perform inference
        float[][] outputScores = new float[1][2]; // Assuming the model outputs 2 classes
        tflite.run(inputBuffer, outputScores);

        // Postprocess the inference result
        float confidenceValue = outputScores[0][0]; // Example: Confidence for the first class
        String className = confidenceValue >= 0.5 ? "Potholes" : "Open Gutters";

        // Display the result and confidence
        result.setText("Result: " + className);
        confidence.setText("Confidence: " + (int)(confidenceValue * 100) + "%");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            imageView.setImageBitmap(image);
            classifyImage(image);
        }
    }

    @Override
    protected void onDestroy() {
        // Close the TensorFlow Lite interpreter to release resources
        tflite.close();
        super.onDestroy();
    }
}
