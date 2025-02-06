package com.example.flashcard.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;

import androidx.lifecycle.ViewModel;

import com.example.flashcard.model.folder.Folder;
import com.example.flashcard.model.topic.Topic;
import com.example.flashcard.repository.ApiService;
import com.example.flashcard.utils.Utils;

import java.util.List;

public class FolderViewModel extends ViewModel {
    public void updateTopicForFolder(ApiService apiService, View view, Context mContext,
                                     List<Topic> addedTopic, List<Topic> removedTopic, Folder folder, SharedPreferences sharedPreferences) {
        new UpdateTopicAsyncTask(apiService, view, mContext, addedTopic, removedTopic, folder, sharedPreferences).execute();
    }

    private static class UpdateTopicAsyncTask extends AsyncTask<Void, Void, Void> {
        private ApiService dataRepository;
        private View view;
        private Context mContext;
        private List<Topic> addedTopic;
        private List<Topic> removedTopic;
        private Folder folder;
        private SharedPreferences sharedPreferences;

        public UpdateTopicAsyncTask(ApiService apiService, View view, Context mContext,
                                    List<Topic> addedTopic, List<Topic> removedTopic, Folder folder, SharedPreferences sharedPreferences) {
            this.dataRepository = dataRepository;
            this.view = view;
            this.mContext = mContext;
            this.addedTopic = addedTopic;
            this.removedTopic = removedTopic;
            this.folder = folder;
            this.sharedPreferences = sharedPreferences;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (Topic topic : addedTopic) {
                try {
                } catch (Exception e) {
                    Utils.showSnackBar(view, e.getMessage());
                }
            }

            for (Topic topic : removedTopic) {
                try {
                } catch (Exception e) {
                    Utils.showSnackBar(view, e.getMessage());
                }
            }

            return null;
        }
    }
}