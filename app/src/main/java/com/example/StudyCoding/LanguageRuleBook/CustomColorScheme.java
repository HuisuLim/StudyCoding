package com.example.StudyCoding.LanguageRuleBook;

import android.graphics.Color;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.markusressel.kodehighlighter.core.colorscheme.ColorScheme;
import de.markusressel.kodehighlighter.core.rule.LanguageRule;
import kotlin.jvm.functions.Function0;

public class CustomColorScheme implements ColorScheme {
    private final LanguageRule typeKeywordRule;
    private final LanguageRule controlKeywordRule;
    private final LanguageRule ioKeywordRule;

    public CustomColorScheme(LanguageRule typeKeywordRule, LanguageRule controlKeywordRule, LanguageRule ioKeywordRule) {
        this.typeKeywordRule = typeKeywordRule;
        this.controlKeywordRule = controlKeywordRule;
        this.ioKeywordRule = ioKeywordRule;
    }

    @NonNull
    @Override
    public Set<Function0<CharacterStyle>> getStyles(@NonNull LanguageRule languageRule) {
        if (languageRule == typeKeywordRule) {
            // 타입 키워드 스타일 (파란색)
            return new HashSet<Function0<CharacterStyle>>() {{
                add(() -> new ForegroundColorSpan(Color.BLUE));
            }};
        } else if (languageRule == controlKeywordRule) {
            // 제어 흐름 키워드 스타일 (빨간색)
            return new HashSet<Function0<CharacterStyle>>() {{
                add(() -> new ForegroundColorSpan(Color.RED));
            }};
        } else if (languageRule == ioKeywordRule) {
            // 제어 흐름 키워드 스타일 (빨간색)
            return new HashSet<Function0<CharacterStyle>>() {{
                add(() -> new ForegroundColorSpan(Color.MAGENTA));
            }};
        }
        return Collections.emptySet();
    }
}
