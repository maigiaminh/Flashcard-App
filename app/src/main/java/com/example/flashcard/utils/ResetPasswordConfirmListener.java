package com.example.flashcard.utils;

public interface ResetPasswordConfirmListener {
    void onConfirm(String email);

    void changePassword(String oldPassword, String newPassword);
}
