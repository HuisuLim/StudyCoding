package com.example.StudyCoding.API.APIModels.Judge0Models;


public class SubmissionRequest {
    private String source_code;
    private int language_id;
    private String stdin;
    private String expected_output;

    public SubmissionRequest(String source_code, int language_id, String stdin, String expected_output) {
        this.source_code = source_code;
        this.language_id = language_id;
        this.stdin = stdin;
        this.expected_output = expected_output;
    }

}
