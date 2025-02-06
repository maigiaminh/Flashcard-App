package com.example.flashcard.model.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class LoginResponse implements Parcelable {
    @SerializedName("data")

    private User user;
    @SerializedName("status")

    private String status;
    @SerializedName("message")

    private String message;

    public LoginResponse(User user, String status, String message) {
        this.user = user;
        this.status = status;
        this.message = message;
    }

    protected LoginResponse(Parcel in) {
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

    public static final Creator<LoginResponse> CREATOR = new Creator<LoginResponse>() {
        @Override
        public LoginResponse createFromParcel(Parcel in) {
            return new LoginResponse(in);
        }

        @Override
        public LoginResponse[] newArray(int size) {
            return new LoginResponse[size];
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
