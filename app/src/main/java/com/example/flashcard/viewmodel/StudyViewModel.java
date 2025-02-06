package com.example.flashcard.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Timer;
import java.util.TimerTask;

public class StudyViewModel extends ViewModel {
    private MutableLiveData<Long> _seconds = new MutableLiveData<>();
    public MutableLiveData<Long> getSeconds() {
        return _seconds;
    }

    private Timer timer;

    public StudyViewModel() {
        _seconds.setValue(0L);
    }

    public void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                _seconds.postValue(_seconds.getValue() != null ? _seconds.getValue() + 1 : 0);
            }
        }, 0, 1000);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        stopTimer();
    }
}