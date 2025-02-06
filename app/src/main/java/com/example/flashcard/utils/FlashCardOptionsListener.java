package com.example.flashcard.utils;

public interface FlashCardOptionsListener {
    void onApply(boolean isShuffle, boolean isAutoPlaySound, boolean isFrontFirst, Constant.Language studyLanguage);
}
