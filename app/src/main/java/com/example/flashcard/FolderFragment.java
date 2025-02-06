package com.example.flashcard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.flashcard.adapter.FolderAdapter;
import com.example.flashcard.model.folder.Folder;
import com.example.flashcard.model.folder.FolderResponse;
import com.example.flashcard.model.folder.FoldersFormUserResponse;
import com.example.flashcard.model.topic.Topic;
import com.example.flashcard.repository.ApiClient;
import com.example.flashcard.repository.ApiService;
import com.example.flashcard.utils.Constant;
import com.example.flashcard.utils.CustomOnItemClickListener;
import com.example.flashcard.utils.OnDialogConfirmListener;
import com.example.flashcard.utils.Utils;
import com.example.flashcard.viewmodel.HomeDataViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FolderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FolderFragment extends Fragment implements OnDialogConfirmListener, CustomOnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private HomeDataViewModel homeDataViewModel;
    private SharedPreferences sharedPreferences;
    private FolderAdapter folderAdapter;
    private ApiService apiService;
    private RecyclerView folderRecyclerView;
    private LinearLayout noFolderLayout;
    private Button addFolderBtn;

    public FolderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FolderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FolderFragment newInstance(String param1, String param2) {
        FolderFragment fragment = new FolderFragment();
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
        View view = inflater.inflate(R.layout.fragment_folder, container, false);
        addFolderBtn = view.findViewById(R.id.addFolderBtn);
        noFolderLayout = view.findViewById(R.id.noFolderLayout);
        folderRecyclerView = view.findViewById(R.id.folderRecyclerView);
        initViewModel();
        addFolderBtn.setOnClickListener(v -> {
            Utils.showCreateFolderDialog(Gravity.CENTER, requireActivity(), new OnDialogConfirmListener() {
                @Override
                public void onCreateFolderDialogConfirm(String folderName, String description) {
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
            });
        });
        return view;
    }

    private void initViewModel() {
        apiService = ApiClient.getClient();
        homeDataViewModel = new ViewModelProvider(requireActivity()).get(HomeDataViewModel.class);
        sharedPreferences = requireActivity().getSharedPreferences(Constant.SHARE_PREF, Context.MODE_PRIVATE);
        homeDataViewModel.getFolderList().observe(requireActivity(), folders -> {
            List<Folder> mutableList = folders;
            if (mutableList.isEmpty()) {
                folderRecyclerView.setVisibility(View.GONE);
                noFolderLayout.setVisibility(View.VISIBLE);
            } else {
                folderRecyclerView.setVisibility(View.VISIBLE);
                noFolderLayout.setVisibility(View.GONE);
                folderAdapter = new FolderAdapter(requireActivity(), mutableList, R.layout.folder_library_item, homeDataViewModel.getUser().getValue(), this);
                folderRecyclerView.setHasFixedSize(true);
                folderRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
                folderRecyclerView.setAdapter(folderAdapter);
            }
        });
    }


    @Override
    public void onTopicClick(Topic topic) {

    }

    @Override
    public void onFolderClick(Folder folder) {
        Intent intent = new Intent(requireActivity(), FolderDetailActivity.class);
        intent.putExtra("folder", folder);
        startActivity(intent);
    }

    @Override
    public void onCreateFolderDialogConfirm(String folderName, String description) {
        Log.d("Fragment Folder", "Clciked");
        Call<FolderResponse> call = apiService.createFolder(homeDataViewModel.getUser().getValue().getId(), folderName, description);
        call.enqueue(new Callback<FolderResponse>() {

            @Override
            public void onResponse(Call<FolderResponse> call, Response<FolderResponse> response) {
                if (response.isSuccessful()) {
                    FolderResponse folderResponse = response.body();
                    if (folderResponse != null && "OK".equals(folderResponse.getStatus())) {
                        Call<FoldersFormUserResponse> callFolder = apiService.getUserFolder(homeDataViewModel.getUser().getValue().getId());
                        callFolder.enqueue(new Callback<FoldersFormUserResponse>() {
                            @Override
                            public void onResponse(Call<FoldersFormUserResponse> call, Response<FoldersFormUserResponse> response) {
                                FoldersFormUserResponse foldersFormUserResponse = response.body();
                                List<Folder> listFolder = new ArrayList<>();
                                if (foldersFormUserResponse != null && "OK".equals(foldersFormUserResponse.getStatus())) {
                                    listFolder = foldersFormUserResponse.getData();
                                    homeDataViewModel.setFolderList(listFolder);
                                    Utils.showDialog(Gravity.CENTER, "Folder created", requireActivity());
                                } else {
                                    Log.d("Fetch data", "NOT OK");
                                    Utils.showDialog(Gravity.CENTER, "Error", requireActivity());
                                }
                            }

                            @Override
                            public void onFailure(Call<FoldersFormUserResponse> call, Throwable t) {
                                Log.d("Fetch data", "ERROR " + t);

                            }
                        });
                        Log.d("CreateTopicActivity", "Create success");
                        Utils.showDialog(Gravity.CENTER, "Folder created", requireActivity());
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
        });
    }

    @Override
    public void onAddTopicToFolderDialogConfirm() {

    }

    @Override
    public void onDeleteFolderDialogConfirm() {

    }
}