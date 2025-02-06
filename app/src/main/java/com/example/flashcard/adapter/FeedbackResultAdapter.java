package com.example.flashcard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashcard.R;
import com.example.flashcard.model.results.ResultData;

import java.util.List;

public class FeedbackResultAdapter extends RecyclerView.Adapter<FeedbackResultAdapter.FeedbackResultViewHolder> {
    private Context mContext;
    private List<ResultData> resultList;
    private int layout;

    public FeedbackResultAdapter(Context context, List<ResultData> resultList, int layout) {
        this.mContext = context;
        this.resultList = resultList;
        this.layout = layout;
    }

    public static class FeedbackResultViewHolder extends RecyclerView.ViewHolder {
        public TextView resultQuestionTxt;
        public TextView resultCorrectTxt;
        public TextView resultIncorrectTxt;
        public TextView resultTxt;

        public FeedbackResultViewHolder(View itemView) {
            super(itemView);
            resultQuestionTxt = itemView.findViewById(R.id.resultQuestionTxt);
            resultCorrectTxt = itemView.findViewById(R.id.resultCorrectTxt);
            resultIncorrectTxt = itemView.findViewById(R.id.resultIncorrectTxt);
            resultTxt = itemView.findViewById(R.id.resultTxt);
        }
    }

    @Override
    public FeedbackResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(layout, parent, false);
        return new FeedbackResultViewHolder(view);
    }


    @Override
    public void onBindViewHolder(FeedbackResultViewHolder holder, int position) {
        ResultData result = resultList.get(position);
        holder.resultQuestionTxt.setText(result.getQuestion());
        holder.resultCorrectTxt.setText(result.getAnswer());
        holder.resultIncorrectTxt.setText(result.getChosenAnswer());
        holder.resultTxt.setText(result.isCorrect() ? "Correct" : "Incorrect");
        holder.resultTxt.setBackgroundColor(result.isCorrect() ? mContext.getColor(R.color.correct_answer) : mContext.getColor(R.color.incorrect_answer));

        if (result.isCorrect()) {
            holder.resultCorrectTxt.setVisibility(View.VISIBLE);
            holder.resultIncorrectTxt.setVisibility(View.GONE);
        } else {
            holder.resultCorrectTxt.setVisibility(View.VISIBLE);
            holder.resultIncorrectTxt.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }
}