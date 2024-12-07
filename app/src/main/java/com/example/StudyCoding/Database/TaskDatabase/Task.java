package com.example.StudyCoding.Database.TaskDatabase;

public class Task {
    private String url;
    private String title;

    public Task(String url, String title) {
        this.url = url;
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    // 문제 번호를 추출하여 반환하는 메서드
    public String getProblemNumber() {
        if (url != null && url.contains("/")) {
            // 마지막 '/' 이후의 부분을 추출
            String[] parts = url.split("/");
            return parts[parts.length - 1];
        }
        return null; // URL이 유효하지 않을 경우 null 반환
    }
}
