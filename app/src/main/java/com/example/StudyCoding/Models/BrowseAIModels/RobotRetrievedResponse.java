package com.example.StudyCoding.Models.BrowseAIModels;

import java.util.Map;
public class RobotRetrievedResponse {
    private int statusCode; // JSON의 statusCode를 매핑
    private String messageCode; // JSON의 messageCode를 매핑
    private Result result;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessageCode() {
        return messageCode != null ? messageCode : "N/A"; // 기본값
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
        private String status; // "status" 필드 추가
        private Long finishedAt;
        private Map<String, String> capturedTexts;
        private InputParameters inputParameters;
        private CapturedScreenshots capturedScreenshots;

        public String getId() {
            return id != null ? id : "N/A"; // 기본값
        }

        public void setId(String id) {
            this.id = id;
        }
        public String getStatus() {
            return status != null ? status : "N/A"; // 기본값
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Long getFinishedAt() {
            return finishedAt;
        }

        public void setFinishedAt(int statusCode) {
            this.finishedAt = finishedAt;
        }


        public Map<String, String> getCapturedTexts() {
            return capturedTexts != null ? capturedTexts : Map.of(); // 빈 Map 반환
        }

        public void setCapturedTexts(Map<String, String> capturedTexts) {
            this.capturedTexts = capturedTexts;
        }

        public InputParameters getInputParameters() {
            return inputParameters != null ? inputParameters : new InputParameters(); // 기본 객체
        }

        public void setInputParameters(InputParameters inputParameters) {
            this.inputParameters = inputParameters;
        }

        public CapturedScreenshots getCapturedScreenshots() {
            return capturedScreenshots != null ? capturedScreenshots : new CapturedScreenshots(); // 기본 객체
        }

        public void setCapturedScreenshots(CapturedScreenshots capturedScreenshots) {
            this.capturedScreenshots = capturedScreenshots;
        }

        public static class InputParameters {
            private String originUrl;

            public String getOriginUrl() {
                return originUrl != null ? originUrl : "N/A"; // 기본값
            }

            public void setOriginUrl(String originUrl) {
                this.originUrl = originUrl;
            }
        }

        public static class CapturedScreenshots {
            private ProblemCapture problem_capture;

            public ProblemCapture getProblem_capture() {
                return problem_capture != null ? problem_capture : new ProblemCapture(); // 기본 객체
            }

            public void setProblem_capture(ProblemCapture problem_capture) {
                this.problem_capture = problem_capture;
            }

            public static class ProblemCapture {
                private String src;

                public String getSrc() {
                    return src != null ? src : "N/A"; // 기본값
                }

                public void setSrc(String src) {
                    this.src = src;
                }
            }
        }
    }
}
