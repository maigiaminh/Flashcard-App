package com.example.flashcard.model.folder;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class Folder implements Parcelable {
    @SerializedName("folderID")
    private int id;
    @SerializedName("userID")

    private String userId;
    @SerializedName("folderName")

    private String folderName;
    @SerializedName("folderDescription")

    private String folderDescription;
    private int topicCount;
    private ArrayList<String> topicInFolderId;
    private boolean isChosen;

    public Folder(int id, String userId, String folderNameEnglish, String folderNameVietnamese, int topicCount, ArrayList<String> topicInFolderId, boolean isChosen) {
        this.id = id;
        this.userId = userId;
        this.folderName = folderNameEnglish;
        this.folderDescription = folderNameVietnamese;
        this.topicCount = topicCount;
        this.topicInFolderId = topicInFolderId;
        this.isChosen = isChosen;
    }

    protected Folder(Parcel in) {
        id = in.readInt();
        userId = in.readString();
        folderName = in.readString();
        folderDescription = in.readString();
        topicCount = in.readInt();
        topicInFolderId = in.createStringArrayList();
        isChosen = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(userId);
        dest.writeString(folderName);
        dest.writeString(folderDescription);
        dest.writeInt(topicCount);
        dest.writeStringList(topicInFolderId);
        dest.writeByte((byte) (isChosen ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Folder> CREATOR = new Creator<Folder>() {
        @Override
        public Folder createFromParcel(Parcel in) {
            return new Folder(in);
        }

        @Override
        public Folder[] newArray(int size) {
            return new Folder[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getFolderName() {
        return folderName;
    }

    public String getFolderDescription() {
        return folderDescription;
    }

    public int getTopicCount() {
        return topicCount;
    }

    public ArrayList<String> getTopicInFolderId() {
        return topicInFolderId;
    }

    public boolean isChosen() {
        return isChosen;
    }
}
