package com.example.StudyCoding.Models.BrowseAIModels;

import java.util.Map;

public class SimplifiedTaskResponse {
    private Result result;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public static class Result {
        private String id;
        private Map<String, String> capturedTexts;
        private InputParameters inputParameters;
        private CapturedScreenshots capturedScreenshots;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Map<String, String> getCapturedTexts() {
            return capturedTexts;
        }

        public void setCapturedTexts(Map<String, String> capturedTexts) {
            this.capturedTexts = capturedTexts;
        }

        public InputParameters getInputParameters() {
            return inputParameters;
        }

        public void setInputParameters(InputParameters inputParameters) {
            this.inputParameters = inputParameters;
        }

        public CapturedScreenshots getCapturedScreenshots() {
            return capturedScreenshots;
        }

        public void setCapturedScreenshots(CapturedScreenshots capturedScreenshots) {
            this.capturedScreenshots = capturedScreenshots;
        }

        public static class InputParameters {
            private String originUrl;

            public String getOriginUrl() {
                return originUrl;
            }

            public void setOriginUrl(String originUrl) {
                this.originUrl = originUrl;
            }
        }

        public static class CapturedScreenshots {
            private ProblemCapture problem_capture;

            public ProblemCapture getProblem_capture() {
                return problem_capture;
            }

            public void setProblem_capture(ProblemCapture problem_capture) {
                this.problem_capture = problem_capture;
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
