package com.example.flashcard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.flashcard.adapter.LibraryScreenTabAdapter;
import com.example.flashcard.model.folder.Folder;
import com.example.flashcard.model.folder.FolderResponse;
import com.example.flashcard.model.folder.FoldersFormUserResponse;
import com.example.flashcard.repository.ApiClient;
import com.example.flashcard.repository.ApiService;
import com.example.flashcard.utils.OnDialogConfirmListener;
import com.example.flashcard.utils.Utils;
import com.example.flashcard.viewmodel.HomeDataViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LibraryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LibraryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private HomeDataViewModel homeDataViewModel;
    private LibraryScreenTabAdapter libraryScreenTabAdapter;
    private TabLayout tabLayout;
    private OnDialogConfirmListener onDialogConfirmListener;
    private ImageButton addBtn;
    private ViewPager2 pagesLayoutList;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LibraryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SolutionsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LibraryFragment newInstance(String param1, String param2) {
        LibraryFragment fragment = new LibraryFragment();
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
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        homeDataViewModel = new ViewModelProvider(requireActivity()).get(HomeDataViewModel.class);
        addBtn = view.findViewById(R.id.addBtn);
        pagesLayoutList = view.findViewById(R.id.pagesLayoutList);
        tabLayout = view.findViewById(R.id.tabLayoutList);
        libraryScreenTabAdapter = new LibraryScreenTabAdapter(requireActivity());
        pagesLayoutList.setAdapter(libraryScreenTabAdapter);
        new TabLayoutMediator(tabLayout, pagesLayoutList, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("TOPIC");
                    break;
                case 1:
                    tab.setText("FOLDER");
                    break;
            }
        }).attach();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                handleTabSelection(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                handleTabSelection(tab);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                handleTabSelection(tab);
            }
        });
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnDialogConfirmListener) {
            onDialogConfirmListener = (OnDialogConfirmListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnDialogConfirmListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onDialogConfirmListener = null;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            Bundle args = getArguments();
            if (args != null) {
                int tabIndex = args.getInt("tabIndex");
                pagesLayoutList.setCurrentItem(tabIndex, true);
            }
        }
    }

    private void handleTabSelection(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                addBtn.setOnClickListener(v -> {
                    Intent addTopicIntent = new Intent(requireActivity(), CreateTopicActivity.class);
                    startActivity(addTopicIntent);
                });
                break;
            case 1:
                addBtn.setOnClickListener(v -> {
                    Utils.showCreateFolderDialog(
                            Gravity.CENTER,
                            requireActivity(),
                            new OnDialogConfirmListener() {
                                @Override
                                public void onCreateFolderDialogConfirm(String folderName, String description) {
                                    ApiService apiService = ApiClient.getClient();
                                    Call<FolderResponse> call = apiService.createFolder(homeDataViewModel.getUser().getValue().getId(), folderName, description);
                                    call.enqueue(new Callback<FolderResponse>() {

                                        @Override
                                        public void onResponse(Call<FolderResponse> call, Response<FolderResponse> response) {
                                            if (response.isSuccessful()) {
                                                FolderResponse folderResponse = response.body();
                                                if (folderResponse != null && "OK".equals(folderResponse.getStatus())) {
                                                    Utils.showDialog(Gravity.CENTER, "Folder created", requireActivity());
                                                    Call<FoldersFormUserResponse> callFolder = apiService.getUserFolder(homeDataViewModel.getUser().getValue().getId());
                                                    callFolder.enqueue(new Callback<FoldersFormUserResponse>() {
                                                        @Override
                                                        public void onResponse(Call<FoldersFormUserResponse> call, Response<FoldersFormUserResponse> response) {
                                                            FoldersFormUserResponse foldersFormUserResponse = response.body();
                                                            List<Folder> listFolder = new ArrayList<>();
                                                            if (foldersFormUserResponse != null && "OK".equals(foldersFormUserResponse.getStatus())) {
                                                                listFolder = foldersFormUserResponse.getData();
                                                                homeDataViewModel.setFolderList(listFolder);
                                                            } else {
                                                                Log.d("Fetch data", "NOT OK");
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Call<FoldersFormUserResponse> call, Throwable t) {
                                                            Log.d("Fetch data", "ERROR " + t);

                                                        }
                                                    });
                                                } else {
                                                    Utils.showDialog(Gravity.CENTER, "Failed to create folder! Try again!", requireActivity());
                                                    Log.e("HomeActivity", "API call failed at home activity. Error: " + response.message());
                                                }
                                            } else {
                                                Utils.showDialog(Gravity.CENTER, "Something went wrong! Please try again!", requireActivity());
                                                Log.e("HomeActivity", "API call failed at home activity. Error: " + response.message());
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<FolderResponse> call, Throwable t) {
                                            Utils.showDialog(Gravity.CENTER, "Something went wrong", requireActivity());
                                        }
                                    });                    }

                                @Override
                                public void onAddTopicToFolderDialogConfirm() {

                                }

                                @Override
                                public void onDeleteFolderDialogConfirm() {

                                }
                            }
                    );
                });
                break;
        }
    }
}