package com.example.flashcard;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.flashcard.model.quiz.Quiz;
import com.example.flashcard.model.topic.Topic;
import com.example.flashcard.model.vocabulary.Vocabulary;
import com.example.flashcard.repository.ApiClient;
import com.example.flashcard.repository.UnsplashApiClient;
import com.example.flashcard.repository.UnsplashApiService;
import com.example.flashcard.utils.Constant;
import com.example.flashcard.utils.UnsplashResponse;
import com.example.flashcard.utils.Utils;
import com.google.android.material.button.MaterialButton;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TypingActivity extends AppCompatActivity {
    private UnsplashApiService apiService;
    private ImageButton closeBtn;
    private TextView quizProgressTxt;
    private Topic topic;
    private int questionCount = 1;
    private int totalQuestions = 0;
    private List<Vocabulary> vocabulariesList;
    private boolean shuffled = false;
    private Constant.Language studyLanguage;
    private List<Quiz> quizzesList;
    private List<Boolean> answersCorrectness;
    private ArrayList<String> chosenAnswers;
    private boolean instantFeedback = false;
    private boolean image = false;

    private Constant.StudyMode studyMode;
    private boolean currentAnswerMode = false;
    private int correctCount = 0;
    private int incorrectCount = 0;
    private Button skipBtn;
    private TextView questionTxt;
    private EditText answerTxt;
    private ImageView imageView;
    private Random rand;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_typing);

        apiService = UnsplashApiClient.getRetrofitInstance().create(UnsplashApiService.class);

        closeBtn = findViewById(R.id.closeBtn);
        quizProgressTxt = findViewById(R.id.quizProgressTxt);
        questionTxt = findViewById(R.id.questionTxt);
        skipBtn = findViewById(R.id.skipBtn);
        answerTxt = findViewById(R.id.answerTxt);
        imageView = findViewById(R.id.imageView);
        Intent intent = getIntent();
        vocabulariesList = intent.getParcelableArrayListExtra("vocabularies");
        totalQuestions = intent.getIntExtra("questionCount", 0);
        shuffled = intent.getBooleanExtra("shuffleQuestion", false);
        topic = intent.getParcelableExtra("topic");
        instantFeedback = intent.getBooleanExtra("instantFeedBack", false);
        image = intent.getBooleanExtra("image", false);
        studyMode = (Constant.StudyMode) intent.getSerializableExtra("studyMode");
        studyLanguage = (Constant.Language) intent.getSerializableExtra("studyLanguage");
        rand = new Random();

        closeBtn.setOnClickListener(v ->{
            finish();
        });

        answerTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(answerTxt.getText().toString().equals("") || answerTxt.getText().toString() == null){
                    skipBtn.setText("SKIP");
                }
                else{
                    skipBtn.setText("SUBMIT");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answerTxt.getText().toString().equals("") || answerTxt.getText().toString() == null) {
                    if(studyLanguage == Constant.Language.ENGLISH){
                        showSkippedDialog(quizzesList.get(questionCount - 1).getCorrectAnswer().getMeaning(), "SKIPPED");
                    }
                    else{
                        showSkippedDialog(quizzesList.get(questionCount - 1).getCorrectAnswer().getVocabulary(), "SKIPPED");
                    }
                }
                else{
                    String correctAnswer;
                    String answer = answerTxt.getText().toString();

                    if(studyLanguage == Constant.Language.ENGLISH){
                        correctAnswer  = quizzesList.get(questionCount - 1).getCorrectAnswer().getMeaning();
                    }
                    else{
                        correctAnswer = quizzesList.get(questionCount - 1).getCorrectAnswer().getVocabulary();
                    }
                    if(isInputMatch(answer, correctAnswer)){
                        showCorrectDialog();
                    }
                    else{
                        showWrongDialog(correctAnswer, answer);
                    }
                }
            }
        });

        quizzesList = Utils.generateQuizzes(vocabulariesList, shuffled).subList(0, totalQuestions);
        answersCorrectness = new ArrayList<>();
        chosenAnswers = new ArrayList<>();

        for(int i = 0; i < quizzesList.size(); i++){
            answersCorrectness.add(false);
            chosenAnswers.add("");
        }
        showQuestion();
    }

    private void showQuestion(){
        quizProgressTxt.setText(questionCount + "/" + totalQuestions);
        answerTxt.setText("");
        if(questionCount > totalQuestions){
            if (instantFeedback){
                Intent intent = new Intent(TypingActivity.this, FeedbackActivity.class);
                intent.putExtra("correctCount", correctCount);
                intent.putExtra("incorrectCount", incorrectCount);
                intent.putExtra("totalCount", totalQuestions);
                intent.putExtra("topic", topic);
                intent.putExtra("studyMode", studyMode);
                intent.putParcelableArrayListExtra("vocabularies", new ArrayList<>(vocabulariesList));
                intent.putParcelableArrayListExtra("quizzesList" , new ArrayList<>(quizzesList));
                boolean[] answersCorrectnessArray = new boolean[answersCorrectness.size()];
                for (int i = 0; i < answersCorrectness.size(); i++) {
                    answersCorrectnessArray[i] = answersCorrectness.get(i);
                }
                intent.putExtra("answersCorrectness", answersCorrectnessArray);
                intent.putStringArrayListExtra("chosenAnswers", chosenAnswers);
                startActivity(intent);
            }
            else{
                finish();
            }
        }
        else{
            if(image){
                Call<UnsplashResponse> call = apiService.searchPhotos(Constant.UNSPLASH_API_KEY, quizzesList.get(questionCount - 1).getCorrectAnswer().getVocabulary());
                Log.d("TEST TAG", Constant.UNSPLASH_API_KEY + " " + quizzesList.get(questionCount - 1).getCorrectAnswer().getVocabulary());
                call.enqueue(new Callback<UnsplashResponse>() {
                    @Override
                    public void onResponse(Call<UnsplashResponse> call, Response<UnsplashResponse> response) {
                        Log.d("image", response.toString());
                        if (response.isSuccessful() && response.body() != null && response.body().getResults().size() > 0) {
                            int randomImageIndex = rand.nextInt(response.body().getResults().size());
                            String imageUrl = response.body().getResults().get(randomImageIndex).getUrls().getRegular();
                            imageView.setVisibility(View.VISIBLE);
                            questionTxt.setVisibility(View.GONE);
                            Glide.with(TypingActivity.this)
                                    .load(imageUrl)
                                    .into(imageView);
                        }
                        else{
                            imageView.setVisibility(View.GONE);
                            questionTxt.setVisibility(View.VISIBLE);
                            String question = studyLanguage == Constant.Language.ENGLISH ?
                                    quizzesList.get(questionCount - 1).getCorrectAnswer().getVocabulary() :
                                    quizzesList.get(questionCount - 1).getCorrectAnswer().getMeaning();
                            questionTxt.setText(question);
                        }
                    }

                    @Override
                    public void onFailure(Call<UnsplashResponse> call, Throwable t) {
                        imageView.setVisibility(View.GONE);
                        questionTxt.setVisibility(View.VISIBLE);
                        String question = studyLanguage == Constant.Language.ENGLISH ?
                                quizzesList.get(questionCount - 1).getCorrectAnswer().getVocabulary() :
                                quizzesList.get(questionCount - 1).getCorrectAnswer().getMeaning();
                        questionTxt.setText(question);
                        Log.d("Test image 2", t.getMessage());

                    }
                });
            }
            String question = studyLanguage == Constant.Language.ENGLISH ?
                    quizzesList.get(questionCount - 1).getCorrectAnswer().getVocabulary() :
                    quizzesList.get(questionCount - 1).getCorrectAnswer().getMeaning();
            questionTxt.setText(question);
        }
    }
    private void showCorrectDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.success_answer_dialog);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.CENTER);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                    answersCorrectness.set(questionCount - 1, true);
                    chosenAnswers.set(questionCount - 1, studyLanguage == Constant.Language.ENGLISH ?
                            quizzesList.get(questionCount - 1).getCorrectAnswer().getMeaning() :
                            quizzesList.get(questionCount - 1).getCorrectAnswer().getVocabulary());
                    correctCount++;
                    questionCount++;
                    showQuestion();
                }
            }
        }, 2000);
    }

    private void showSkippedDialog(String correct, String yourAnswer) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.skip_answer_dialog);
        TextView correctAnswerTxt = dialog.findViewById(R.id.correctAnswerTxt);

        correctAnswerTxt.setText(correct);
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.CENTER);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                    chosenAnswers.set(questionCount - 1, yourAnswer);
                    incorrectCount++;
                    questionCount++;
                    showQuestion();
                }
            }
        }, 2000);
    }

    private void showWrongDialog(String correct, String yourAnswer) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.wrong_answer_dialog);
        TextView chosenWrongAnswerTxt = dialog.findViewById(R.id.chosenWrongAnswerTxt);
        TextView correctAnswerTxt = dialog.findViewById(R.id.correctAnswerTxt);

        chosenWrongAnswerTxt.setText(yourAnswer);
        correctAnswerTxt.setText(correct);
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.CENTER);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                    chosenAnswers.set(questionCount - 1, yourAnswer);
                    incorrectCount++;
                    questionCount++;
                    showQuestion();
                }
            }
        }, 2000);
    }

    private String normalizeString(String input) {
        return StringUtils.stripAccents(input).toLowerCase();
    }

    private boolean isInputMatch(String input, String target) {
        String normalizeInput = normalizeString(input);
        String normalizedTarget = normalizeString(target);

        return normalizeInput.equals(normalizedTarget);
    }
}