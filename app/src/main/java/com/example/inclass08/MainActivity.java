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
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    final String TAG = "demo";
    public static final String USER_KEY = "USER";
    EditText et_email,et_password;
    Button btn_Login,btn_SignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Mailer");

        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        btn_Login = findViewById(R.id.btn_login);
        btn_SignUp = findViewById(R.id.btn_signUp);

        if (isConnected()) {
            btn_Login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final OkHttpClient client = new OkHttpClient();

                    RequestBody formBody = new FormBody.Builder()
                            .add("email", et_email.getText().toString().trim())
                            .add("password",et_password.getText().toString().trim())
                            .build();

                    Request request = new Request.Builder()
                            .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/login")
                            .post(formBody)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }

                        @Override public void onResponse(Call call, Response response) throws IOException {
                            try (ResponseBody responseBody = response.body()) {
                                if (!response.isSuccessful()){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(MainActivity.this,"Incorrect username or password!!",Toast.LENGTH_LONG).show();
                                        }
                                    });

                                    //throw new IOException("Unexpected code " + response);
                                }
                                else {

                                    Log.d("demo", "in success");
                                    Headers responseHeaders = response.headers();
                                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                                        Log.d("demo", "onResponse" + responseHeaders.name(i) + ": " + responseHeaders.value(i));
                                    }

                                    JSONObject root = new JSONObject(responseBody.string());
                                    User user = new User();
                                    String token = root.getString("token");
                                    user.fname = root.getString("user_fname");
                                    user.lname = root.getString("user_lname");
                                    Log.d(TAG, "user: " + user.toString());

                                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("token",token);
                                    editor.commit();

                                    Intent inbox = new Intent(MainActivity.this, Inbox.class);
                                    inbox.putExtra(USER_KEY, user);
                                    startActivity(inbox);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });

            btn_SignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent signUp = new Intent(MainActivity.this,SignUp.class);
                    startActivity(signUp);
                    finish();
                }
            });



        }
        else {
            Log.d(TAG,"Not connected");
            Toast.makeText(MainActivity.this, "Not Connected", Toast.LENGTH_SHORT).show();
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
