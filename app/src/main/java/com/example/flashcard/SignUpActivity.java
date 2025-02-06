package com.example.flashcard;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashcard.model.user.LoginResponse;
import com.example.flashcard.model.user.RegisterResponse;
import com.example.flashcard.model.user.User;
import com.example.flashcard.repository.ApiClient;
import com.example.flashcard.repository.ApiService;
import com.example.flashcard.utils.Constant;
import com.example.flashcard.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SignUpActivity extends AppCompatActivity {

    private boolean isPasswordShow = false;
    private EditText userEdt, emailEdt, passEdt, dateEdt;
    private ImageButton backBtn, calenderBtn, viewBtn;
    private Button signUpBtn;
    private ProgressBar progressLoading;
    private TextView tvLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        linkSetup();

        userEdt = findViewById(R.id.edtUser);
        emailEdt = findViewById(R.id.edtEmail);
        passEdt = findViewById(R.id.edtPassword);
        dateEdt = findViewById(R.id.edtDate);
        progressLoading = findViewById(R.id.progressLoading);


        signUpBtn = findViewById(R.id.signUpBtn);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressLoading.setVisibility(View.VISIBLE);
                ApiService apiService = ApiClient.getClient();
                String user = userEdt.getText().toString();
                String email = emailEdt.getText().toString();
                String pass = passEdt.getText().toString();
                String dob = dateEdt.getText().toString();
                int age = dob.isEmpty() ? 0 : (2023 - Integer.parseInt(dateEdt.getText().toString().split("/")[2]));

                if(user.isEmpty() || pass.isEmpty() || email.isEmpty() || age == 0){
                    Utils.showSnackBar(v, "Please fill all your information");
                    progressLoading.setVisibility(View.GONE);
                    return;
                }
                Call<RegisterResponse> call = apiService.register(user, pass, email, age);
                Log.d("Test Call", call.toString());
                call.enqueue(new Callback<RegisterResponse>() {
                    @Override
                    public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                        if (response.isSuccessful() && !response.body().getStatus().equals("NOT OK")) {
                            if(response.body().getStatus().equals("Invalid")){
                                progressLoading.setVisibility(View.GONE);
                                Utils.showDialog(Gravity.CENTER, "Username or Email already existed! Please try again!", SignUpActivity.this );
                            }
                            else{
                                progressLoading.setVisibility(View.GONE);
                                Utils.showDialog(Gravity.CENTER, "Account created", SignUpActivity.this );
                            }
                        } else {
                            Toast.makeText(SignUpActivity.this, "Error", Toast.LENGTH_SHORT).show();

                            Utils.showDialog(Gravity.CENTER, "Something went wrong! Please try again!", SignUpActivity.this );
                            Log.e("RegisterActivity", "API call failed. Error: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<RegisterResponse> call, Throwable t) {
                        Log.e("RegisterActivity", "API call failed. Throwable: " + t.getMessage());
                    }

                });
            }
        });
        calenderBtn = findViewById(R.id.calendarBtn);
        calenderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        viewBtn = findViewById(R.id.viewBtn);
        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPasswordShow){
                    isPasswordShow = !isPasswordShow;
                    passEdt.setTransformationMethod(null);
                }
                else{
                    isPasswordShow = !isPasswordShow;
                    passEdt.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                dateEdt.setText(selectedDate);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void linkSetup(){
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
    }

}