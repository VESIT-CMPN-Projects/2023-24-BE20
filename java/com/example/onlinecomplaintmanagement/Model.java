//package com.example.onlinecomplaintmanagement;
//
//import android.content.Context;
//
//import androidx.annotation.NonNull;
//import org.tensorflow.lite.DataType;
//import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//public class Model {
//    @NonNull
//    private final org.tensorflow.lite.support.model.Model model;
//
//    private Model(@NonNull Context context,
//                  @NonNull org.tensorflow.lite.support.model.Model.Options options) throws IOException {
//        model = org.tensorflow.lite.support.model.Model.createModel(context, "C:\\Users\\26sai\\Downloads\\Online Complaint Management\\alert\\app\\src\\main\\ml\\model.tflite", options);
//
//    }
//
//    @NonNull
//    public static Model newInstance(@NonNull Context context) throws IOException {
//        return new Model(context, (new org.tensorflow.lite.support.model.Model.Options.Builder()).build());
//    }
//
//    @NonNull
//    public Outputs process(@NonNull TensorBuffer inputBuffer) {
//        Outputs outputs = new Outputs(model);
//        model.run(new Object[] {inputBuffer}, outputs.getBuffer());
//        return outputs;
//    }
//
//    public void close() {
//        model.close();
//    }
//
//    public class Outputs {
//        private TensorBuffer outputFeature0;
//
//        private Outputs(org.tensorflow.lite.support.model.Model model) {
//            this.outputFeature0 = TensorBuffer.createFixedSize(model.getOutputTensorShape(0), DataType.FLOAT32);
//        }
//
//        @NonNull
//        public TensorBuffer getOutputFeature0AsTensorBuffer() {
//            return outputFeature0;
//        }
//
//        @NonNull
//        private Map<Integer, Object> getBuffer() {
//            Map<Integer, Object> outputs = new HashMap<>();
//            outputs.put(0, outputFeature0.getBuffer());
//            return outputs;
//        }
//    }
//}
