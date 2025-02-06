package com.example.flashcard.adapter;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashcard.R;
import com.example.flashcard.model.vocabulary.Vocabulary;

import java.util.List;

public class VocabularyInFlashcardAdapter extends RecyclerView.Adapter<VocabularyInFlashcardAdapter.ViewHolder> {

    private boolean studyEnglishMode;
    private Context mContext;
    public List<Vocabulary> vocabularies;
    private int layout;
    private TextToSpeech ttsEnglish;
    private TextToSpeech ttsVietnamese;
    private AnimatorSet frontAnim;
    private AnimatorSet backAnim;
    private float scale;
    private boolean isFlipping;

    public VocabularyInFlashcardAdapter(boolean studyEnglishMode, Context mContext, List<Vocabulary> vocabularies, int layout, TextToSpeech ttsEnglish, TextToSpeech ttsVietnamese) {
        this.studyEnglishMode = studyEnglishMode;
        this.mContext = mContext;
        this.vocabularies = vocabularies;
        this.layout = layout;
        this.ttsEnglish = ttsEnglish;
        this.ttsVietnamese = ttsVietnamese;
        this.frontAnim = (AnimatorSet) AnimatorInflater.loadAnimator(mContext, R.animator.front_animator);
        this.backAnim = (AnimatorSet) AnimatorInflater.loadAnimator(mContext, R.animator.back_animator);
        this.scale = mContext.getResources().getDisplayMetrics().density;
        this.isFlipping = false;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardViewFront;
        CardView cardViewBack;
        TextView backView;
        TextView frontView;
        ImageButton ttsFrontBtn;
        ImageButton ttsBackBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            cardViewFront = itemView.findViewById(R.id.cardFrontWordView);
            cardViewBack = itemView.findViewById(R.id.cardBackWordView);
            backView = itemView.findViewById(R.id.cardBackTextView);
            frontView = itemView.findViewById(R.id.cardFrontTextView);
            ttsFrontBtn = itemView.findViewById(R.id.cardFrontTextToSpeechBtn);
            ttsBackBtn = itemView.findViewById(R.id.cardBackTextToSpeechBtn);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return vocabularies.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Vocabulary vocabulary = vocabularies.get(position);
        holder.backView.setText(vocabulary.getVocabulary());
        holder.frontView.setText(vocabulary.getMeaning());

        if (ttsVietnamese == null || ttsEnglish == null) {
            holder.ttsBackBtn.setVisibility(View.GONE);
            holder.ttsFrontBtn.setVisibility(View.GONE);
        } else {
            holder.ttsBackBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!studyEnglishMode) {
                        ttsVietnamese.speak(vocabulary.getMeaning(), TextToSpeech.QUEUE_FLUSH, null, null);
                    } else {
                        ttsEnglish.speak(vocabulary.getVocabulary(), TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                }
            });

            holder.ttsFrontBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!studyEnglishMode) {
                        ttsEnglish.speak(vocabulary.getVocabulary(), TextToSpeech.QUEUE_FLUSH, null, null);
                    } else {
                        ttsVietnamese.speak(vocabulary.getMeaning(), TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                }
            });
        }

        holder.cardViewFront.setCameraDistance(8000 * scale);
        holder.cardViewBack.setCameraDistance(8000 * scale);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFlipping) {
                    if (vocabulary.isFront()) {
                        frontAnim.setTarget(holder.cardViewFront);
                        backAnim.setTarget(holder.cardViewBack);
                        frontAnim.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {
                                isFlipping = true;
                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                isFlipping = false;
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {
                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {
                            }
                        });

                        backAnim.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {
                                isFlipping = true;
                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                isFlipping = false;
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {
                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {
                            }
                        });

                        frontAnim.start();
                        backAnim.start();
                        vocabulary.setFront(false);
                    } else {
                        frontAnim.setTarget(holder.cardViewBack);
                        backAnim.setTarget(holder.cardViewFront);
                        frontAnim.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {
                                isFlipping = true;
                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                isFlipping = false;
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {
                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {
                            }
                        });

                        backAnim.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {
                                isFlipping = true;
                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                isFlipping = false;
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {
                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {
                            }
                        });

                        frontAnim.start();
                        backAnim.start();
                        vocabulary.setFront(true);
                    }
                }
            }
        });
    }
}
