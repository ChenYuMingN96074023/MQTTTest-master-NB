package com.example.mqtttest.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.mqtttest.recyclerChatRoomList.CRListBean;

import java.util.ArrayList;

public class DBHelper_CRL extends SQLiteOpenHelper {
    private final String TAG = DBHelper_CRL.class.getSimpleName();
    private String DB_NAME = "myDatabase.db";
//    private int DB_VERSION = 1;
    private String _TableName = "CRL_Item_Table";
    private String crT_CRItem = "CREATE TABLE IF NOT EXISTS " + _TableName +"( " +
                                        "topic VARCHAR(50)not null," + //////這裡的50 20只是暫時亂設定的
                                        "time VARCHAR(20),"+
                                        "message VARCHAR,"+
                                        "img_id INTEGER,"+
                                        "PRIMARY KEY (topic));";
    public DBHelper_CRL(Context context, String DB_NAME, SQLiteDatabase.CursorFactory factory, int DB_VERSION){
//        super(context,DB_NAME,factory,DB_VERSION);
        super(context,"myDatabase.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(crT_CRItem);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String sql = "DROP TABLE " + _TableName;
        db.execSQL(sql);
    }

    public long addRec(CRListBean bean)//增加資料
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues rec = new ContentValues();
        rec.put("topic",bean.getTopic());
        rec.put("time",bean.getTime());
        rec.put("message",bean.getMessage());
        rec.put("img_id",bean.getImg_id());
        long rowID = db.insert(_TableName, null,rec);
        db.close();
        return rowID;
    }

    public ArrayList<CRListBean> getRecSet()//讀取database，存進ArrayList
    {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT*FROM " + _TableName;
        Cursor recSet = db.rawQuery(sql,null);
        ArrayList<CRListBean> recAry = new ArrayList<CRListBean>();
        int columnCount = recSet.getColumnCount();
        while (recSet.moveToNext()){
            recAry.add(new CRListBean(recSet.getString(0), recSet.getString(1), recSet.getString(2), recSet.getInt(3)));//////這邊先寫死
        }
        recSet.close();
        db.close();
        return recAry;
    }

    public int RecCount()//傳回table所存的資料的筆數
    {
        SQLiteDatabase db = getWritableDatabase();
        String SQL = "SELECT*FROM " + _TableName;
        Cursor recSet = db.rawQuery(SQL,null);
        return recSet.getCount();
    }

    public int deleteRec(String topic)//刪除一筆資料，回傳刪除的記錄數
    {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT*FROM " + _TableName;
        Cursor recSet = db.rawQuery(sql,null);
        if(recSet.getCount() != 0){
            String whereClause = "topic='"+ topic + "'";
            int rowsAffect = db.delete(_TableName, whereClause, null);
            db.close();
            return rowsAffect;
        }
        else {
            db.close();
            return -1;
        }
    }

    public void deleteTable()//直接刪除整個table
    {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "DELETE FROM " + _TableName +";";
        db.execSQL(sql);
        db.close();
    }

//    public int searchTopic(String topic)//搜尋資料庫裡面是否有該topic的資料，有的話return所在的欄位數
//    {
//        SQLiteDatabase db = getReadableDatabase();
//        String sql = "SELECT*FROM " + _TableName + " WHERE topic LIKE ?";
//        String[] args = {"%" + topic + "%"};
//        Cursor recSet = db.rawQuery(sql, args);
//        int columnCount = recSet.getColumnCount();
//        return columnCount;
//    }

    public void refreshMessage(String topic, String latestMessage)//更新聊天室列表物件的訊息/////////之後要再做時間
    {
        SQLiteDatabase db = getWritableDatabase();
        try {
//            String sql = "UPDATE "+_TableName+" SET message = '"+latestMessage+"' WHERE topic = "+topic+";";
//            db.execSQL(sql);
            ContentValues values = new ContentValues();
            values.put("topic", topic);
            values.put("message", latestMessage);
            values.put("time", "12:34");
            values.put("img_id", 1);
            db.update(_TableName, values, "topic ='"+topic+"'",null);
            Log.d(TAG, "refresh message on CRL item success");
        }
        catch (Exception e){
            Log.d(TAG, "refresh message fail");
        }
            db.close();
    }

}
