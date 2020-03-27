//Assignment Inclass 08
//File Name: Group12_InClass08
//Sanika Pol
//Snehal Kekane

package com.example.inclass08;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DisplayEmail extends AppCompatActivity {

    TextView tv_name,tv_sub,tv_date,tv_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_email);
        tv_name = findViewById(R.id.tv_from);
        tv_sub = findViewById(R.id.tv_sub);
        tv_date = findViewById(R.id.tv_craetedOn);
        tv_msg = findViewById(R.id.tv_msg);

        if(getIntent()!=null && getIntent().getExtras()!=null) {
            Email email = (Email) getIntent().getExtras().getSerializable(Inbox.EMAIL_DETAILS);
            Log.d("demo","Email : " + email.toString());
            tv_name.setText(email.getName());
            tv_date.setText(email.date.toString());
            tv_sub.setText(email.getSubject());
            tv_msg.setText(email.getMesage());
        }

        Button btn_close = (Button) findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
