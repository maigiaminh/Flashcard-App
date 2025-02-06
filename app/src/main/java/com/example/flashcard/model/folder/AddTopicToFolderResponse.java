package com.example.flashcard.model.folder;

import com.example.flashcard.model.topic.Topic;
import com.google.gson.annotations.SerializedName;

public class AddTopicToFolderResponse {
        @SerializedName("status")
        private String status;

        @SerializedName("message")
        private String message;

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
}
