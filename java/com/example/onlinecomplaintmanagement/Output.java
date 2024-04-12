package com.example.onlinecomplaintmanagement;

public class Output {

    public class OutputClass {
        private String className;
        private float confidence;

        public OutputClass(String className, float confidence) {
            this.className = className;
            this.confidence = confidence;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public float getConfidence() {
            return confidence;
        }

        public void setConfidence(float confidence) {
            this.confidence = confidence;
        }
    }

}
