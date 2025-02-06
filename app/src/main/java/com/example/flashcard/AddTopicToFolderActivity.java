package com.example.flashcard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.flashcard.adapter.ChooseTopicAdapter;
import com.example.flashcard.model.folder.AddTopicToFolderResponse;
import com.example.flashcard.model.folder.Folder;
import com.example.flashcard.model.topic.AddTopicToFolder;
import com.example.flashcard.model.topic.Topic;
import com.example.flashcard.model.topic.Topics;
import com.example.flashcard.model.topic.TopicsFormUserResponse;
import com.example.flashcard.model.user.User;
import com.example.flashcard.repository.ApiClient;
import com.example.flashcard.repository.ApiService;
import com.example.flashcard.utils.Constant;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTopicToFolderActivity extends AppCompatActivity {

    private ImageButton returnBtn;
    private ImageButton acceptTopicBtn;
    private RecyclerView chosenTopicRecyclerView;
    private ApiService apiService;
    private SharedPreferences sharedPreferences;
    private User currentUser;
    private Folder folder;
    private ChooseTopicAdapter adapter;
    private List<Topic> currentTopics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_topic_to_folder);

        folder = getIntent().getParcelableExtra("folder");

        returnBtn = findViewById(R.id.returnBtn);
        returnBtn.setOnClickListener(v -> {
            finish();
        });
        initData();
        acceptTopicBtn = findViewById(R.id.acceptTopicBtn);
        acceptTopicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent = new Intent(AddTopicToFolderActivity.this, HomeActivity.class);
                ArrayList<Topic> arrayList = new ArrayList<>(adapter.getTopics());
                ArrayList<Topic> chosenTopics = new ArrayList<>();
                for (Topic topic : arrayList) {
                    if (topic.isChosen()) {
                        chosenTopics.add(topic);
                        addTopictoFolder(folder.getId(), topic.getId());
                    }
                }

                startActivity(newIntent);
            }
        });
        chosenTopicRecyclerView = findViewById(R.id.chosenTopicRecyclerView);
    }

    private void initData() {
        apiService = ApiClient.getClient();
        sharedPreferences = getSharedPreferences(Constant.SHARE_PREF, MODE_PRIVATE);

        if (sharedPreferences.getString(Constant.USER_DATA, null) == null) {
            finish();
        } else {
            currentUser = new Gson().fromJson(sharedPreferences.getString(Constant.USER_DATA, null), User.class);
        }

        currentTopics = getIntent().getParcelableArrayListExtra("currentTopics");

        Call<AddTopicToFolder> call = apiService.selectTopicToFolder(folder.getId(), currentUser.getId());
        call.enqueue(new Callback<AddTopicToFolder>() {
            @Override
            public void onResponse(Call<AddTopicToFolder> call, Response<AddTopicToFolder> response) {
                AddTopicToFolder addTopicToFolder = response.body();
                if (addTopicToFolder != null && "OK".equals(addTopicToFolder.getStatus())) {
//                    for(Topics t: topicsFormUserResponse.getData()){
//                        currentTopics.addAll(t.getAdditionalInfo());
//                    }
                    currentTopics = addTopicToFolder.getData();
                }
                adapter = new ChooseTopicAdapter(AddTopicToFolderActivity.this, currentTopics, R.layout.topic_library_item);
                chosenTopicRecyclerView.setHasFixedSize(true);
                chosenTopicRecyclerView.setLayoutManager(new LinearLayoutManager(AddTopicToFolderActivity.this, LinearLayoutManager.VERTICAL, false));
                chosenTopicRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<AddTopicToFolder> call, Throwable t) {

            }
        });
    }

    private void addTopictoFolder(int folderID, int topicID){
        Log.d("add topic to folder", folderID + "  " + topicID);
        Call<AddTopicToFolderResponse> call = apiService.insertFolderDetail(folderID, topicID);
        call.enqueue(new Callback<AddTopicToFolderResponse>() {
            @Override
            public void onResponse(Call<AddTopicToFolderResponse> call, Response<AddTopicToFolderResponse> response) {
                Log.d("Test add topic", response.body().getMessage());
            }

            @Override
            public void onFailure(Call<AddTopicToFolderResponse> call, Throwable t) {
                Log.d("Test add topic", t.getMessage());
            }
        });
    }
}