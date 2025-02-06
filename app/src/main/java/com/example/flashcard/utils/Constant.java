package com.example.flashcard.utils;

import com.example.flashcard.adapter.FolderAdapter;

public class Constant {
    public static final String USER_DATA = "USERDATA";
    public static final String SHARE_PREF = "SHAREDPREFKEY";
    public static final int PICK_IMAGE_REQUEST = 111;
    public static final int PICK_IMAGE_INTENT = 222;
    public static final int ADD_TOPIC_TO_FOLDER = 333;

    public static final float MAX_SCALE = 1f;
    public static final float SCALE_PERCENT = 0.8f;
    public static final float MIN_SCALE = SCALE_PERCENT * MAX_SCALE;
    public static final float MAX_ALPHA = 1.0f;
    public static final float MIN_ALPHA = 0.05f;
    public static String UNSPLASH_API_KEY = "H5Ku9-8ug8iQsduF4f9Fg_xPhKY70YKQBKKnWTKaMA0";
    public static enum StudyMode{
        Quiz,
        Flashcard,
        Typing
    }

    public static enum Language{
        ENGLISH,
        VIETNAMESE
    }
}
