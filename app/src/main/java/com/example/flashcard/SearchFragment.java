package com.example.flashcard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.flashcard.adapter.TopicAdapter;
import com.example.flashcard.model.folder.Folder;
import com.example.flashcard.model.topic.Topic;
import com.example.flashcard.utils.CustomOnItemClickListener;
import com.example.flashcard.viewmodel.HomeDataViewModel;
import com.google.android.material.internal.TextWatcherAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements CustomOnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private HomeDataViewModel homeDataViewModel;
    private TopicAdapter topicAdapter;
    private List<Topic> originalTopicsList;
    private EditText searchBar;
    private ImageView backButton;
    private RecyclerView publicTopicRecyclerView;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LibraryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        searchBar = view.findViewById(R.id.searchBar);
        publicTopicRecyclerView = view.findViewById(R.id.publicTopicRecyclerView);
        initVM();
        homeDataViewModel.getPublicTopicsList().observe(getViewLifecycleOwner(), topicList -> {
            originalTopicsList = topicList;
            List<Topic> mutableList = new ArrayList<>(topicList);

            if (topicAdapter == null || topicAdapter.getItemCount() == 0) {
                topicAdapter = new TopicAdapter(requireActivity(), mutableList, R.layout.topic_library_item, this);
                publicTopicRecyclerView.setHasFixedSize(true);
                publicTopicRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
                publicTopicRecyclerView.setAdapter(topicAdapter);
            } else {
                topicAdapter.setTopics(mutableList);
            }
        });

        searchBar.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<Topic> mutableList = new ArrayList<>(originalTopicsList);
                String searchText = s.toString().toLowerCase();

                if (!searchText.isEmpty()) {
                    List<Topic> filteredList = new ArrayList<>();
                    for (Topic topic : mutableList) {
                        if (topic.getTopicName().toLowerCase().contains(searchText)){
                            filteredList.add(topic);
                        }
                    }
                    topicAdapter.setTopics(filteredList);
                } else {
                    topicAdapter.setTopics(mutableList);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private void initVM() {
        originalTopicsList = new ArrayList<>();
        homeDataViewModel = new ViewModelProvider(requireActivity()).get(HomeDataViewModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        searchBar.getText().clear();
        searchBar.clearFocus();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            Bundle args = getArguments();
            if (args != null) {
                int isFocused = args.getInt("tabIndex");
                if (isFocused == 1) {
                    searchBar.requestFocus();
                    InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT);
                }
            }
            if (args != null) {
                args.clear();
            }
        }
    }

    @Override
    public void onTopicClick(Topic topic) {
        Intent intent = new Intent(requireContext(), TopicActivity.class);
        intent.putExtra("topic", topic);
        startActivity(intent);
    }

    @Override
    public void onFolderClick(Folder folder) {

    }
}