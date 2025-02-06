package com.example.flashcard.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.flashcard.model.folder.Folder;
import com.example.flashcard.model.topic.Topic;
import com.example.flashcard.model.user.User;

import java.util.List;

public class HomeDataViewModel extends ViewModel {
    private MutableLiveData<User> user = new MutableLiveData<>();
    private MutableLiveData<List<Topic>> topicsList = new MutableLiveData<>();
    private MutableLiveData<List<Folder>> folderList = new MutableLiveData<>();
    private MutableLiveData<List<Topic>> publicTopicsList = new MutableLiveData<>();

    public MutableLiveData<User> getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user.setValue(user);
    }

    public MutableLiveData<List<Topic>> getTopicsList() {
        return topicsList;
    }

    public void setTopicsList(List<Topic> topicsList) {
        this.topicsList.postValue(topicsList);
    }

    public MutableLiveData<List<Folder>> getFolderList() {
        return folderList;
    }

    public void setFolderList(List<Folder> folderList) {
        this.folderList.postValue(folderList);
    }

    public MutableLiveData<List<Topic>> getPublicTopicsList() {
        return publicTopicsList;
    }

    public void setPublicTopicsList(List<Topic> publicTopicsList) {
        this.publicTopicsList.postValue(publicTopicsList);
    }
}
