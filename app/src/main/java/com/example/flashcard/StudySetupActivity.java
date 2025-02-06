package com.example.flashcard;

import android.content.Intent;
import android.os.Bundle;

import com.example.flashcard.model.topic.Topic;
import com.example.flashcard.model.vocabulary.Vocabulary;
import com.example.flashcard.utils.Constant;
import com.example.flashcard.utils.PromptOptionsListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.flashcard.databinding.ActivityStudySetupBinding;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

public class StudySetupActivity extends AppCompatActivity {
    private TextView topicName;
    private EditText questionCountEdt;
    private SwitchMaterial shuffleQuestionSwitch;
    private SwitchMaterial instantFeedBackSwitch;
    private SwitchMaterial imageSwitch;

    private TextView questionCountTxt;
    private Spinner languageSpinner;
    private Topic topic;
    private List<Vocabulary> vocabularies;
    private Constant.StudyMode studyMode;
    private Constant.Language studyLanguage = Constant.Language.ENGLISH;

    private ImageButton closeBtn;
    private MaterialButton startBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_setup);

        topicName = findViewById(R.id.topicName);
        questionCountEdt = findViewById(R.id.questionCountEdt);
        shuffleQuestionSwitch = findViewById(R.id.shuffleQuestionSwitch);
        instantFeedBackSwitch = findViewById(R.id.instantFeedBackSwitch);
        imageSwitch = findViewById(R.id.imageSwitch);
        questionCountTxt = findViewById(R.id.questionCountTxt);
        languageSpinner = findViewById(R.id.languageSpinner);
        closeBtn = findViewById(R.id.closeBtn);
        startBtn = findViewById(R.id.startBtn);

        closeBtn.setOnClickListener(view ->{
            finish();
        });
        Intent intent = getIntent();
        Topic data = intent.getParcelableExtra("topic");
        if (data != null) {
            studyMode = (Constant.StudyMode) intent.getSerializableExtra("studyMode");
            vocabularies = intent.getParcelableArrayListExtra("vocabularies");
            topic = data;
        } else {
            finish();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.drop_down_item, new String[]{"ENGLISH", "VIETNAMESE"});
        languageSpinner.setAdapter(adapter);
        questionCountTxt.setText("QUESTION COUNT + (" + vocabularies.size() + " max)");
        topicName.setText(topic.getTopicName());

        questionCountEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String value = editable.toString();
                if (!value.isEmpty()) {
                    try {
                        int count = Integer.parseInt(value);
                        if (count > topic.getVocabularyCount()) {
                            questionCountEdt.setText(String.valueOf(vocabularies.size()));
                        }
                    } catch (Exception e) {
                        questionCountEdt.setText("0");
                    }
                }
            }
        });

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0:
                        studyLanguage = Constant.Language.ENGLISH;
                        break;
                    case 1:
                        studyLanguage = Constant.Language.VIETNAMESE;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                studyLanguage = Constant.Language.ENGLISH;
            }
        });


        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (studyMode == Constant.StudyMode.Quiz) {
                    Intent quizIntent = new Intent(StudySetupActivity.this, QuizActivity.class);
                    boolean shuffleQuestion = shuffleQuestionSwitch.isChecked();
                    boolean instanceFeedBack = instantFeedBackSwitch.isChecked();
                    boolean image = imageSwitch.isChecked();

                    quizIntent.putExtra("topic", topic);
                    quizIntent.putExtra("studyLanguage", studyLanguage);
                    quizIntent.putExtra("questionCount", questionCountEdt.getText().toString().isEmpty() ? vocabularies.size() : Integer.parseInt(questionCountEdt.getText().toString()));
                    quizIntent.putExtra("shuffleQuestion", shuffleQuestion);
                    quizIntent.putParcelableArrayListExtra("vocabularies", new ArrayList<>(vocabularies));
                    quizIntent.putExtra("instantFeedBack", instanceFeedBack);
                    quizIntent.putExtra("image", image);
                    quizIntent.putExtra("studyMode", studyMode);
                    startActivity(quizIntent);
                } else if (studyMode == Constant.StudyMode.Typing) {
                    Intent typingIntent = new Intent(StudySetupActivity.this, TypingActivity.class);
                    boolean shuffleQuestion = shuffleQuestionSwitch.isChecked();
                    boolean instanceFeedBack = instantFeedBackSwitch.isChecked();
                    boolean image = imageSwitch.isChecked();

                    typingIntent.putExtra("topic", topic);
                    typingIntent.putExtra("studyLanguage", studyLanguage);
                    typingIntent.putExtra("questionCount", questionCountEdt.getText().toString().isEmpty() ? vocabularies.size() : Integer.parseInt(questionCountEdt.getText().toString()));
                    typingIntent.putExtra("shuffleQuestion", shuffleQuestion);
                    typingIntent.putParcelableArrayListExtra("vocabularies", new ArrayList<>(vocabularies));
                    typingIntent.putExtra("instantFeedBack", instanceFeedBack);
                    typingIntent.putExtra("image", image);
                    typingIntent.putExtra("studyMode", studyMode);
                    startActivity(typingIntent);
                }
            }
        });
    }

}