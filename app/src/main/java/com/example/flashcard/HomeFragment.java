package com.example.flashcard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.flashcard.adapter.FolderAdapter;
import com.example.flashcard.adapter.TopicAdapter;
import com.example.flashcard.model.folder.Folder;
import com.example.flashcard.model.topic.Topic;
import com.example.flashcard.utils.Constant;
import com.example.flashcard.utils.CustomOnItemClickListener;
import com.example.flashcard.utils.OnBottomNavigationChangeListener;
import com.example.flashcard.utils.OnDrawerNavigationPressedListener;
import com.example.flashcard.viewmodel.HomeDataViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements CustomOnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private OnBottomNavigationChangeListener onBottomNavigationChangeListener;
    private OnDrawerNavigationPressedListener onDrawerNavigationPressedListener;
    private HomeDataViewModel userViewModel;
    private TopicAdapter topicAdapter;
    private FolderAdapter folderAdapter;
    private Button seeAllTopicBtn;
    private Button seeAllFoldersBtn;
    private ViewPager2 topicRecyclerView;
    private ViewPager2 folderRecyclerView;
    private MaterialButton searchAll;
    private TextView noTopicText;
    private TextView noFolderText;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        searchAll = view.findViewById(R.id.searchAll);
        seeAllTopicBtn = view.findViewById(R.id.seeAllTopicBtn);
        seeAllFoldersBtn = view.findViewById(R.id.seeAllFoldersBtn);
        noTopicText = view.findViewById(R.id.noTopicText);
        noFolderText = view.findViewById(R.id.noFolderText);
        topicRecyclerView = view.findViewById(R.id.topicRecyclerView);
        folderRecyclerView = view.findViewById(R.id.folderRecyclerView);
        getUserVM();
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnBottomNavigationChangeListener && context instanceof OnDrawerNavigationPressedListener) {
            onBottomNavigationChangeListener = (OnBottomNavigationChangeListener) context;
            onDrawerNavigationPressedListener = (OnDrawerNavigationPressedListener) context;
        } else {
            throw new RuntimeException(requireContext().toString() + " must implement OnBottomNavigationChangeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onBottomNavigationChangeListener = null;
        onDrawerNavigationPressedListener = null;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            getUserVM();
        }
    }

    private void getUserVM() {
        userViewModel = new ViewModelProvider(requireActivity()).get(HomeDataViewModel.class);

        searchAll.setOnClickListener(v ->
                onBottomNavigationChangeListener.changeBottomNavigationItem(R.id.searchFragment, 1));

        seeAllTopicBtn.setOnClickListener(v ->
                onBottomNavigationChangeListener.changeBottomNavigationItem(R.id.libraryFragment, 0));

        seeAllFoldersBtn.setOnClickListener(v ->
                onBottomNavigationChangeListener.changeBottomNavigationItem(R.id.libraryFragment, 1));

        userViewModel.getTopicsList().observe(getViewLifecycleOwner(), topics -> {
            if(topics == null){
                topicRecyclerView.setVisibility(View.GONE);
                noTopicText.setVisibility(View.VISIBLE);
            }
            else if (topics.isEmpty()) {
                topicRecyclerView.setVisibility(View.GONE);
                noTopicText.setVisibility(View.VISIBLE);
            } else {
                topicRecyclerView.setVisibility(View.VISIBLE);
                noTopicText.setVisibility(View.GONE);
                if (topicAdapter == null) {
                    topicAdapter = new TopicAdapter(requireContext(), topics, R.layout.topic_home_item, this);
                    topicRecyclerView.setAdapter(topicAdapter);
                    topicRecyclerView.setClipToPadding(false);
                    topicRecyclerView.setClipChildren(false);
                    int screenHeight = getResources().getDisplayMetrics().heightPixels;
                    float nextItemTranslationX = 19f * screenHeight / 60;
                    topicRecyclerView.setPageTransformer((view, position) -> {
                        float absPosition = Math.abs(position);
                        view.setAlpha(Constant.MAX_ALPHA - (Constant.MAX_ALPHA - Constant.MIN_ALPHA) * absPosition);
                        float scale = Constant.MAX_SCALE - (Constant.MAX_SCALE - Constant.MIN_SCALE) * absPosition;
                        view.setScaleY(scale);
                        view.setScaleX(scale);
                        view.setTranslationX(-position * nextItemTranslationX);
                    });
                } else {
                    topicAdapter.setTopics(topics);
                }
            }
        });

        userViewModel.getFolderList().observe(getViewLifecycleOwner(), folders -> {
            if (folders.isEmpty()) {
                folderRecyclerView.setVisibility(View.GONE);
                noFolderText.setVisibility(View.VISIBLE);
            } else {
                folderRecyclerView.setVisibility(View.VISIBLE);
                noFolderText.setVisibility(View.GONE);
                if (folderAdapter == null) {
                    folderAdapter = new FolderAdapter(requireContext(), folders, R.layout.folder_home_item,
                            userViewModel.getUser().getValue(), this);
                    folderRecyclerView.setAdapter(folderAdapter);
                    folderRecyclerView.setClipToPadding(false);
                    folderRecyclerView.setClipChildren(false);
                    int screenHeight = getResources().getDisplayMetrics().heightPixels;
                    float nextItemTranslationX = 19f * screenHeight / 60;
                    folderRecyclerView.setPageTransformer((view, position) -> {
                        float absPosition = Math.abs(position);
                        view.setAlpha(Constant.MAX_ALPHA - (Constant.MAX_ALPHA - Constant.MIN_ALPHA) * absPosition);
                        float scale = Constant.MAX_SCALE - (Constant.MAX_SCALE - Constant.MIN_SCALE) * absPosition;
                        view.setScaleY(scale);
                        view.setScaleX(scale);
                        view.setTranslationX(-position * nextItemTranslationX);
                    });
                } else {
                    folderAdapter.setFolders(folders);
                }
            }
        });
    }

    @Override
    public void onTopicClick(Topic topic) {
        Intent intent = new Intent(requireContext(), TopicActivity.class);
        intent.putExtra("topic", topic);
        startActivity(intent);
    }

    @Override
    public void onFolderClick(Folder folder) {
        Intent intent = new Intent(requireContext(), FolderDetailActivity.class);
        intent.putExtra("folder", folder);
        startActivity(intent);
    }
}