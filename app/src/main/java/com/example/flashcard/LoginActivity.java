package com.example.flashcard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.flashcard.model.user.LoginResponse;
import com.example.flashcard.model.user.User;
import com.example.flashcard.repository.ApiClient;
import com.example.flashcard.repository.ApiService;
import com.example.flashcard.utils.Constant;
import com.example.flashcard.utils.ResetPasswordConfirmListener;
import com.example.flashcard.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText userEdt, passEdt;
    private Button loginBtn;
    private ImageButton backBtn;
//    private ApiService apiService = ApiClient.getClient().create(ApiService.class);
    private SharedPreferences sharedPref;
    private LinearLayout contentLayout;
    private ProgressBar progressLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.login_layout);
        userEdt = findViewById(R.id.edtUser);
        passEdt = findViewById(R.id.edtPassword);
        contentLayout = findViewById(R.id.contentLayout);
        progressLoading = findViewById(R.id.progressLoading);

        backBtn = findViewById(R.id.backBtn);
        sharedPref = getSharedPreferences(Constant.SHARE_PREF, Context.MODE_PRIVATE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loginBtn = findViewById(R.id.loginBtn);

        linkSetup();
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressLoading.setVisibility(View.VISIBLE);
                ApiService apiService = ApiClient.getClient();
                String user = userEdt.getText().toString();
                String pass = passEdt.getText().toString();

                if(user.isEmpty() || pass.isEmpty()){
                    Utils.showSnackBar(v, "Please fill your username and password");
                    progressLoading.setVisibility(View.GONE);
                    return;
                }
                Call<LoginResponse> call = apiService.login(user, pass);
                Log.d("Test Call", call.toString());
                call.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if (response.isSuccessful() && !response.body().getStatus().equals("NOT OK")) {
                            if(response.body().getStatus().equals("Invalid")){
                                Log.d("Test", "Wrong");
                                progressLoading.setVisibility(View.GONE);
                                Utils.showDialog(Gravity.CENTER, "Wrong username or password", LoginActivity.this );
                            }
                            else{
                                Log.d("API", "Raw JSON response: " + new Gson().toJson(response.body()));
                                LoginResponse loginResponse = response.body();
                                User user = loginResponse.getUser();
                                Log.d("LoginActivity", "Received user data: " +
                                        "ID: " + user.getId() +
                                        ", Username: " + user.getUsername() +
                                        ", Password: " + user.getPassword() +
                                        ", Email: " + user.getEmail() +
                                        ", Age: " + user.getAge() +
                                        ", Avatar: " + user.getProfileImage());

                                Gson gson = new GsonBuilder().setLenient().create();
                                String json = gson.toJson(user);
                                Log.d("API", "API call successful. Received user data: " + json);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString(Constant.USER_DATA, gson.toJson(user));
                                editor.apply();

                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            progressLoading.setVisibility(View.GONE);

                            Utils.showDialog(Gravity.CENTER, "Something went wrong! Please try again!", LoginActivity.this );
                            Log.e("LoginActivity", "API call failed. Error: " + response.message());
                        }
                    }
                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        // Log lỗi onFailure
                        progressLoading.setVisibility(View.GONE);

                        Log.e("LoginActivity", "API call failed. Throwable: " + t.getMessage());
                    }
                });
            }
        });



    }

    private void showForgetPassDialog(){
        Utils.showResetPasswordDialog(Gravity.CENTER, LoginActivity.this, new ResetPasswordConfirmListener() {
            @Override
            public void onConfirm(String email) {
                Log.d("Reset pass", "Confirm");

            }

            @Override
            public void changePassword(String oldPassword, String newPassword) {
                // Implement changePassword if needed
            }
        });
    }

    private void linkSetup(){
        Link userLink = new Link("username")
                .setTextColor(Color.parseColor("#0000FF"))
                .setHighlightAlpha(.4f)
                .setUnderlined(false)
                .setBold(true)
                .setOnLongClickListener(new Link.OnLongClickListener() {
                    @Override
                    public void onLongClick(String clickedText) {

                    }
                })
                .setOnClickListener(new Link.OnClickListener() {
                    @Override
                    public void onClick(String clickedText) {
                        showForgetPassDialog();
                    }
                });

        Link passLink = new Link("password")
                .setTextColor(Color.parseColor("#0000FF"))
                .setHighlightAlpha(.4f)
                .setUnderlined(false)
                .setBold(true)
                .setOnLongClickListener(new Link.OnLongClickListener() {
                    @Override
                    public void onLongClick(String clickedText) {

                    }
                })
                .setOnClickListener(new Link.OnClickListener() {
                    @Override
                    public void onClick(String clickedText) {
                        showForgetPassDialog();
                    }
                });

        Link termsLink = new Link("Terms of service")
                .setTextColor(Color.parseColor("#0000FF"))
                .setHighlightAlpha(.4f)
                .setUnderlined(false)
                .setBold(true)
                .setOnLongClickListener(new Link.OnLongClickListener() {
                    @Override
                    public void onLongClick(String clickedText) {

                    }
                })
                .setOnClickListener(new Link.OnClickListener() {
                    @Override
                    public void onClick(String clickedText) {

                    }
                });

        Link policyLink = new Link("Privacy policy")
                .setTextColor(Color.parseColor("#0000FF"))
                .setHighlightAlpha(.4f)
                .setUnderlined(false)
                .setBold(true)
                .setOnLongClickListener(new Link.OnLongClickListener() {
                    @Override
                    public void onLongClick(String clickedText) {

                    }
                })
                .setOnClickListener(new Link.OnClickListener() {
                    @Override
                    public void onClick(String clickedText) {

                    }
                });

        LinkBuilder.on(findViewById(R.id.tvLink))
                .addLink(termsLink)
                .addLink(policyLink)
                .build();
        LinkBuilder.on(findViewById(R.id.tvUserAndPass))
                .addLink(userLink)
                .addLink(passLink)
                .build();
    }

}