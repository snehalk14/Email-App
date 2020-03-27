//Assignment Inclass 08
//File Name: Group12_InClass08
//Sanika Pol
//Snehal Kekane

package com.example.inclass08;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Inbox extends AppCompatActivity implements EmailAdapter.iEmail {

    final String TAG = "demo";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter = null;
    private RecyclerView.LayoutManager layoutManager;
    TextView tv_name;
    ArrayList<Email> emails;
    ImageView iv_newEmail,iv_logout;

    public static final String EMAIL_DETAILS = "EMAIL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        tv_name = findViewById(R.id.tv_Name);

        iv_newEmail = findViewById(R.id.iv_newEmail);
        iv_logout = findViewById(R.id.iv_logout);

        recyclerView = findViewById(R.id.recyclerInbox);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);


        if (isConnected()) {
            final User user;
            if(getIntent()!=null && getIntent().getExtras()!=null){
                user = (User) getIntent().getExtras().getSerializable(MainActivity.USER_KEY);
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Inbox.this);
                String token = sharedPreferences.getString("token", null);
                tv_name.setText(user.fname + " "+ user.lname);
                final OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox?")
                        .header("Authorization","BEARER " + token)
                        .build();


                client.newCall(request).enqueue(new Callback() {
                    @Override public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override public void onResponse(Call call, Response response) throws IOException {
                        try (ResponseBody responseBody = response.body()) {
                            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                            //Log.d("demo","in success");
                            Headers responseHeaders = response.headers();
                            for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                                Log.d(TAG , "onResponse" + responseHeaders.name(i) + ": " + responseHeaders.value(i));
                            }

                            //Log.d(TAG,"onResponse" + responseBody.string());
                            JSONObject root = new JSONObject(responseBody.string());
                            JSONArray messages = root.getJSONArray("messages");
                            emails = new ArrayList<Email>();
                            for(int i=0;i<messages.length();i++){
                                JSONObject message = messages.getJSONObject(i);
                                Email email = new Email();
                                email.setId(message.getInt("id"));
                                email.setName(message.getString("sender_fname") + " " + message.getString("sender_lname"));
                                email.setMesage(message.getString("message"));
                                email.setSubject(message.getString("subject"));
                                Log.d(TAG,"sub: " + email.getSubject());
                                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                email.setDate(inputFormat.parse(message.getString("created_at")));
                                emails.add(email);
                                Log.d(TAG,"email: " + email.toString());
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter = new EmailAdapter(emails,Inbox.this);
                                    recyclerView.setAdapter(mAdapter);
                                    mAdapter.notifyDataSetChanged();
                                }
                            });


                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            iv_newEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent CreateNewEmail = new Intent(Inbox.this, CreateNewEmail.class);
                    startActivity(CreateNewEmail);
                }
            });

            iv_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent login = new Intent(Inbox.this, MainActivity.class);
                    startActivity(login);
                    finish();
                }
            });

        }
        else {
            Log.d(TAG,"Not connected");
            Toast.makeText(Inbox.this, "Not Connected", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void deleteEmail(final int position) {
        Log.d(TAG,"Position : " + position);

        int id = emails.get(position).getId();
        Log.d(TAG,"ID: " + id);

        final OkHttpClient client = new OkHttpClient();
        String url = "http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox/delete/" + id;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Inbox.this);
        String token = sharedPreferences.getString("token", null);


        Request request = new Request.Builder()
                .url(url)
                .header("Authorization","BEARER " + token)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {

            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Inbox.this, "Something went wrong. Please try again!!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    else {
                        Log.d("demo","in delete");
                        Headers responseHeaders = response.headers();
                        for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                            Log.d(TAG , "onResponse" + responseHeaders.name(i) + ": " + responseHeaders.value(i));
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                emails.remove(position);
                                mAdapter.notifyDataSetChanged();
                            }
                        });

                    }



                }

            }
        });


    }

    @Override
    public void displayEmail(int position) {
        Log.d(TAG,"Position : " + position);
        Email email = new Email();
        email.setId(emails.get(position).getId());
        email.setName(emails.get(position).getName());
        Log.d(TAG,"Name: " + email.getName());
        email.setDate(emails.get(position).getDate());
        Log.d(TAG,"date: " + email.getDate().toString());
        email.setSubject(emails.get(position).getSubject());
        email.setMesage(emails.get(position).getMesage());
        Log.d(TAG,"ID: " + email.getId());

        Intent display = new Intent(Inbox.this,DisplayEmail.class);
        display.putExtra(EMAIL_DETAILS,email);
        startActivity(display);

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
