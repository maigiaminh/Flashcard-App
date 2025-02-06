package com.example.flashcard;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.flashcard.adapter.TopicAdapter;
import com.example.flashcard.model.folder.Folder;
import com.example.flashcard.model.topic.Topic;
import com.example.flashcard.utils.CustomOnItemClickListener;
import com.example.flashcard.viewmodel.HomeDataViewModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TopicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TopicFragment extends Fragment implements CustomOnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private HomeDataViewModel homeDataViewModel;
    private TopicAdapter topicAdapter;
    private LinearLayout noTopicLayout;
    private RecyclerView topicRecyclerView;
    private Button addTopicBtn;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TopicFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TopicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TopicFragment newInstance(String param1, String param2) {
        TopicFragment fragment = new TopicFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_topic, container, false);
        noTopicLayout = view.findViewById(R.id.noTopicLayout);
        topicRecyclerView = view.findViewById(R.id.topicRecyclerView);
        addTopicBtn = view.findViewById(R.id.addTopicBtn);
        initViewModel();
        return view;
    }

    private void initViewModel() {
        homeDataViewModel = new ViewModelProvider(requireActivity()).get(HomeDataViewModel.class);
        homeDataViewModel.getTopicsList().observe(getViewLifecycleOwner(), topics -> {
            List<Topic> mutableList = topics;
            if (mutableList.isEmpty()) {
                noTopicLayout.setVisibility(View.VISIBLE);
                topicRecyclerView.setVisibility(View.GONE);
                addTopicBtn.setOnClickListener(v -> {
                    Intent addTopicIntent = new Intent(requireActivity(), CreateTopicActivity.class);
                    startActivity(addTopicIntent);
                });
            } else {
                noTopicLayout.setVisibility(View.GONE);
                topicRecyclerView.setVisibility(View.VISIBLE);
                topicAdapter = new TopicAdapter(requireActivity(), mutableList, R.layout.topic_library_item, this);
                topicRecyclerView.setHasFixedSize(true);
                topicRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
                topicRecyclerView.setAdapter(topicAdapter);
            }
        });
    }

    @Override
    public void onTopicClick(Topic topic) {
        Intent intent = new Intent(requireActivity(), TopicActivity.class);
        intent.putExtra("topic", topic);
        startActivity(intent);
    }

    @Override
    public void onFolderClick(Folder folder) {

    }

}