package com.example.flashcard.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

import com.example.flashcard.R;
import com.example.flashcard.model.quiz.Quiz;
import com.example.flashcard.model.user.User;
import com.example.flashcard.model.vocabulary.Vocabulary;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utils {
    public static DownloadConditions downloadConditions = new DownloadConditions.Builder().requireWifi().build();
    public static TranslatorOptions engToVietOptions = new TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.VIETNAMESE)
            .build();
    public static final Translator engToVietTranslator = Translation.getClient(engToVietOptions);

    public static void showSnackBar(View view, String message) {
        Snackbar snackBar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
        snackBar.setAction("Close", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBar.dismiss();
            }
        });

        snackBar.show();
    }

    public static void showDialog(int gravity, String message, Context mContext) {
        Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.message_dialog);
        Log.d("Dialog", "Show " + message);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttribute = window.getAttributes();
        windowAttribute.gravity = gravity;
        window.setAttributes(windowAttribute);
        dialog.setCancelable(true);

        Button mainButton = dialog.findViewById(R.id.alertMainButton);
        TextView alertMessage = dialog.findViewById(R.id.alertBody);

        alertMessage.setText(message);

        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void showCreateFolderDialog(int gravity, Context mContext, OnDialogConfirmListener onDialogConfirmListener) {
        Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.create_folder_dialog);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttribute = window.getAttributes();
        windowAttribute.gravity = gravity;
        window.setAttributes(windowAttribute);
        dialog.setCancelable(true);

        Button createBtn = dialog.findViewById(R.id.createFolderBtn);
        Button cancelBtn = dialog.findViewById(R.id.createFolderCancelBtn);
        EditText folderNameEdt = dialog.findViewById(R.id.folderNameEdt);
        EditText folderDescEdt = dialog.findViewById(R.id.folderDesc);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String folderName = folderNameEdt.getText().toString();
                String folderDesc = folderDescEdt.getText().toString();
                if (folderName.isEmpty() || folderDesc.isEmpty()) {
                    Toast.makeText(mContext, "Please enter Folder Name !!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                onDialogConfirmListener.onCreateFolderDialogConfirm(folderName, folderDesc);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void showEditFolderDialog(String folderName, String folderDesc, int gravity, Context mContext, OnDialogConfirmListener onDialogConfirmListener) {
        Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.create_folder_dialog);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttribute = window.getAttributes();
        windowAttribute.gravity = gravity;
        window.setAttributes(windowAttribute);
        dialog.setCancelable(true);

        Button createBtn = dialog.findViewById(R.id.createFolderBtn);
        Button cancelBtn = dialog.findViewById(R.id.createFolderCancelBtn);
        EditText folderNameEdt = dialog.findViewById(R.id.folderNameEdt);
        EditText folderDescEdt = dialog.findViewById(R.id.folderDesc);

        folderNameEdt.setText(folderName);
        folderDescEdt.setText(folderDesc);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String folderName = folderNameEdt.getText().toString();
                String folderDesc = folderDescEdt.getText().toString();
                if (folderName.isEmpty() || folderDesc.isEmpty()) {
                    Toast.makeText(mContext, "Enter enter Folder Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                onDialogConfirmListener.onCreateFolderDialogConfirm(folderName, folderDesc);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void showResetPasswordDialog(int gravity, Context mContext, ResetPasswordConfirmListener resetPasswordConfirmListener) {
        Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.forget_password_dialog);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttribute = window.getAttributes();
        windowAttribute.gravity = gravity;
        window.setAttributes(windowAttribute);
        dialog.setCancelable(true);

        AppCompatButton acceptBtn = dialog.findViewById(R.id.acceptBtn);
        AppCompatButton cancelBtn = dialog.findViewById(R.id.cancelBtn);
        EditText emailEdt = dialog.findViewById(R.id.emailField);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEdt.getText().toString();
                if (email.isEmpty()) {
                    Toast.makeText(mContext, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }
                resetPasswordConfirmListener.onConfirm(email);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void showChangePasswordDialog(int gravity, Context mContext, ResetPasswordConfirmListener resetPasswordConfirmListener) {
        Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.change_password_dialog);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttribute = window.getAttributes();
        windowAttribute.gravity = gravity;
        window.setAttributes(windowAttribute);
        dialog.setCancelable(true);

        AppCompatButton acceptBtn = dialog.findViewById(R.id.acceptBtn);
        AppCompatButton cancelBtn = dialog.findViewById(R.id.cancelBtn);
        EditText passwordEdt = dialog.findViewById(R.id.oldPasswordEdt);
        EditText newPasswordEdt = dialog.findViewById(R.id.newPasswordEdt);
        EditText confirmedPasswordEdt = dialog.findViewById(R.id.confirmedPasswordEdt);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = passwordEdt.getText().toString();
                String newPassword = newPasswordEdt.getText().toString();
                String confirmedPassword = confirmedPasswordEdt.getText().toString();
                if (password.isEmpty() || newPassword.isEmpty() || confirmedPassword.isEmpty()) {
                    Toast.makeText(mContext, "Please fill all information", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!newPassword.equals(confirmedPassword)) {
                    Toast.makeText(mContext, "Password are not matched", Toast.LENGTH_SHORT).show();
                    return;
                }
                resetPasswordConfirmListener.changePassword(password, newPassword);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void showDeleteFolderConfirmDialog(int gravity, String message, Context mContext, OnDialogConfirmListener onDialogConfirmListener) {
        Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.option_dialog);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttribute = window.getAttributes();
        windowAttribute.gravity = gravity;
        window.setAttributes(windowAttribute);
        dialog.setCancelable(true);

        TextView alertBody = dialog.findViewById(R.id.alertBody);
        Button cancelBtn = dialog.findViewById(R.id.dialogCancelBtn);
        TextView acceptBtn = dialog.findViewById(R.id.dialogOkBtn);

        alertBody.setText(message);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDialogConfirmListener.onDeleteFolderDialogConfirm();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static List<Quiz> generateQuizzes(List<Vocabulary> vocabularies, boolean isShuffled) {
        List<Quiz> quizzes = new ArrayList<>();
        List<Vocabulary> shuffledVocabularies = isShuffled ? new ArrayList<>(vocabularies) : vocabularies;
        if (isShuffled) {
            Collections.shuffle(shuffledVocabularies);
        }

        for (Vocabulary vocabulary : shuffledVocabularies) {
            List<Vocabulary> incorrectVocabularies = new ArrayList<>(vocabularies);
            incorrectVocabularies.remove(vocabulary);
            Collections.shuffle(incorrectVocabularies);

            List<Vocabulary> options = new ArrayList<>(incorrectVocabularies.subList(0, Math.min(3, incorrectVocabularies.size())));
            Quiz quiz = new Quiz(vocabulary, new ArrayList<>(options));
            quizzes.add(quiz);
        }

        return quizzes;
    }

    public static void propCorrectAnswer(Context mContext) {
        Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.success_answer_dialog);
        Window window = dialog.getWindow();
        if (window == null) return;

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);
        dialog.setCancelable(false);

        ImageButton closeBtn = dialog.findViewById(R.id.closeBtn);

        closeBtn.setOnClickListener(view -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    public static void propWrongAnswer(Context mContext, String correctAnswer, String wrongAnswer) {
        Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.wrong_answer_dialog);
        Window window = dialog.getWindow();
        if (window == null) return;

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);
        dialog.setCancelable(false);

        TextView correctAnswerTxt = dialog.findViewById(R.id.correctAnswerTxt);
        TextView wrongAnswerTxt = dialog.findViewById(R.id.chosenWrongAnswerTxt);
        ImageButton closeBtn = dialog.findViewById(R.id.closeBtn);

        correctAnswerTxt.setText(correctAnswer);
        wrongAnswerTxt.setText(wrongAnswer);
        closeBtn.setOnClickListener(view -> {
            dialog.dismiss();
        });

        dialog.show();
    }
    
    public static User getUserFromSharedPreferences(Context mContext, SharedPreferences sharedPreferences) {
        String userDataJson = sharedPreferences.getString("USERDATA", null);
        return new Gson().fromJson(userDataJson, User.class);
    }
}
