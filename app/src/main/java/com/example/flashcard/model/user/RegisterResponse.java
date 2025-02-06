package com.example.flashcard.model.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse implements Parcelable {
    @SerializedName("data")

    private User user;
    @SerializedName("status")

    private String status;
    @SerializedName("message")

    private String message;

    public RegisterResponse(User user, String status, String message) {
        this.user = user;
        this.status = status;
        this.message = message;
    }

    protected RegisterResponse(Parcel in) {
        user = in.readParcelable(User.class.getClassLoader());
        status = in.readString();
        message = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(user, flags);
        dest.writeString(status);
        dest.writeString(message);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RegisterResponse> CREATOR = new Creator<RegisterResponse>() {
        @Override
        public RegisterResponse createFromParcel(Parcel in) {
            return new RegisterResponse(in);
        }

        @Override
        public RegisterResponse[] newArray(int size) {
            return new RegisterResponse[size];
        }
    };

    public User getUser() {
        return user;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
