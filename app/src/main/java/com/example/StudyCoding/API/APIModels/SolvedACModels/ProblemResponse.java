package com.example.StudyCoding.API.APIModels.SolvedACModels;

import com.google.gson.annotations.SerializedName;
import java.util.List;


public class ProblemResponse {
    @SerializedName("problemId")
    public int problemId;

    @SerializedName("titleKo")
    public String titleKo;

    @SerializedName("level")
    public int level;

    @SerializedName("tags")
    public List<Tag> tags;

    public static class Tag {
        @SerializedName("key")
        public String key;

        @SerializedName("displayNames")
        public List<DisplayName> displayNames;

        public static class DisplayName {
            @SerializedName("language")
            public String language;

            @SerializedName("name")
            public String name;

            @Override
            public String toString() {
                return "DisplayName{" +
                        "language='" + language + '\'' +
                        ", name='" + name + '\'' +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "Tag{" +
                    "key='" + key + '\'' +
                    ", displayNames=" + displayNames +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ProblemResponse{" +
                "problemId=" + problemId +
                ", titleKo='" + titleKo + '\'' +
                ", level=" + level +
                ", tags=" + tags +
                '}';
    }
}

