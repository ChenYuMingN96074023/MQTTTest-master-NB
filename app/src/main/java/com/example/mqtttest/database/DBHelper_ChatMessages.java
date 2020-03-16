package com.example.mqtttest.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.mqtttest.recyclerChatRoom.MQTTBean;

import java.util.ArrayList;

public class DBHelper_ChatMessages extends SQLiteOpenHelper {

    private String DB_NAME = "chatMessages.db";
    private String tableName ;
    private final String TAG = DBHelper_ChatMessages.class.getSimpleName();

    public DBHelper_ChatMessages(Context context,String DBName, String tableName,SQLiteDatabase.CursorFactory factory, int version) {
//        super(context, name, factory, version);
        super(context , "chatMessages.db", null, 1);
        this.tableName = tableName;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        final String sql = "DROP TABLE " + tableName;
//        db.execSQL(sql);
    }

    public void createTable(String tableName)//建立資料表
    {
        SQLiteDatabase db = getWritableDatabase();
        String crT_CRItem = "CREATE TABLE IF NOT EXISTS " + tableName +"( " +
                "message VARCHAR ," +
                "clientID VARCHAR(50),"+
                "time VARCHAR," + //////這裡的50只是暫時亂設定的
                "type INTEGER);";
        db.execSQL(crT_CRItem);
        Log.d(TAG,"created table:" + tableName);
        db.close();
    }

    public long addRec(MQTTBean bean, String tableName)//增加資料
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues rec = new ContentValues();
        rec.put("message",bean.getMessage());
        rec.put("clientID",bean.getClientID());
        rec.put("Time", bean.getTime());
        rec.put("type",bean.getType());
        long rowID = db.insert(tableName, null,rec);
        db.close();
        return rowID;
    }

    public ArrayList<MQTTBean> getRecSet(String tableName)//讀取database，存進ArrayList
    {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT*FROM " + tableName;
        Cursor recSet = db.rawQuery(sql,null);
        ArrayList<MQTTBean> recAry = new ArrayList<MQTTBean>();
        int columnCount = recSet.getColumnCount();
        while (recSet.moveToNext()){
            recAry.add(new MQTTBean(recSet.getString(0), recSet.getString(1), recSet.getString(2), recSet.getInt(3)));//////這邊先寫死
        }
        recSet.close();
        db.close();
        return recAry;
    }

    public void deleteTable(String tableName) {//刪除一個table
        SQLiteDatabase db = getReadableDatabase();
        String sql = "DELETE FROM " + tableName + ";";
        db.execSQL(sql);
        db.close();
    }
}
