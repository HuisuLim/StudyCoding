package com.example.StudyCoding.Database.Database_Problem;

public class Problem {
    private int problemId;
    private String titleKo;
    private int level;
    private String tag1;
    private String tag2;
    private String tag3;
    private String tag4;
    private String tag5;

    public Problem(int problemId, String titleKo, int level, String tag1, String tag2, String tag3, String tag4, String tag5) {
        this.problemId = problemId;
        this.titleKo = titleKo;
        this.level = level;
        this.tag1 = tag1;
        this.tag2 = tag2;
        this.tag3 = tag3;
        this.tag4 = tag4;
        this.tag5 = tag5;
    }

    // Getter 메서드들...
    public int getProblemId() { return problemId; }
    public String getTitleKo() { return titleKo; }
    public int getLevel() { return level; }
    public String getTag1() { return tag1; }
    public String getTag2() { return tag2; }
    public String getTag3() { return tag3; }
    public String getTag4() { return tag4; }
    public String getTag5() { return tag5; }
}
