package com.example.flashcard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.flashcard.model.topic.Topic;
import com.example.flashcard.model.vocabulary.Vocabulary;
import com.example.flashcard.repository.ApiClient;
import com.example.flashcard.repository.ApiService;
import com.example.flashcard.utils.Constant;
import com.example.flashcard.utils.FlashCardOptionsListener;
import com.example.flashcard.utils.OnSwipeScreenListener;
import com.example.flashcard.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineExceptionHandler;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Job;

public class FlashcardActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, FlashCardOptionsListener {
    private ImageButton closeBtn;
    private TextView quizProgressTxt;
    private ImageButton flashCardOptionBtn;
    private ImageButton autoBtn;
    private CardView cardFrontWordView;
    private ImageButton cardFrontTextToSpeechBtn;
    private TextView cardFrontTextView;
    private CardView cardBackWordView;
    private ImageButton cardBackTextToSpeechBtn;
    private TextView cardBackTextView;
    private ImageButton prevVocabularyBtn;
    private ImageButton nextVocabularyBtn;
    private TextToSpeech ttsEnglish;
    private TextToSpeech ttsVietnamese;
    private List<Vocabulary> vocabularies;
    private List<Vocabulary> originalVocabularies;
    private Topic topic;
    private int index = 0;
    private int totalVocabularies = 0;
    private Constant.Language language = Constant.Language.ENGLISH;
    private AnimatorSet frontAnim;
    private AnimatorSet backAnim;
    private boolean isFlipping = false;
    private boolean isFront = false;
    private ApiService apiService;
    private SharedPreferences sharedPreferences;
    private boolean isBookmarked = false;
    private boolean isShuffled = false;
    private boolean isAutoPlayAudio = false;
    private boolean isFrontFirst = false;
    private boolean isAutoPlayCard = false;
    private ImageButton bookmarkVocabularyBtn;
    private ImageButton bookmarkVocabBtn;
    private ConstraintLayout flashCardLayout;
    private LinearLayout flashCardActivityLayout;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable autoPlayRunnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);

        closeBtn = findViewById(R.id.closeBtn);
        quizProgressTxt = findViewById(R.id.quizProgressTxt);
        flashCardOptionBtn = findViewById(R.id.flashCardOptionBtn);
        autoBtn = findViewById(R.id.autoBtn);
        cardFrontWordView = findViewById(R.id.cardFrontWordView);
        cardFrontTextToSpeechBtn = findViewById(R.id.cardFrontTextToSpeechBtn);
        cardFrontTextView = findViewById(R.id.cardFrontTextView);
        cardBackWordView = findViewById(R.id.cardBackWordView);
        cardBackTextToSpeechBtn = findViewById(R.id.cardBackTextToSpeechBtn);
        cardBackTextView = findViewById(R.id.cardBackTextView);
        prevVocabularyBtn = findViewById(R.id.prevVocabularyBtn);
        nextVocabularyBtn = findViewById(R.id.nextVocabularyBtn);
        bookmarkVocabularyBtn = findViewById(R.id.bookmarkVocabularyBtn);
        bookmarkVocabBtn = findViewById(R.id.bookmarkVocabBtn);
        flashCardLayout = findViewById(R.id.flashCardLayout);
        flashCardActivityLayout = findViewById(R.id.flashCardActivityLayout);

        apiService = ApiClient.getClient();
        sharedPreferences = getSharedPreferences(Constant.SHARE_PREF, MODE_PRIVATE);
        ttsEnglish = new TextToSpeech(this, this);
        ttsVietnamese = new TextToSpeech(this, this);
        vocabularies = getIntent().getParcelableArrayListExtra("vocabularies");
        topic = getIntent().getParcelableExtra("topic");
        totalVocabularies = vocabularies.size();
        frontAnim = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.front_animator);
        backAnim = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.back_animator);
        closeBtn.setOnClickListener(view -> finish());

        flashCardOptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });

        flashCardActivityLayout.setOnTouchListener(new OnSwipeScreenListener(this) {
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                if (index < totalVocabularies) {
                    index++;
                    setVocabulary();
                }
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                if (index > 0) {
                    index--;
                    setVocabulary();
                }
            }
        });

        setVocabulary();
        autoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isAutoPlayCard) {
                    isAutoPlayCard = true;
                    isAutoPlayAudio = true;
                    autoBtn.setImageResource(R.drawable.baseline_pause_circle_24);
                    startAutoScroll();
                } else {
                    isAutoPlayCard = false;
                    isAutoPlayAudio = false;
                    autoBtn.setImageResource(R.drawable.baseline_play_circle_filled_24);
                    stopAutoScroll();
                }
            }
        });
    }

    private void showBottomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.topic_bottom_sheet);


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = ttsEnglish.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA) {
                Utils.showDialog(Gravity.CENTER, "THIS LANGUAGE IS NOT SUPPORTED", this);
            }
            int res = ttsVietnamese.setLanguage(new Locale("vi"));
            if (res == TextToSpeech.LANG_MISSING_DATA) {
                Utils.showDialog(Gravity.CENTER, "THIS LANGUAGE IS NOT SUPPORTED", this);
            }
        } else {
            Utils.showDialog(Gravity.CENTER, "FAILED", this);
        }
    }

    @Override
    public void onApply(boolean isShuffle, boolean isAutoPlaySound, boolean isFrontFirst, Constant.Language studyLanguage) {
        this.isShuffled = isShuffle;
        this.isAutoPlayAudio = isAutoPlaySound;
        this.isFrontFirst = isFrontFirst;
        this.language = studyLanguage;
        index = 0;
        if (isShuffle) {
            originalVocabularies = new ArrayList<>(vocabularies);
            Collections.shuffle(vocabularies);
        } else {
            if (originalVocabularies != null) {
                vocabularies = new ArrayList<>(originalVocabularies);
            }
        }
        setVocabulary();
    }
    private void startAutoScroll() {
        autoPlayRunnable = new Runnable() {
            @Override
            public void run() {
                if (index != 0) {
                    ttsEnglish.stop();
                    ttsVietnamese.stop();
                    if (isFront) {
                        frontAnim.setTarget(cardFrontWordView);
                        backAnim.setTarget(cardBackWordView);
                        frontAnim.start();
                        backAnim.start();
                        isFront = false;
                    } else {
                        backAnim.setTarget(cardFrontWordView);
                        frontAnim.setTarget(cardBackWordView);
                        backAnim.start();
                        frontAnim.start();
                        isFront = true;
                    }
                } else {
                    setVocabulary();
                }
                handler.postDelayed(this, 3000);
                index++;
                setVocabulary();
            }
        };
        handler.post(autoPlayRunnable);
    }

    private void stopAutoScroll(){
        handler.removeCallbacks(autoPlayRunnable);
    }
    private void setVocabulary() {
        if (index == totalVocabularies) {
            stopAutoScroll();
            finish();
            return;
        }

        if (index == 0 && isAutoPlayCard) {
            if (isFront) {
                frontAnim.setTarget(cardFrontWordView);
                backAnim.setTarget(cardBackWordView);
                frontAnim.start();
                backAnim.start();
                ttsEnglish.speak(vocabularies.get(index).getVocabulary(), TextToSpeech.QUEUE_FLUSH, null, "");
                isFront = false;
            } else {
                backAnim.setTarget(cardFrontWordView);
                frontAnim.setTarget(cardBackWordView);
                backAnim.start();
                frontAnim.start();
                ttsVietnamese.speak(vocabularies.get(index).getMeaning(), TextToSpeech.QUEUE_FLUSH, null, "");
                isFront = true;
            }
        }

        frontAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                isFlipping = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                isFlipping = false;
                if (isAutoPlayAudio) {
                    if (isFrontFirst) {
                        if (isFront) {
                            ttsEnglish.speak(vocabularies.get(index).getVocabulary(), TextToSpeech.QUEUE_FLUSH, null, "");
                        } else {
                            ttsVietnamese.speak(vocabularies.get(index).getMeaning(), TextToSpeech.QUEUE_FLUSH, null, "");
                        }
                    }
                }
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

        if (!isAutoPlayCard) {
            if (isFront) {
                frontAnim.setTarget(cardFrontWordView);
                backAnim.setTarget(cardBackWordView);
                frontAnim.start();
                backAnim.start();
                isFront = false;
            } else {
                if (isAutoPlayAudio) {
                    if (language == Constant.Language.ENGLISH) {
                        if (isFrontFirst) {
                            ttsEnglish.speak(vocabularies.get(index).getMeaning(), TextToSpeech.QUEUE_FLUSH, null, "");
                        } else {
                            ttsEnglish.speak(vocabularies.get(index).getVocabulary(), TextToSpeech.QUEUE_FLUSH, null, "");
                        }
                    } else {
                        if (isFrontFirst) {
                            ttsVietnamese.speak(vocabularies.get(index).getVocabulary(), TextToSpeech.QUEUE_FLUSH, null, "");
                        } else {
                            ttsVietnamese.speak(vocabularies.get(index).getMeaning(), TextToSpeech.QUEUE_FLUSH, null, "");
                        }
                    }
                }
            }
        }

        if (isBookmarked) {
            bookmarkVocabularyBtn.setImageResource(R.drawable.outline_star_outline_24_selected);
            bookmarkVocabBtn.setImageResource(R.drawable.outline_star_outline_24_selected);
        } else {
            bookmarkVocabularyBtn.setImageResource(R.drawable.outline_star_outline_24);
            bookmarkVocabBtn.setImageResource(R.drawable.outline_star_outline_24);
        }

        quizProgressTxt.setText((index + 1) + "/" + totalVocabularies);
        prevVocabularyBtn.setEnabled(index != 0);
        prevVocabularyBtn.setColorFilter(getResources().getColor(index == 0 ? R.color.tertiary_text_color : R.color.white));
        prevVocabularyBtn.setOnClickListener(view -> {
            runOnUiThread(() -> {
                ttsEnglish.stop();
                ttsVietnamese.stop();
                index--;
                setVocabulary();
            });
        });

        bookmarkVocabularyBtn.setOnClickListener(view -> {
            if (!isBookmarked) {
                bookmarkVocabularyBtn.setImageResource(R.drawable.outline_star_outline_24_selected);
                bookmarkVocabBtn.setImageResource(R.drawable.outline_star_outline_24_selected);
                isBookmarked = true;
            } else {
                bookmarkVocabularyBtn.setImageResource(R.drawable.outline_star_outline_24);
                bookmarkVocabBtn.setImageResource(R.drawable.outline_star_outline_24);
                isBookmarked = false;
            }
        });

        bookmarkVocabBtn.setOnClickListener(view -> {
            if (!isBookmarked) {
                bookmarkVocabBtn.setImageResource(R.drawable.outline_star_outline_24_selected);
                bookmarkVocabularyBtn.setImageResource(R.drawable.outline_star_outline_24_selected);
                isBookmarked = true;
            } else {
                bookmarkVocabularyBtn.setImageResource(R.drawable.outline_star_outline_24);
                bookmarkVocabBtn.setImageResource(R.drawable.outline_star_outline_24);
                isBookmarked = false;
            }
        });

        nextVocabularyBtn.setOnClickListener(view -> {
            runOnUiThread(() -> {
                ttsEnglish.stop();
                ttsVietnamese.stop();
                index++;
                setVocabulary();
            });
        });

        Vocabulary vocabulary = vocabularies.get(index);
        if (isFrontFirst) {
            cardBackTextView.setText(language == Constant.Language.ENGLISH ? vocabulary.getMeaning() : vocabulary.getVocabulary());
            cardFrontTextView.setText(language == Constant.Language.ENGLISH ? vocabulary.getVocabulary() : vocabulary.getMeaning());
        } else {
            cardBackTextView.setText(language == Constant.Language.ENGLISH ? vocabulary.getVocabulary() : vocabulary.getMeaning());
            cardFrontTextView.setText(language == Constant.Language.ENGLISH ? vocabulary.getMeaning() : vocabulary.getVocabulary());
        }
        cardBackTextToSpeechBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ttsEnglish.speak(vocabulary.getVocabulary(), TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        cardFrontTextToSpeechBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ttsVietnamese.speak(vocabulary.getMeaning(), TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        float scale = this.getResources().getDisplayMetrics().density;
        cardFrontWordView.setCameraDistance(8000 * scale);
        cardBackWordView.setCameraDistance(8000 * scale);

        flashCardLayout.setOnClickListener(view -> {
            if (!isFlipping) {
                ttsEnglish.stop();
                ttsVietnamese.stop();
                if (isFront) {
                    frontAnim.setTarget(cardFrontWordView);
                    backAnim.setTarget(cardBackWordView);
                    frontAnim.start();
                    backAnim.start();
                    isFront = false;
                } else {
                    frontAnim.setTarget(cardBackWordView);
                    backAnim.setTarget(cardFrontWordView);
                    frontAnim.start();
                    backAnim.start();
                    isFront = true;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ttsEnglish.stop();
        ttsEnglish.shutdown();
        ttsVietnamese.stop();
        ttsVietnamese.shutdown();
    }
}