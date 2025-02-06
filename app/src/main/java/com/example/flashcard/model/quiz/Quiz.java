package com.example.flashcard.model.quiz;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.flashcard.model.vocabulary.Vocabulary;

import java.util.ArrayList;

public class Quiz implements Parcelable {
    private Vocabulary correctAnswer;
    private ArrayList<Vocabulary> wrongAnswer;

    public Quiz(Vocabulary correctAnswer, ArrayList<Vocabulary> wrongAnswer) {
        this.correctAnswer = correctAnswer;
        this.wrongAnswer = wrongAnswer;
    }

    protected Quiz(Parcel in) {
        correctAnswer = in.readParcelable(Vocabulary.class.getClassLoader());
        wrongAnswer = in.createTypedArrayList(Vocabulary.CREATOR);
    }

    public static final Creator<Quiz> CREATOR = new Creator<Quiz>() {
        @Override
        public Quiz createFromParcel(Parcel in) {
            return new Quiz(in);
        }

        @Override
        public Quiz[] newArray(int size) {
            return new Quiz[size];
        }
    };

    public Vocabulary getCorrectAnswer() {
        return correctAnswer;
    }

    public ArrayList<Vocabulary> getWrongAnswer() {
        return wrongAnswer;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(correctAnswer, flags);
        dest.writeTypedList(wrongAnswer);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "correctAnswer=" + correctAnswer +
                ", wrongAnswer=" + wrongAnswer +
                '}';
    }
}