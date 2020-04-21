package com.example.mqtttest;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mqtttest.database.DBHelper_CRL;
import com.example.mqtttest.database.DBHelper_ChatMessages;
import com.example.mqtttest.mqtt.MqttHelper;
import com.example.mqtttest.recyclerChatRoom.MQTTAdapter;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final int ERRROR = -1;
    public static final int TEXT = 1;
    public static final int PHOTO = 2;
    private static final int INTENT_GET_IMAGE = 90;
    private static final String TAG = MainActivity.class.getSimpleName();
    private MqttHelper mqtt;
    private EditText editText;
    private String myClientId,myTopic;
    private RecyclerView recyclerView_CR;
    private DBHelper_ChatMessages dbHelper_chatMessages;
    private DBHelper_CRL dbHelper_crl;
    private ArrayList arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences preferences = getSharedPreferences("testSharePreferences", MODE_PRIVATE);
        myClientId = preferences.getString("clientID", "");

        myTopic = getIntent().getStringExtra("MY_TOPIC");
        recyclerView_CR = findViewById(R.id.recyclerView_CR);
        editText = findViewById(R.id.editText);

        if (myClientId != null && myTopic != null) {
            mqtt = new MqttHelper(this, myTopic, myClientId, recyclerView_CR);
        } else {
            Toast.makeText(MainActivity.this,"fail \n ID:"+ myClientId + "Topic:"+ myTopic,Toast.LENGTH_LONG).show();
        }

        dbHelper_crl = new DBHelper_CRL(MainActivity.this, null,null,1);
        dbHelper_crl.setUnreadMsgNum(myTopic, 0);//設未讀訊息為0

        dbHelper_chatMessages = new DBHelper_ChatMessages(MainActivity.this,null, myTopic,null,1);
        arrayList = dbHelper_chatMessages.getRecSet(myTopic);
        recyclerView_CR.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView_CR.scrollToPosition(arrayList.size()-1);
        recyclerView_CR.setAdapter(new MQTTAdapter(MainActivity.this,arrayList,myClientId));
    }

    @Override
    public void onResume() {
        super.onResume();
        if(dbHelper_chatMessages==null){
            dbHelper_chatMessages = new DBHelper_ChatMessages(MainActivity.this,null, myTopic,null,1);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(dbHelper_chatMessages!=null){
            dbHelper_chatMessages.close();
            dbHelper_chatMessages = null;
            Log.d("tag","closing table:"+myTopic);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("tag", "finished MainActivity");
        MainActivity.this.finish();

    }

    public void buttonPublisher(View view) {
        mqtt.startPublish(myTopic, editText.getText().toString(), TEXT);
        editText.setText("");
    }

    public void imgPublisher(View view) {
        Intent imgIntent = new Intent();
        Log.d(TAG,"Photo0");
        imgIntent.setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(imgIntent, INTENT_GET_IMAGE);
    }

    // activity for result

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,"Photo1");
        switch (requestCode) {
            case INTENT_GET_IMAGE:
                Log.d(TAG,"Photo2");
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        Uri uri = data.getData();
                        ContentResolver cr = this.getContentResolver();
                        try {
                            Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);/////////////////
                            byte[] imageByte = byteArrayOutputStream.toByteArray();
                            String imgEncode = Base64.encodeToString(imageByte, Base64.DEFAULT);
                            mqtt.startPublish(myTopic, imgEncode, PHOTO);
                            Log.d(TAG, "photo encode :" + imgEncode);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            Log.d(TAG,"Error msg from photo "+e.getMessage());
                        }
                    }
                }
                break;
        }
    }

    // options Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
//            mqtt.startSubscribe();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
