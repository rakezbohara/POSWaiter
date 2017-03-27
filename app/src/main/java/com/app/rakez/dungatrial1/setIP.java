package com.app.rakez.dungatrial1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class setIP extends AppCompatActivity {

    EditText ipAddress;
    Button saveIp;
    String requestFrom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_ip);
        Bundle source = getIntent().getExtras();
        requestFrom = source.getString("requestFrom");
        ipAddress = (EditText) findViewById(R.id.ipAddress);
        saveIp = (Button) findViewById(R.id.saveIP);
        saveIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!ipAddress.equals(null)){
                    String ip = ipAddress.getText().toString();
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyIP", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("IPAddress",ip);
                    editor.commit();
                    Toast.makeText(getApplicationContext(),"IP Address Saved",Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent in;
        if(requestFrom.equals("home")){
            in = new Intent(getApplicationContext(),NavActivity.class);

        }else{
            in = new Intent(getApplicationContext(),ScrollingActivity.class);
        }
        startActivity(in);
        finish();
    }


}
