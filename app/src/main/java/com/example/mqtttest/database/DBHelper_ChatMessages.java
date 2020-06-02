package com.example.mqtttest.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;
import android.util.Log;

import com.example.mqtttest.recyclerChatRoom.MQTTBean;
import com.example.mqtttest.recyclerPhoto.PhotoBean;

import java.util.ArrayList;

public class DBHelper_ChatMessages extends SQLiteOpenHelper {

    public static String DBName_Messages_Group = "chatMessages_Grp.db";
    public static String DBName_Messages_Individual = "chatMessages_Ind.db";
    private String tableName ;
    private final String TAG = DBHelper_ChatMessages.class.getSimpleName();

    public DBHelper_ChatMessages(Context context,String DBName, int IndOrGrp, String tableName,SQLiteDatabase.CursorFactory factory, int version) {
//        super(context, name, factory, version);
        super(context , (IndOrGrp==1?DBName_Messages_Group:DBName_Messages_Individual), null, 1);//依據變數individualOrGroup決定要開哪個.db檔
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
                "clientID VARCHAR(50),"+ //規定clientID最多50字元
                "time VARCHAR," +
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
            recAry.add(new MQTTBean(recSet.getString(0), recSet.getString(1), recSet.getString(2), recSet.getInt(3)));
        }
        recSet.close();
        db.close();
        return recAry;
    }

    public ArrayList<PhotoBean> getPhotoRecSet(String tableName)//只讀取訊息中的圖片部分，存進PhotoBean形式的ArrayList
    {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT*FROM "+tableName+" WHERE type = ?";
        String[] args = {"2"};
        Cursor recSet = db.rawQuery(sql, args);
        ArrayList<PhotoBean> recAry = new ArrayList<PhotoBean>();
        int columnCount = recSet.getColumnCount();
        while (recSet.moveToNext()){
            byte[] decodeByte = Base64.decode(recSet.getString(0).getBytes(), Base64.DEFAULT);
            recAry.add(new PhotoBean(decodeByte, recSet.getString(1), recSet.getPosition()));
        }
        recSet.close();
        db.close();
        return recAry;
    }

    public int getPhotoIndexByTime(String tableName, String msgTime){ //藉由發送時間辨別圖片，並回傳該圖片的index值
        ///////////////////////////////////////////////////////////////此方法若同一秒內有多張圖片將產生錯誤
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT time FROM "+tableName+" WHERE type = ?";
        String[] args = {"2"}; //2:photo
        Cursor recSet = db.rawQuery(sql, args);
        while (recSet.moveToNext()){
//            Log.d(TAG, "time = "+recSet.getString(0));
            if (recSet.getString(0).equals(msgTime)){
                int index = recSet.getPosition();
                Log.d(TAG, "index值為"+index);
                return index;
            }
        }
        //以下為default，程式不該往下執行
        Log.d(TAG,"getPhotoIndexByTime片段的程式錯誤");
        return -1;
    }

    public void deleteTable(String tableName) {//刪除一個table
        SQLiteDatabase db = getReadableDatabase();
        String sql = "DELETE FROM " + tableName + ";";
        db.execSQL(sql);
        db.close();
    }
}
