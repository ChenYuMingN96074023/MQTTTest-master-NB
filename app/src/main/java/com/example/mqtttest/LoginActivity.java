package com.example.mqtttest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mqtttest.mqtt.MqttHelper;

public class LoginActivity extends AppCompatActivity {

    private Button BtnLogin,BtnTest;
    private EditText EtAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EtAccount= findViewById(R.id.et_account);
        BtnLogin = findViewById(R.id.btn_login);
        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(LoginActivity.this,MainActivity.class);
                loginIntent.putExtra("MY_CLIENT_ID",EtAccount.getText().toString());
                startActivity(loginIntent);
            }
        });

        BtnTest = findViewById(R.id.btn_test);
        BtnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent testCRLRvIntent = new Intent(LoginActivity.this, ChatRoomListActivity.class);
                startActivity(testCRLRvIntent);
            }
        });
    }
}
