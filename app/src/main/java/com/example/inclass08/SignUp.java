
//Assignment Inclass 08
//File Name: Group12_InClass08
//Sanika Pol
//Snehal Kekane
package com.example.inclass08;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SignUp extends AppCompatActivity {

    TextView tv_email, tv_password, tv_fname, tv_lname, tv_repass;
    Button btn_signUp, btn_cancel;

    String user_fname, user_lname;
    final String TAG = "demo";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        tv_email = findViewById(R.id.tv_email);
        tv_password = findViewById(R.id.tv_choosePassword);
        tv_repass = findViewById(R.id.tv_repeatPassword);
        tv_fname = findViewById(R.id.tv_firstName);
        tv_lname = findViewById(R.id.tv_lastName);
        btn_signUp = findViewById(R.id.btn_sign_up);
        btn_cancel = findViewById(R.id.btn_cancel);

        if(isConnected()){
            btn_signUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final OkHttpClient client = new OkHttpClient();


                    if (tv_password.getText().toString().equals(tv_repass.getText().toString())) {
                        String password = tv_repass.getText().toString();


                        RequestBody formBody = new FormBody.Builder()
                                .add("email", tv_email.getText().toString().trim())
                                .add("password", tv_password.getText().toString().trim())
                                .add("fname", tv_fname.getText().toString().trim())
                                .add("lname", tv_lname.getText().toString().trim())
                                .build();

                        Request request = new Request.Builder()
                                .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/signup")
                                .post(formBody)
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                try (ResponseBody responseBody = response.body()) {
                                    if (!response.isSuccessful()) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(SignUp.this,"Something went wrong. Please try again!!",Toast.LENGTH_LONG).show();
                                            }
                                        });

                                    } else {
                                        String obj = response.body().string();
                                        JSONObject Jsonobject = new JSONObject(obj);
                                        String token = Jsonobject.getString("token");
                                        user_fname = Jsonobject.getString("user_fname");
                                        user_lname = Jsonobject.getString("user_lname");

                                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SignUp.this);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("token",token);
                                        editor.commit();

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(SignUp.this, "User created successfully", Toast.LENGTH_SHORT).show();
                                                Intent login  = new Intent(SignUp.this,MainActivity.class);
                                                startActivity(login);
                                                finish();
                                            }
                                        });
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }else {
                        Toast.makeText(SignUp.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent login  = new Intent(SignUp.this,MainActivity.class);
                    startActivity(login);
                }
            });
        }
        else {
            Log.d(TAG,"Not connected");
            Toast.makeText(SignUp.this, "Not Connected", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !((NetworkInfo) networkInfo).isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }
}

