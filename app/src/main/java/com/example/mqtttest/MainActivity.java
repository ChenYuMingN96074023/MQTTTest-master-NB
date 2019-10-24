package com.example.mqtttest;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.mqtttest.mqtt.MqttHelper;
import com.example.mqtttest.recyclerMQTT.MQTTFunction;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {

    public static final int ERRROR = -1;
    public static final int TEXT = 1;
    public static final int PHOTO = 2;
    private static final int INTENT_GET_IMAGE = 90;
    private static final String TAG = MainActivity.class.getSimpleName();
    private MqttHelper mqtt;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        //String myClientId = "NCKU1";//寫死時的寫法
        String myClientId = getIntent().getStringExtra("MY_CLIENT_ID");

        if (myClientId != null) {

            MQTTFunction mqttFunction = new MQTTFunction(this, recyclerView, myClientId);
            mqtt = new MqttHelper(mqttFunction, "NCKU_TOPIC", myClientId, recyclerView);
//            mqtt.startSubscribe();
        }
        editText = findViewById(R.id.editText);
    }

    public void buttonPublisher(View view) {
        mqtt.startPublisher(editText.getText().toString(), TEXT);
        editText.setText("");
    }

    public void imgPublisher(View view) {
        Intent imgIntent = new Intent();
        imgIntent.setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(imgIntent, INTENT_GET_IMAGE);
    }

    // activity for result

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case INTENT_GET_IMAGE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        Uri uri = data.getData();
                        ContentResolver cr = this.getContentResolver();
                        try {
                            Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                            byte[] imageByte = byteArrayOutputStream.toByteArray();
                            String imgEncode = Base64.encodeToString(imageByte, Base64.DEFAULT);
                            mqtt.startPublisher(imgEncode, PHOTO);
                            Log.d(TAG, "photo encode :" + imgEncode);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
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
