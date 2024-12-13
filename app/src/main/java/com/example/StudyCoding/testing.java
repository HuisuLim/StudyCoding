package com.example.StudyCoding;

import android.graphics.Color;
import android.os.Bundle;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.StudyCoding.LanguageRuleBook.ControlKeywordRule;
import com.example.StudyCoding.LanguageRuleBook.CustomLanguageRuleBook;
import com.example.StudyCoding.LanguageRuleBook.TypeKeywordRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.markusressel.kodeeditor.library.view.CodeEditorLayout;
import de.markusressel.kodehighlighter.core.LanguageRuleBook;
import de.markusressel.kodehighlighter.core.RuleMatches;
import de.markusressel.kodehighlighter.core.colorscheme.ColorScheme;
import de.markusressel.kodehighlighter.core.rule.LanguageRule;

import de.markusressel.kodehighlighter.core.rule.RuleMatch;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function0;

public class testing extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testing);
        // CodeEditorLayout 초기화
        CodeEditorLayout codeEditorLayout = findViewById(R.id.codeEditorView);
        // LanguageRuleBook 생성
        LanguageRuleBook languageRuleBook2 = new CustomLanguageRuleBook(
                new TypeKeywordRule(),
                new ControlKeywordRule()
        );


// CodeEditorLayout에 적용
        codeEditorLayout.setLanguageRuleBook(languageRuleBook2);



        // 샘플 코드 텍스트
        String sampleCode = "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello, World!\");\n" +
                "    }\n" +
                "}";
        codeEditorLayout.setText(sampleCode);
        String hi = codeEditorLayout.getText();
        Log.d("hi", hi);


    }
}
