package com.example.StudyCoding.API.APIModels.BrowseAIModels;

import java.util.Map;

public class RobotTaskResponse {
    private int statusCode;
    private String messageCode;
    private Result result;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public static class Result {
        private String id;
        private String robotId;
        private String status;
        private Map<String, String> capturedTexts;
        private CapturedScreenshots capturedScreenshots;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getRobotId() {
            return robotId;
        }

        public void setRobotId(String robotId) {
            this.robotId = robotId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Map<String, String> getCapturedTexts() {
            return capturedTexts;
        }

        public void setCapturedTexts(Map<String, String> capturedTexts) {
            this.capturedTexts = capturedTexts;
        }

        public CapturedScreenshots getCapturedScreenshots() {
            return capturedScreenshots;
        }

        public void setCapturedScreenshots(CapturedScreenshots capturedScreenshots) {
            this.capturedScreenshots = capturedScreenshots;
        }

        public static class CapturedScreenshots {
            private ProblemCapture problemCapture;

            public ProblemCapture getProblemCapture() {
                return problemCapture;
            }

            public void setProblemCapture(ProblemCapture problemCapture) {
                this.problemCapture = problemCapture;
            }

            public static class ProblemCapture {
                private String src;

                public String getSrc() {
                    return src;
                }

                public void setSrc(String src) {
                    this.src = src;
                }
            }
        }
    }
}
