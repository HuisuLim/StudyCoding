package com.example.StudyCoding.LanguageRuleBook;


import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.markusressel.kodehighlighter.core.rule.LanguageRule;
import de.markusressel.kodehighlighter.core.rule.RuleMatch;

public class ControlKeywordRule implements LanguageRule {
    @NonNull
    @Override
    public List<RuleMatch> findMatches(@NonNull CharSequence charSequence) {
        List<RuleMatch> matches = new ArrayList<>();
        String regex = "\\b(if|else|while|for|return|public|void|protected|class|const|import)\\b"; // 제어 흐름 키워드
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(charSequence);

        while (matcher.find()) {
            matches.add(new RuleMatch(matcher.start(), matcher.end())); // 매칭 범위 저장
        }
        return matches;
    }
}
