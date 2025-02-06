package com.example.flashcard.utils;

import com.example.flashcard.model.folder.Folder;
import com.example.flashcard.model.topic.Topic;

public interface CustomOnItemClickListener {
    public void onTopicClick(Topic topic);
    public void onFolderClick(Folder folder);
}
