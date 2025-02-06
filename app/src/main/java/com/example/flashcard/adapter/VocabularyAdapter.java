package com.example.flashcard.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashcard.R;
import com.example.flashcard.model.vocabulary.Vocabulary;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.List;

public class VocabularyAdapter extends RecyclerView.Adapter<VocabularyAdapter.ViewHolder> {
    public List<Vocabulary> vocabs;

    public VocabularyAdapter(List<Vocabulary> vocabularyList){
        vocabs = vocabularyList;
    }

    private DownloadConditions downloadConditions = new DownloadConditions.Builder().requireWifi().build();
    private TranslatorOptions engToVietOptions = new TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.VIETNAMESE)
            .build();
    private Translator engToVietTranslator = Translation.getClient(engToVietOptions);

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Vocabulary vocab = vocabs.get(position);
        holder.editTextVocabulary.setText(vocab.getVocabulary());
        holder.editTextMeaning.setText(vocab.getMeaning());

        holder.removeVocabularyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vocabs.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
            }
        });

        holder.editTextVocabulary.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                engToVietTranslator.downloadModelIfNeeded(downloadConditions).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        engToVietTranslator.translate(s.toString()).addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String translatedText) {
                                holder.editTextMeaning.setText(translatedText);
                                vocab.setMeaning(translatedText);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                holder.editTextMeaning.setText("");
                            }
                        });
                    }
                });
                vocab.setVocabulary(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        holder.editTextMeaning.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                vocab.setMeaning(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return vocabs.size();
    }

    public void addFlashcard() {
        vocabs.add(new Vocabulary(0, "","", new ArrayList<>(), new ArrayList<>()));
        notifyItemInserted(vocabs.size() - 1);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private EditText editTextVocabulary;
        private EditText editTextMeaning;
        private ImageButton removeVocabularyBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            editTextVocabulary = itemView.findViewById(R.id.editTextVocabulary);
            editTextMeaning = itemView.findViewById(R.id.editTextMeaning);
            removeVocabularyBtn = itemView.findViewById(R.id.removeVocabularyBtn);
        }
    }
}