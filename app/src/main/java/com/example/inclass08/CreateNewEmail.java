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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import android.widget.AdapterView.OnItemSelectedListener;
public class CreateNewEmail extends AppCompatActivity {


    EditText et_msg,et_subject;
    Button btn_send, btn_cancel;
    Spinner spn_users;
    final String TAG = "demo";
    ArrayList<User> users;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_email);

        et_subject = findViewById(R.id.et_subject);
        et_msg = findViewById(R.id.et_message);
        btn_send = findViewById(R.id.btn_send);
        btn_cancel  = findViewById(R.id.btn_cancel);
        spn_users = findViewById(R.id.spn_users);

        if(isConnected()){

            final OkHttpClient client = new OkHttpClient();


            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CreateNewEmail.this);
            String token = sharedPreferences.getString("token", null);
            Log.d(TAG, "Token in create : " + token);

            Request request = new Request.Builder()
                    .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/users")
                    .header("Authorization", "BEARER " + token)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        if (!response.isSuccessful()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(CreateNewEmail.this, "Something went wrong. Please try again!!", Toast.LENGTH_LONG).show();
                                }
                            });

                        } else {

                            Log.d("demo", "in success");
                            Headers responseHeaders = response.headers();
                            for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                                Log.d("demo", "onResponse" + responseHeaders.name(i) + ": " + responseHeaders.value(i));
                            }

                            JSONObject root = new JSONObject(responseBody.string());
                            JSONArray array = root.getJSONArray("users");

                            users = new ArrayList<User>();
                            for(int i =0 ;i<array.length();i++){
                                User user = new User();
                                JSONObject userObj = array.getJSONObject(i);
                                user.id = userObj.getInt("id");
                                user.fname = userObj.getString("fname");
                                user.lname = userObj.getString("lname");
                                Log.d(TAG,"User: "  + user.toString());
                                users.add(user);
                            }

                            Log.d(TAG,"usersize" + users.size());

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    ArrayAdapter<User> dataAdapter = new ArrayAdapter<User>(CreateNewEmail.this,android.R.layout.simple_spinner_dropdown_item,users);
                                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spn_users.setAdapter(dataAdapter);
                                    //spn_users.setSelection(0);
                                }
                            });



                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            btn_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user = (User) spn_users.getSelectedItem();
                    final OkHttpClient client = new OkHttpClient();

                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CreateNewEmail.this);
                    String token = sharedPreferences.getString("token", null);

                    RequestBody formBody = new FormBody.Builder()
                            .add("receiver_id", user.id+"")
                            .add("subject", et_subject.getText().toString().trim())
                            .add("message", et_msg.getText().toString().trim())
                            .build();

                    Request request = new Request.Builder()
                            .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox/add")
                            .post(formBody)
                            .header("Authorization","BEARER " + token)
                            .header("Content-Type" , "application/x-www-form-urlencoded")
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
                                            Toast.makeText(CreateNewEmail.this, "Somthing went wrong. Prease try again!!", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(CreateNewEmail.this, "Message sent successfully", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    });
                                }
                            }
                        }
                    });

                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });


        }
        else {
            Log.d(TAG,"Not connected");
            Toast.makeText(CreateNewEmail.this, "Not Connected", Toast.LENGTH_SHORT).show();
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
