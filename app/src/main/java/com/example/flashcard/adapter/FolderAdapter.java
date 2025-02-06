package com.example.flashcard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashcard.model.user.User;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;
import com.example.flashcard.R;
import com.example.flashcard.model.folder.Folder;
import com.example.flashcard.utils.CustomOnItemClickListener;

import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {
    private Context mContext;
    private List<Folder> folders;
    private int layout;
    private User user;
    private CustomOnItemClickListener customOnItemClickListener;

    public FolderAdapter(Context context, List<Folder> folders, int layout, User user, CustomOnItemClickListener customOnItemClickListener) {
        this.mContext = context;
        this.folders = folders;
        this.layout = layout;
        this.user = user;
        this.customOnItemClickListener = customOnItemClickListener;
    }

    public class FolderViewHolder extends RecyclerView.ViewHolder {
        TextView folderNameTxt;
        TextView folderDescription;
        TextView folderOwnerNameTxt;
        ShapeableImageView folderOwnerImg;

        public FolderViewHolder(View itemView) {
            super(itemView);
            folderNameTxt = itemView.findViewById(R.id.folderNameEnglishTv);
            folderDescription = itemView.findViewById(R.id.folderNameVietnameseTv);
            folderOwnerImg = itemView.findViewById(R.id.folderUserImg);
            folderOwnerNameTxt = itemView.findViewById(R.id.folderUserName);
        }
    }

    @Override
    public FolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(layout, parent, false);
        return new FolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FolderViewHolder holder, int position) {
        Folder folder = folders.get(position);
        holder.itemView.setOnClickListener(v -> customOnItemClickListener.onFolderClick(folder));
        holder.folderNameTxt.setText(folder.getFolderName());
        holder.folderDescription.setText(folder.getFolderDescription());
        holder.folderOwnerNameTxt.setText(user.getUsername());
        Picasso.get().load(user.getProfileImage()).into(holder.folderOwnerImg);
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    public void setFolders(List<Folder> folders) {
        this.folders = folders;
        notifyDataSetChanged();
    }
}
