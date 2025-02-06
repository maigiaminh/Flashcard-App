package com.example.flashcard;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashcard.adapter.VocabularyInFlashcardAdapter;
import com.example.flashcard.model.topic.DeleteTopic;
import com.example.flashcard.model.topic.Topic;
import com.example.flashcard.model.topic.TopicResponse;
import com.example.flashcard.model.topic.Topics;
import com.example.flashcard.model.topic.TopicsFormUserResponse;
import com.example.flashcard.model.user.User;
import com.example.flashcard.model.user.UserFromTopicResponse;
import com.example.flashcard.model.vocabulary.VocabulariesFromTopicResponse;
import com.example.flashcard.model.vocabulary.Vocabulary;
import com.example.flashcard.repository.ApiClient;
import com.example.flashcard.repository.ApiService;
import com.example.flashcard.utils.Constant;
import com.example.flashcard.utils.OnTopicDialogListener;
import com.example.flashcard.utils.Utils;
import com.example.flashcard.viewmodel.TopicViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import kotlinx.coroutines.CoroutineScope;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

public class TopicActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, OnTopicDialogListener {
    private TopicViewModel topicViewModel;
    private ApiService apiService;
    private Topic topic;
    private List<Vocabulary> vocabulariesList;
    private boolean isOwner;
    private List<Vocabulary> originalVocabulariesList;
    private SharedPreferences sharedPreferences;
    private ActivityResultLauncher<Intent> addTopicToFolderResultLauncher;
    private ActivityResultLauncher<Intent> editTopicVocabulariesResultLauncher;

    private User user;
    private VocabularyInFlashcardAdapter vocabularyInFlashcardAdapter;
    private TextToSpeech ttsEnglish;
    private TextToSpeech ttsVietnamese;

    private ImageButton optionMenuBtn;
    private ImageButton returnBtn;
    private ViewPager2 flashCardViewPager;
    private MaterialButton learnByFlashCardBtn;
    private MaterialButton learnByQuizBtn;
    private MaterialButton learnByTypingBtn;
    private ShapeableImageView topicUserImg;
    private TextView topicUserName;
    private TextView topicName;
    private TextView topicDesc;
    private ProgressBar fullScreenProgressBar;
    private ScrollingPagerIndicator scrollPagerIndicator;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        sharedPreferences = getSharedPreferences(Constant.SHARE_PREF, MODE_PRIVATE);
        user = Utils.getUserFromSharedPreferences(this, sharedPreferences);
        Intent intent = getIntent();
        Topic data = intent.getParcelableExtra("topic");
        ttsEnglish = new TextToSpeech(this, this);
        ttsVietnamese = new TextToSpeech(this, this);
        optionMenuBtn = findViewById(R.id.optionMenuBtn);
        returnBtn = findViewById(R.id.returnBtn);
        ActivityResult addTopicToFolderResultLauncher;
        flashCardViewPager = findViewById(R.id.flashCardViewPager);
        topicUserImg = findViewById(R.id.topicUserImg);
        topicUserName = findViewById(R.id.topicUserName);
        learnByFlashCardBtn = findViewById(R.id.learnByFlashCardBtn);
        learnByQuizBtn = findViewById(R.id.learnByQuizBtn);
        learnByTypingBtn = findViewById(R.id.learnByTypingBtn);
        topicName = findViewById(R.id.topicName);
        topicDesc = findViewById(R.id.topicDesc);
        fullScreenProgressBar = findViewById(R.id.fullScreenProgressBar);
        scrollPagerIndicator = findViewById(R.id.scrollPagerIndicator);
        progressBar = findViewById(R.id.progressBar);
        if (data == null) {
            finish();
        } else {
            topic = data;
            isOwner = user.getId() == topic.getOwnerId() ? true : false;
        }

        apiService = ApiClient.getClient();
        sharedPreferences = getSharedPreferences(Constant.SHARE_PREF, MODE_PRIVATE);

        optionMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });

        topicName.setText(topic.getTopicName());
        topicDesc.setText(topic.getDescription());

        topicViewModel = new ViewModelProvider(this).get(TopicViewModel.class);
        topicViewModel.setVocabulariesList(new ArrayList<>());
        initViewModel();

        returnBtn.setOnClickListener(v -> {
            finish();
        });

        learnByQuizBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TopicActivity.this, StudySetupActivity.class);
                intent.putExtra("topic", topic);
                intent.putExtra("studyMode", Constant.StudyMode.Quiz);
                intent.putParcelableArrayListExtra("vocabularies", new ArrayList<>(vocabulariesList));
                startActivity(intent);
            }
        });

        learnByTypingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TopicActivity.this, StudySetupActivity.class);
                intent.putExtra("topic", topic);
                intent.putExtra("studyMode", Constant.StudyMode.Typing);
                intent.putParcelableArrayListExtra("vocabularies", new ArrayList<>(vocabulariesList));
                startActivity(intent);
            }
        });

        learnByFlashCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TopicActivity.this, FlashcardActivity.class);
                intent.putExtra("topic", topic);
                intent.putParcelableArrayListExtra("vocabularies", new ArrayList<>(vocabulariesList));
                startActivity(intent);
            }
        });

        topicViewModel.getVocabularies().observe(this, new Observer<List<Vocabulary>>() {
            @Override
            public void onChanged(List<Vocabulary> items) {
                vocabulariesList = items;
                vocabularyInFlashcardAdapter = new VocabularyInFlashcardAdapter(true, TopicActivity.this, items, R.layout.flashcard_layout_item, ttsEnglish, ttsVietnamese);
                flashCardViewPager.setAdapter(vocabularyInFlashcardAdapter);
                flashCardViewPager.setOffscreenPageLimit(3);
                flashCardViewPager.setClipToPadding(false);
                scrollPagerIndicator.attachToPager(flashCardViewPager);

                if (items.size() < 2) {
                    learnByTypingBtn.setVisibility(View.GONE);
                    learnByQuizBtn.setVisibility(View.GONE);
                } else {
                    learnByTypingBtn.setVisibility(View.VISIBLE);
                    learnByQuizBtn.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    private void showBottomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.topic_bottom_sheet);

        MaterialButton editTopicBtn = dialog.findViewById(R.id.editTopicBtn);
        MaterialButton deleteTopicBtn = dialog.findViewById(R.id.deleteTopicBtn);

        if(isOwner){
            editTopicBtn.setVisibility(View.VISIBLE);
            deleteTopicBtn.setVisibility(View.VISIBLE);
        }
        else{
            editTopicBtn.setVisibility(View.GONE);
            deleteTopicBtn.setVisibility(View.GONE);
        }
        editTopicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopicActivity.this, CreateTopicActivity.class);
                intent.putExtra("topic", topic);
                startActivity(intent);
            }
        });

        deleteTopicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                DeleteTopic(topic.getId());
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }
    private void initViewModel() {
        progressBar.setVisibility(View.VISIBLE);
        apiService = ApiClient.getClient();
        Call<VocabulariesFromTopicResponse> call = apiService.getVocabulariesFromTopic(topic.getId());
        call.enqueue(new Callback<VocabulariesFromTopicResponse>() {
            @Override
            public void onResponse(Call<VocabulariesFromTopicResponse> call, Response<VocabulariesFromTopicResponse> response) {
                progressBar.setVisibility(View.GONE);
                VocabulariesFromTopicResponse vocabulariesFromTopicResponse = response.body();
                List<Vocabulary> listVocab = vocabulariesFromTopicResponse.getData();
                topicViewModel.setVocabulariesList(listVocab);
            }

            @Override
            public void onFailure(Call<VocabulariesFromTopicResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Utils.showDialog(Gravity.CENTER, "ERROR WHEN LOAD TOPIC", TopicActivity.this);
            }
        });

        apiService = ApiClient.getClient();
        Call<UserFromTopicResponse> userCall = apiService.getUserFromTopic(topic.getId());
        userCall.enqueue(new Callback<UserFromTopicResponse>() {
            @Override
            public void onResponse(Call<UserFromTopicResponse> call, Response<UserFromTopicResponse> response) {
                progressBar.setVisibility(View.GONE);
                UserFromTopicResponse userFromTopicResponse = response.body();
                User owner = userFromTopicResponse.getData();
                if(owner != null){
                    if(owner.getProfileImage() != null) {
                        Picasso.get().load(Uri.parse(owner.getProfileImage())).into(topicUserImg);
                    }
                    topicUserName.setText(owner.getUsername());
                }
            }

            @Override
            public void onFailure(Call<UserFromTopicResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Utils.showDialog(Gravity.CENTER, "ERROR WHEN LOAD USER", TopicActivity.this);
            }
        });
    }
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = ttsEnglish.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA) {
                Utils.showDialog(Gravity.CENTER, "THIS LANGUAGE IS NOT SUPPORTED", TopicActivity.this);
            }

            int res = ttsVietnamese.setLanguage(new Locale("vi"));
            if (res == TextToSpeech.LANG_MISSING_DATA) {
                Utils.showDialog(Gravity.CENTER, "THIS LANGUAGE IS NOT SUPPORTED", TopicActivity.this);
            }
        } else {
            Utils.showDialog(Gravity.CENTER, "FAILED", TopicActivity.this);
        }
    }

    @Override
    public void onSaveToFolder() {
        Intent intent = new Intent(this, AddTopicFolderActivity.class);
        intent.putExtra("topic", topic);
        addTopicToFolderResultLauncher.launch(intent);
    }

    @Override
    public void onDeleteTopic() {
        fullScreenProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onEditTopic() {
        Intent intent = new Intent(TopicActivity.this, CreateTopicActivity.class);
        intent.putExtra("topic", topic);
        intent.putExtra("isEdit", true);
        editTopicVocabulariesResultLauncher.launch(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initViewModel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(ttsEnglish != null){
            ttsEnglish.stop();
            ttsEnglish.shutdown();
        }
        if(ttsVietnamese != null){
            ttsVietnamese.stop();
            ttsVietnamese.shutdown();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initViewModel();
    }

    private void DeleteTopic(int topicID){
        Call<DeleteTopic> call = apiService.deleteTopic(topicID);
        call.enqueue(new Callback<DeleteTopic>() {
            @Override
            public void onResponse(Call<DeleteTopic> call, Response<DeleteTopic> response) {
                if (response.isSuccessful()) {
                    DeleteTopic deleteTopic = response.body();
                    if (deleteTopic != null && "OK".equals(deleteTopic.getStatus())) {
                        Utils.showDialog(Gravity.CENTER, "Topic deleted", TopicActivity.this );
                        finish();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Utils.showDialog(Gravity.CENTER, "Delete 12312321", TopicActivity.this );
                    }
                } else {
                    Toast.makeText(TopicActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    Utils.showDialog(Gravity.CENTER, "Delete error", TopicActivity.this );
                }
            }

            @Override
            public void onFailure(Call<DeleteTopic> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Utils.showDialog(Gravity.CENTER, "Something went wrong! Please try again!", TopicActivity.this );
            }
        });
    }
}