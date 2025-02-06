package com.example.flashcard.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashcard.R;
import com.example.flashcard.model.topic.Topic;
import com.example.flashcard.model.user.User;
import com.example.flashcard.model.user.UserFromTopicResponse;
import com.example.flashcard.repository.ApiClient;
import com.example.flashcard.repository.ApiService;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseTopicAdapter extends RecyclerView.Adapter<ChooseTopicAdapter.TopicViewHolder> {

    private Context mContext;
    private List<Topic> topics;
    private int layout;

    public ChooseTopicAdapter(Context mContext, List<Topic> topics, int layout) {
        this.mContext = mContext;
        this.topics = topics;
        this.layout = layout;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    @Override
    public TopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(layout, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TopicViewHolder holder, int position) {
        Topic topic = topics.get(position);

        holder.itemView.setOnClickListener(view -> {
            topic.setChosen(!topic.isChosen());
            notifyItemChanged(position);
        });

        holder.itemView.setBackground(topic.isChosen()
                ? mContext.getDrawable(R.drawable.topic_library_selected)
                : mContext.getDrawable(R.drawable.topic_library_unselected));

        holder.topicNameTxt.setText(topic.getTopicName());
        holder.topicTermsCount.setText(topic.getVocabularyCount() + " Vocabularies");
        ApiService apiService = ApiClient.getClient();
        Call<UserFromTopicResponse> call = apiService.getUserFromTopic(topic.getId());
        call.enqueue(new Callback<UserFromTopicResponse>() {
            @Override
            public void onResponse(Call<UserFromTopicResponse> call, Response<UserFromTopicResponse> response) {
                UserFromTopicResponse userFromTopicResponse = response.body();
                User owner = userFromTopicResponse.getData();
                if(owner != null){
                    holder.topicOwnerNameTxt.setText(owner.getUsername());
                    if(owner.getProfileImage() != null) {
                        Picasso.get().load(Uri.parse(owner.getProfileImage())).into(holder.topicOwnerImg);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserFromTopicResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    public static class TopicViewHolder extends RecyclerView.ViewHolder {
        TextView topicNameTxt;
        TextView topicTermsCount;
        ShapeableImageView topicOwnerImg;
        TextView topicOwnerNameTxt;

        public TopicViewHolder(View itemView) {
            super(itemView);
            topicNameTxt = itemView.findViewById(R.id.topicItemNameTxt);
            topicTermsCount = itemView.findViewById(R.id.termsCount);
            topicOwnerImg = itemView.findViewById(R.id.topicUserImg);
            topicOwnerNameTxt = itemView.findViewById(R.id.topicUserName);
        }
    }
}