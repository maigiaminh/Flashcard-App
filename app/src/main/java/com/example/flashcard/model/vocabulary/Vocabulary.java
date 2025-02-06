package com.example.flashcard.model.vocabulary;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Vocabulary implements Parcelable {
    private int id;
    private String vocabulary;
    private String meaning;
    private String topicId;
    private List<String> vocabularyStatisticId;
    private List<String> bookmarkVocabularyId;
    private boolean isFront;

    public Vocabulary(
            int id,
            String vocabulary,
            String meaning,
            List<String> vocabularyStatisticId,
            List<String> bookmarkVocabularyId) {
        this.id = id;
        this.vocabulary = vocabulary;
        this.meaning = meaning;
        this.topicId = null;
        this.vocabularyStatisticId = vocabularyStatisticId;
        this.bookmarkVocabularyId = bookmarkVocabularyId;
        this.isFront = false;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setVocabulary(String vocabulary) {
        this.vocabulary = vocabulary;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public void setVocabularyStatisticId(List<String> vocabularyStatisticId) {
        this.vocabularyStatisticId = vocabularyStatisticId;
    }

    public void setBookmarkVocabularyId(List<String> bookmarkVocabularyId) {
        this.bookmarkVocabularyId = bookmarkVocabularyId;
    }

    public void setFront(boolean front) {
        isFront = front;
    }

    protected Vocabulary(Parcel in) {
        id = in.readInt();
        vocabulary = in.readString();
        meaning = in.readString();
        topicId = in.readString();
        vocabularyStatisticId = in.createStringArrayList();
        bookmarkVocabularyId = in.createStringArrayList();
        isFront = in.readByte() != 0;
    }

    public static final Creator<Vocabulary> CREATOR = new Creator<Vocabulary>() {
        @Override
        public Vocabulary createFromParcel(Parcel in) {
            return new Vocabulary(in);
        }

        @Override
        public Vocabulary[] newArray(int size) {
            return new Vocabulary[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getVocabulary() {
        return vocabulary;
    }

    public String getMeaning() {
        return meaning;
    }

    public String getTopicId() {
        return topicId;
    }

    public List<String> getVocabularyStatisticId() {
        return vocabularyStatisticId;
    }

    public List<String> getBookmarkVocabularyId() {
        return bookmarkVocabularyId;
    }

    public boolean isFront() {
        return isFront;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(vocabulary);
        dest.writeString(meaning);
        dest.writeString(topicId);
        dest.writeStringList(vocabularyStatisticId);
        dest.writeStringList(bookmarkVocabularyId);
        dest.writeByte((byte) (isFront ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Vocabulary{" +
                "id=" + id +
                ", vocabulary='" + vocabulary + '\'' +
                ", meaning='" + meaning + '\'' +
                '}';
    }
}
