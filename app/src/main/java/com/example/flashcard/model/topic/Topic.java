package com.example.flashcard.model.topic;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.flashcard.model.user.User;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Topic implements Parcelable {
    @SerializedName("id")
    private int id;
    @SerializedName("topicName")

    private String topicName;
    @SerializedName("description")

    private String description;
    @SerializedName("vocabularyCount")
    private int vocabularyCount;
    @SerializedName("isPublic")

    private int isPublic;
    private ArrayList<String> userId;
    private List<String> learningStatisticsId;
    private List<String> topicInFolderId;
    private List<String> vocabularyId;
    @SerializedName("ownerID")
    private int ownerId;
    private boolean chosen;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getVocabularyCount() {
        return vocabularyCount;
    }

    public void setVocabularyCount(int vocabularyCount) {
        this.vocabularyCount = vocabularyCount;
    }

    public int isPublic() {
        return isPublic;
    }

    public void setPublic(int aPublic) {
        isPublic = aPublic;
    }

    public ArrayList<String> getUserId() {
        return userId;
    }

    public void setUserId(ArrayList<String> userId) {
        this.userId = userId;
    }

    public List<String> getLearningStatisticsId() {
        return learningStatisticsId;
    }

    public void setLearningStatisticsId(List<String> learningStatisticsId) {
        this.learningStatisticsId = learningStatisticsId;
    }

    public List<String> getTopicInFolderId() {
        return topicInFolderId;
    }

    public void setTopicInFolderId(List<String> topicInFolderId) {
        this.topicInFolderId = topicInFolderId;
    }

    public List<String> getVocabularyId() {
        return vocabularyId;
    }

    public void setVocabularyId(List<String> vocabularyId) {
        this.vocabularyId = vocabularyId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public boolean isChosen() {
        return chosen;
    }

    public void setChosen(boolean chosen) {
        this.chosen = chosen;
    }

    public Topic(int id, String topicName, int vocabularyCount,
                 int isPublic, String description,
                 ArrayList<String> userId, List<String> learningStatisticsId,
                 List<String> topicInFolderId, List<String> vocabularyId,
                 int ownerId, boolean chosen) {
        this.id = id;
        this.topicName = topicName;
        this.description = description;
        this.vocabularyCount = vocabularyCount;
        this.isPublic = isPublic;
        this.userId = userId;
        this.learningStatisticsId = learningStatisticsId;
        this.topicInFolderId = topicInFolderId;
        this.vocabularyId = vocabularyId;
        this.ownerId = ownerId;
        this.chosen = chosen;
    }

    protected Topic(Parcel in) {
        id = in.readInt();
        topicName = in.readString();
        description = in.readString();
        vocabularyCount = in.readInt();
        isPublic = in.readByte();
        userId = in.createStringArrayList();
        learningStatisticsId = in.createStringArrayList();
        topicInFolderId = in.createStringArrayList();
        vocabularyId = in.createStringArrayList();
        ownerId = in.readInt();
        chosen = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(topicName);
        dest.writeString(description);
        dest.writeInt(vocabularyCount);
        dest.writeByte((byte) (isPublic));
        dest.writeStringList(userId);
        dest.writeStringList(learningStatisticsId);
        dest.writeStringList(topicInFolderId);
        dest.writeStringList(vocabularyId);
        dest.writeInt(ownerId);
        dest.writeByte((byte) (chosen ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Topic> CREATOR = new Creator<Topic>() {
        @Override
        public Topic createFromParcel(Parcel in) {
            return new Topic(in);
        }

        @Override
        public Topic[] newArray(int size) {
            return new Topic[size];
        }
    };
}
