package com.example.mqtttest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



public class LoginActivity extends AppCompatActivity {

    private Button BtnLogin;
    private EditText EtAccount;
    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EtAccount= findViewById(R.id.et_account);
        BtnLogin = findViewById(R.id.btn_login);

        SharedPreferences preferences = getSharedPreferences("testSharePreferences", MODE_PRIVATE);
        String clientID = preferences.getString("clientID", null);
        if (clientID != null){
            EtAccount.setText(clientID);
            login();
//            Log.d(TAG, "automatically login, clientID:" + clientID);
        }

    }
///////////測試push到github
    public void loginButton(View view){
        AlertDialog.Builder ad=new AlertDialog.Builder(LoginActivity.this);
        ad.setTitle("登入");
        ad.setMessage("您將以" + EtAccount.getText().toString() + "登入聊天\n" +
                "在登入前請先確認是否有重複登入，或是其他人已使用此帳號\n" +
                "確認登入?");
        ad.setPositiveButton("是", new DialogInterface.OnClickListener() {//登入按鈕
            public void onClick(DialogInterface dialog, int i) {
                // TODO Auto-generated method stub
                login();
            }
        });
        ad.setNegativeButton("否",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                //不用執行任何操作
            }
        });
        ad.show();//顯示對話框
//        login();
    }
    private void login(){
        Intent loginIntent = new Intent(LoginActivity.this, ChatRoomListActivity.class);

        SharedPreferences preferences = getSharedPreferences("testSharePreferences", MODE_PRIVATE);
        preferences.edit()
                .putString("clientID", EtAccount.getText().toString())
                .commit();

        startActivity(loginIntent);
        LoginActivity.this.finish();//關閉Activity
    }
}
