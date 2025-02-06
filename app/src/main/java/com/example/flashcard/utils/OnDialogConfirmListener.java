package com.example.flashcard.utils;

public interface OnDialogConfirmListener {
    void onCreateFolderDialogConfirm(String folderName, String description);
    void onAddTopicToFolderDialogConfirm();
    void onDeleteFolderDialogConfirm();

}
