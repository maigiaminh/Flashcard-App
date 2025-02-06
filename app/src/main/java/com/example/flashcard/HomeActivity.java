package com.example.flashcard;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.flashcard.model.folder.Folder;
import com.example.flashcard.model.folder.FolderResponse;
import com.example.flashcard.model.folder.FoldersFormUserResponse;
import com.example.flashcard.model.topic.PublicTopicResponse;
import com.example.flashcard.model.topic.Topic;
import com.example.flashcard.model.topic.Topics;
import com.example.flashcard.model.topic.TopicsFormUserResponse;
import com.example.flashcard.model.user.User;
import com.example.flashcard.repository.ApiClient;
import com.example.flashcard.repository.ApiService;
import com.example.flashcard.utils.Constant;
import com.example.flashcard.utils.OnBottomNavigationChangeListener;
import com.example.flashcard.utils.OnDialogConfirmListener;
import com.example.flashcard.utils.OnDrawerNavigationPressedListener;
import com.example.flashcard.utils.Utils;
import com.example.flashcard.viewmodel.HomeDataViewModel;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements OnBottomNavigationChangeListener, OnDrawerNavigationPressedListener, OnDialogConfirmListener {

    FloatingActionButton fab;
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    FragmentManager fragmentManager;
    Fragment homeFragment;
    Fragment searchFragment;
    Fragment libraryFragment;
    Fragment profileFragment;
    Fragment recentFragment;
    private ActivityResultLauncher<Intent> launchSettingsForResult;
    private SharedPreferences sharedPref;
    private HomeDataViewModel userViewModel;
    private BroadcastReceiver connectivityReceiver;
    private final int REQUEST_INTERNET_PERMISSION = 666;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        launchSettingsForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
            if(result.getResultCode() == RESULT_OK){
                Intent intent = getIntent();
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                overridePendingTransition(0, 0);
                startActivity(intent);
            }
        });

        sharedPref = getSharedPreferences(Constant.SHARE_PREF, Context.MODE_PRIVATE);
        if (sharedPref.getString(Constant.USER_DATA, null) == null) {
            backtoIntro();
            return;
        } else {
            userViewModel = new ViewModelProvider(this).get(HomeDataViewModel.class);
            userViewModel.setUser(new Gson().fromJson(sharedPref.getString(Constant.USER_DATA, null), User.class));
        }

        bottomNavigationView = findViewById(R.id.bottomNavbar);

        fab = findViewById(R.id.fab);
        drawerLayout = findViewById(R.id.homeDrawerLayout);
        fragmentManager = getSupportFragmentManager();
        homeFragment = new HomeFragment();
        searchFragment = new SearchFragment();
        libraryFragment = new LibraryFragment();
        profileFragment = new ProfileFragment();
        recentFragment = homeFragment;

        fragmentManager.beginTransaction().add(R.id.fragmentContainer, homeFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragmentContainer, searchFragment).hide(searchFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragmentContainer, libraryFragment).hide(libraryFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragmentContainer, profileFragment).hide(profileFragment).commit();

        NavigationView navigationView = findViewById(R.id.drawerNavigation);
        BottomAppBar bottomAppBar = findViewById(R.id.bottomAppBar);
        setSupportActionBar(bottomAppBar);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.nav_home:
                        fragmentManager.beginTransaction().hide(recentFragment).show(homeFragment).commit();
                        recentFragment = homeFragment;
                        bottomNavigationView.setSelectedItemId(R.id.home);
                        break;
                    case R.id.nav_search:
                        fragmentManager.beginTransaction().hide(recentFragment).show(searchFragment).commit();
                        recentFragment = searchFragment;
                        bottomNavigationView.setSelectedItemId(R.id.search);
                        break;
                    case R.id.nav_library:
                        fragmentManager.beginTransaction().hide(recentFragment).show(libraryFragment).commit();
                        recentFragment = libraryFragment;
                        bottomNavigationView.setSelectedItemId(R.id.library);
                        break;
                    case R.id.nav_profile:
                        fragmentManager.beginTransaction().hide(recentFragment).show(profileFragment).commit();
                        recentFragment = profileFragment;
                        bottomNavigationView.setSelectedItemId(R.id.profile);
                        break;
                    case R.id.nav_logout:
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.remove(Constant.USER_DATA);
                        editor.apply();
                        backtoIntro();
                        return true;
                }
                drawerLayout.closeDrawer(GravityCompat.START);

                return true;
            }
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.home:
                    fragmentManager.beginTransaction().hide(recentFragment).show(homeFragment).commit();
                    recentFragment = homeFragment;
                    navigationView.setCheckedItem(R.id.nav_home);
                    break;
                case R.id.search:
                    fragmentManager.beginTransaction().hide(recentFragment).show(searchFragment).commit();
                    recentFragment = searchFragment;
                    navigationView.setCheckedItem(R.id.nav_search);
                    break;
                case R.id.library:
                    fragmentManager.beginTransaction().hide(recentFragment).show(libraryFragment).commit();
                    recentFragment = libraryFragment;
                    navigationView.setCheckedItem(R.id.nav_library);
                    break;
                case R.id.profile:
                    fragmentManager.beginTransaction().hide(recentFragment).show(profileFragment).commit();
                    recentFragment = profileFragment;
                    navigationView.setCheckedItem(R.id.nav_profile);
                    break;
            }

            return true;
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });

        connectivityReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){
                    checkInternetConnection();
                }
            }
        };

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectivityReceiver, filter);

        if (checkSelfPermission(android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.INTERNET}, REQUEST_INTERNET_PERMISSION
            );
        } else {
            checkInternetConnection();
        }
    }

    private void showBottomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        MaterialButton addTopicBtn = dialog.findViewById(R.id.addTopicBtn);
        MaterialButton addFolderBtn = dialog.findViewById(R.id.addFolderBtn);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        addTopicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CreateTopicActivity.class);
                startActivity(intent);
                dialog.dismiss();
                Toast.makeText(HomeActivity.this,"Create a topic is clicked",Toast.LENGTH_SHORT).show();
            }
        });

        addFolderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showCreateFolderDialog(Gravity.CENTER, v.getContext(), new OnDialogConfirmListener() {
                    @Override
                    public void onCreateFolderDialogConfirm(String folderName, String description) {
                        ApiService apiService = ApiClient.getClient();
                        Call<FolderResponse> call = apiService.createFolder(userViewModel.getUser().getValue().getId(), folderName, description);

                        call.enqueue(new Callback<FolderResponse>() {

                            @Override
                            public void onResponse(Call<FolderResponse> call, Response<FolderResponse> response) {
                                if (response.isSuccessful()) {
                                    FolderResponse folderResponse = response.body();
                                    if (folderResponse != null && "OK".equals(folderResponse.getStatus())) {
                                        Log.d("CreateTopicActivity", "Create success");
                                        Utils.showDialog(Gravity.CENTER, "Folder created", HomeActivity.this );
                                        initViewModel();
                                    } else {
                                        Toast.makeText(HomeActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                        Utils.showDialog(Gravity.CENTER, "Failed to create folder! Try again!", HomeActivity.this );
                                        Log.e("HomeActivity", "API call failed at home activity. Error: " + response.message());
                                    }
                                } else {
                                    Toast.makeText(HomeActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    Utils.showDialog(Gravity.CENTER, "Something went wrong! Please try again!", HomeActivity.this );
                                    Log.e("HomeActivity", "API call failed at home activity. Error: " + response.message());
                                }
                            }

                            @Override
                            public void onFailure(Call<FolderResponse> call, Throwable t) {
                                Utils.showDialog(Gravity.CENTER, "Something went wrong", HomeActivity.this );
                            }
                        });
                    }

                    @Override
                    public void onAddTopicToFolderDialogConfirm() {

                    }

                    @Override
                    public void onDeleteFolderDialogConfirm() {

                    }
                });
                dialog.dismiss();
                Toast.makeText(HomeActivity.this,"Create a folder is Clicked",Toast.LENGTH_SHORT).show();
            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    @Override
    public void changeBottomNavigationItem(int itemIndex, int tabIndex) {
        if (itemIndex == R.id.homeFragment) {
            fragmentManager.beginTransaction().hide(recentFragment).show(homeFragment).commit();
            recentFragment = homeFragment;
            bottomNavigationView.setSelectedItemId(R.id.home);
        } else if (itemIndex == R.id.profileFragment) {
            fragmentManager.beginTransaction().hide(recentFragment).show(profileFragment).commit();
            recentFragment = profileFragment;
            bottomNavigationView.setSelectedItemId(R.id.profile);
        } else if (itemIndex == R.id.libraryFragment) {
            Bundle bundle = new Bundle();
            bundle.putInt("tabIndex", tabIndex);
            libraryFragment.setArguments(bundle);
            fragmentManager.beginTransaction().hide(recentFragment).show(libraryFragment).commit();
            recentFragment = libraryFragment;
            bottomNavigationView.setSelectedItemId(R.id.library);
        } else if (itemIndex == R.id.searchFragment) {
            Bundle bundle = new Bundle();
            bundle.putInt("tabIndex", tabIndex);
            searchFragment.setArguments(bundle);
            fragmentManager.beginTransaction().hide(recentFragment).show(searchFragment).commit();
            recentFragment = searchFragment;
            bottomNavigationView.setSelectedItemId(R.id.search);
        } else {
            fragmentManager.beginTransaction().hide(recentFragment).show(homeFragment).commit();
            recentFragment = homeFragment;
            bottomNavigationView.setSelectedItemId(R.id.home);
        }

    }

    @Override
    public void onCreateFolderDialogConfirm(String folderName, String description) {

    }

    @Override
    public void onAddTopicToFolderDialogConfirm() {

    }

    @Override
    public void onDeleteFolderDialogConfirm() {

    }

    @Override
    public void openDrawerFromFragment() {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }


    private class StartActivityForResult {
    }

    private void backtoIntro() {
        Intent intro = new Intent(this, MainActivity.class);
        intro.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intro);
    }

    private void checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;

        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            initViewModel();
        } else {
            Utils.showSnackBar(getCurrentFocus(), "No Internet Connection");
        }
    }

    private void initViewModel() {
        ApiService apiService = ApiClient.getClient();
        Call<TopicsFormUserResponse> callTopics = apiService.getUserTopic(userViewModel.getUser().getValue().getId());
        callTopics.enqueue(new Callback<TopicsFormUserResponse>() {
            @Override
            public void onResponse(Call<TopicsFormUserResponse> call, Response<TopicsFormUserResponse> response) {
                TopicsFormUserResponse topicsFormUserResponse = response.body();
                List<Topic> listTopic = new ArrayList<>();
                if (topicsFormUserResponse != null && "OK".equals(topicsFormUserResponse.getStatus())) {
                    for(Topics t: topicsFormUserResponse.getData()){
                        listTopic.addAll(t.getAdditionalInfo());
                    }
                    userViewModel.setTopicsList(listTopic);
                } else {
                    Log.d("Fetch data", "NOT OK");
                }
            }

            @Override
            public void onFailure(Call<TopicsFormUserResponse> call, Throwable t) {
                Log.d("Fetch data", "ERROR " + t);

            }
        });

        Call<FoldersFormUserResponse> callFolder = apiService.getUserFolder(userViewModel.getUser().getValue().getId());
        callFolder.enqueue(new Callback<FoldersFormUserResponse>() {
            @Override
            public void onResponse(Call<FoldersFormUserResponse> call, Response<FoldersFormUserResponse> response) {
                FoldersFormUserResponse foldersFormUserResponse = response.body();
                List<Folder> listFolder = new ArrayList<>();
                if (foldersFormUserResponse != null && "OK".equals(foldersFormUserResponse.getStatus())) {
                    listFolder = foldersFormUserResponse.getData();
                    userViewModel.setFolderList(listFolder);
                } else {
                    Log.d("Fetch data", "NOT OK");
                }
            }

            @Override
            public void onFailure(Call<FoldersFormUserResponse> call, Throwable t) {
                Log.d("Fetch data", "ERROR " + t);

            }
        });

        Call<PublicTopicResponse> callPublicTopic = apiService.getPublicTopic(userViewModel.getUser().getValue().getId());
        callPublicTopic.enqueue(new Callback<PublicTopicResponse>() {
            @Override
            public void onResponse(Call<PublicTopicResponse> call, Response<PublicTopicResponse> response) {
                PublicTopicResponse publicTopicResponse = response.body();
                List<Topic> publicTopic = publicTopicResponse.getData();
                if(publicTopic != null){
                    userViewModel.setPublicTopicsList(publicTopic);
                }
            }

            @Override
            public void onFailure(Call<PublicTopicResponse> call, Throwable t) {
                Log.d("Fetch data", "ERROR " + t);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkInternetConnection();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(connectivityReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_INTERNET_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initViewModel();
            } else {
                Utils.showSnackBar(getCurrentFocus(), "Permission Denied");
            }
        }
    }
}