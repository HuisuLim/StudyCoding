package com.example.StudyCoding.LanguageRuleBook;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.markusressel.kodehighlighter.core.LanguageRuleBook;
import de.markusressel.kodehighlighter.core.RuleMatches;
import de.markusressel.kodehighlighter.core.colorscheme.ColorScheme;
import de.markusressel.kodehighlighter.core.rule.LanguageRule;
import de.markusressel.kodehighlighter.core.rule.RuleMatch;
import kotlin.coroutines.Continuation;

public class CustomLanguageRuleBook implements LanguageRuleBook {
    private final Set<LanguageRule> languageRuleSet;
    private final ColorScheme colorScheme;

    public CustomLanguageRuleBook(LanguageRule typeKeywordRule, LanguageRule controlKeywordRule) {
        this.languageRuleSet = new HashSet<LanguageRule>() {{
            add(typeKeywordRule);
            add(controlKeywordRule);
        }};
        this.colorScheme = new CustomColorScheme(typeKeywordRule, controlKeywordRule);
    }

    @NonNull
    @Override
    public ColorScheme getDefaultColorScheme() {
        return colorScheme;
    }

    @NonNull
    @Override
    public Set<LanguageRule> getRules() {
        return languageRuleSet;
    }

    @Nullable
    @Override
    public Object createHighlighting(@NonNull CharSequence charSequence, @NonNull Continuation<? super List<RuleMatches>> continuation) {
        List<RuleMatches> ruleMatchesList = new ArrayList<>();
        for (LanguageRule rule : languageRuleSet) {
            List<RuleMatch> matches = rule.findMatches(charSequence);
            if (!matches.isEmpty()) {
                ruleMatchesList.add(new RuleMatches(rule, matches));
            }
        }
        return ruleMatchesList;
    }
}

