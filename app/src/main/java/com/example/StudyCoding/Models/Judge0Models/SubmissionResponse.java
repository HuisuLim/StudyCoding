package com.example.StudyCoding.Models.Judge0Models;

public class SubmissionResponse {
    private String token;
    private String stdout;
    private String stderr;
    private String compile_output;
    //private String
    private int status_id; // 상태 ID
    private int language_id; // 언어 ID


    // Getters
    public String getToken() {
        return token;
    }


    public String getStdout() {
        return stdout;
    }

    public String getStderr() {
        return stderr;
    }

    public String getCompileOutput(){return compile_output;}

    public int getStatusId() {
        return status_id;
    }

    public int getLanguageId() {return language_id;}


}
