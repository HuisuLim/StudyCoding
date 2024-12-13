package com.example.StudyCoding.Models.BrowseAIModels;

import java.util.Map;


    public class RobotTaskRequest {
        private Map<String, Object> inputParameters;

        public RobotTaskRequest(Map<String, Object> inputParameters) {
            this.inputParameters = inputParameters;
        }

        public Map<String, Object> getInputParameters() {
            return inputParameters;
        }

        public void setInputParameters(Map<String, Object> inputParameters) {
            this.inputParameters = inputParameters;
        }
    }

