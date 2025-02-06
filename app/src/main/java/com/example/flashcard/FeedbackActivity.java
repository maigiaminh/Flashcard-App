package com.example.flashcard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.flashcard.adapter.FeedbackResultAdapter;
import com.example.flashcard.model.quiz.Quiz;
import com.example.flashcard.model.results.ResultData;
import com.example.flashcard.model.topic.Topic;
import com.example.flashcard.model.vocabulary.Vocabulary;
import com.example.flashcard.repository.ApiClient;
import com.example.flashcard.utils.Constant;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FeedbackActivity extends AppCompatActivity {
    private Constant.Language studyLanguage;
    private List<Vocabulary> vocabulariesList;
    private List<Boolean> answerCorrectness;
    private List<String> chosenAnswers;
    private List<Quiz> quizzesList;
    private Topic topic;
    private List<ResultData> resultDataList;
    private FeedbackResultAdapter resultDataAdapter;

    private ProgressBar correctProgressBar;
    private TextView correctCountTxt;
    private TextView wrongCountTxt;
    private TextView progressTxt;
    private TextView resultFeedbackTxt;
    private TextView feedBackTxt;
    private MaterialButton flashcardBtn;
    private Constant.StudyMode studyMode;

    private MaterialButton tryAgainBtn;
    private RecyclerView answerRecyclerView;
    private ImageButton closeBtn;
    private MaterialButton quizOrTypingBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        correctProgressBar = findViewById(R.id.correctProgressBar);
        correctCountTxt = findViewById(R.id.correctCountTxt);
        wrongCountTxt = findViewById(R.id.wrongCountTxt);
        progressTxt = findViewById(R.id.progressTxt);
        resultFeedbackTxt = findViewById(R.id.resultFeedbackTxt);
        feedBackTxt = findViewById(R.id.feedBackTxt);
        flashcardBtn = findViewById(R.id.flashcardBtn);
        tryAgainBtn = findViewById(R.id.tryAgainBtn);
        closeBtn = findViewById(R.id.closeBtn);
        answerRecyclerView = findViewById(R.id.answerRecyclerView);
        quizOrTypingBtn = findViewById(R.id.learnByQuizOrTyping);

        Intent intent = getIntent();
        vocabulariesList = intent.getParcelableArrayListExtra("vocabularies");
        quizzesList = intent.getParcelableArrayListExtra("quizzesList");
        boolean[] answersCorrectnessArray = getIntent().getBooleanArrayExtra("answersCorrectness");
        answerCorrectness = new ArrayList<>();
        for (boolean value : answersCorrectnessArray) {
            answerCorrectness.add(value);
        }
        chosenAnswers = intent.getStringArrayListExtra("chosenAnswers");
        topic = intent.getParcelableExtra("topic");
        studyMode = (Constant.StudyMode) intent.getSerializableExtra("studyMode");
        int correctCount = intent.getIntExtra("correctCount", 0);
        int incorrectCount = intent.getIntExtra("incorrectCount", 0);
        int totalCount = intent.getIntExtra("totalCount", 0);
        int percentage = (correctCount * 100) / totalCount;
        correctProgressBar.setProgress(percentage);
        progressTxt.setText(percentage + "%");
        correctCountTxt.setText(correctCount + " Corrects");
        wrongCountTxt.setText(incorrectCount + " Wrongs");
        studyResult(percentage);
        resultDataList = new ArrayList<>();
        for (int i = 0; i < quizzesList.size(); i++) {
            Quiz quiz = quizzesList.get(i);
            ResultData resultData = new ResultData(answerCorrectness.get(i), chosenAnswers.get(i), "", "");
            if (studyLanguage == Constant.Language.ENGLISH) {
                resultData.setQuestion(quiz.getCorrectAnswer().getVocabulary());
                resultData.setAnswer(quiz.getCorrectAnswer().getMeaning());
            } else {
                resultData.setQuestion(quiz.getCorrectAnswer().getMeaning());
                resultData.setAnswer(quiz.getCorrectAnswer().getVocabulary());
            }
            resultDataList.add(resultData);
        }

        if(studyMode == Constant.StudyMode.Quiz){
            quizOrTypingBtn.setText("LEARN BY TYPING");
            quizOrTypingBtn.setIcon(getDrawable(R.drawable.typing));
            quizOrTypingBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FeedbackActivity.this, StudySetupActivity.class);
                    intent.putExtra("topic", topic);
                    intent.putExtra("studyMode", Constant.StudyMode.Typing);
                    intent.putParcelableArrayListExtra("vocabularies", new ArrayList<>(vocabulariesList));
                    startActivity(intent);
                }
            });
        } else if (studyMode == Constant.StudyMode.Typing) {
            quizOrTypingBtn.setText("LEARN BY QUIZ");
            quizOrTypingBtn.setIcon(getDrawable(R.drawable.quiz));
            quizOrTypingBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FeedbackActivity.this, StudySetupActivity.class);
                    intent.putExtra("topic", topic);
                    intent.putExtra("studyMode", Constant.StudyMode.Quiz);
                    intent.putParcelableArrayListExtra("vocabularies", new ArrayList<>(vocabulariesList));
                    startActivity(intent);
                }
            });
        }

        flashcardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedbackActivity.this, FlashcardActivity.class);
                intent.putExtra("topic", topic);
                intent.putExtra("studyMode", Constant.StudyMode.Quiz);
                intent.putParcelableArrayListExtra("vocabularies", new ArrayList<>(vocabulariesList));
                startActivity(intent);
            }
        });

        tryAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(studyMode == Constant.StudyMode.Quiz){
                    Intent intent = new Intent(FeedbackActivity.this, StudySetupActivity.class);
                    intent.putExtra("topic", topic);
                    intent.putExtra("studyMode", Constant.StudyMode.Quiz);
                    intent.putParcelableArrayListExtra("vocabularies", new ArrayList<>(vocabulariesList));
                    startActivity(intent);
                }
                else if(studyMode == Constant.StudyMode.Typing){
                    Intent intent = new Intent(FeedbackActivity.this, StudySetupActivity.class);
                    intent.putExtra("topic", topic);
                    intent.putExtra("studyMode", Constant.StudyMode.Typing);
                    intent.putParcelableArrayListExtra("vocabularies", new ArrayList<>(vocabulariesList));
                    startActivity(intent);
                }
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedbackActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        resultDataAdapter = new FeedbackResultAdapter(this, resultDataList, R.layout.result_data_layout);
        answerRecyclerView.setHasFixedSize(true);
        answerRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        answerRecyclerView.setAdapter(resultDataAdapter);
    }

    private void studyResult(int percentage){
        if (percentage > 99) {
            resultFeedbackTxt.setText("EXCELLENT");
            feedBackTxt.setText("You did a perfect job");
        } else if (percentage > 90) {
            resultFeedbackTxt.setText("GREAT");
            feedBackTxt.setText("Awesome performance!");
        } else if (percentage > 70) {
            resultFeedbackTxt.setText("GOOD");
            feedBackTxt.setText("Well done!");
        } else if (percentage > 50) {
            resultFeedbackTxt.setText("OKAY");
            feedBackTxt.setText("You're getting there!");
        } else {
            resultFeedbackTxt.setText("KEEP TRYING");
            feedBackTxt.setText("Keep practicing, you'll improve!");
        }
    }
}