package com.zybooks.weighttracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {

    // Define class fields
    private static final String DB = "weightapp.db";
    private static final int VER = 1;

    // Constructor
    public DbHelper(Context c){ super(c, DB, null, VER); }

    // Create our tables during initialization
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT)");
        db.execSQL("CREATE TABLE goals(id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, goal REAL)");
        db.execSQL("CREATE TABLE weights(id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, date TEXT, value REAL)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db,int o,int n){}

    public boolean validateUser(String u, String p){
        try (Cursor c = getReadableDatabase().rawQuery(
                "SELECT id FROM users WHERE username=? AND password=?", new String[]{u,p})) {
            return c.moveToFirst();
        }
    }
    public boolean createUserIfNotExists(String u, String p){
        try (Cursor c = getReadableDatabase().rawQuery(
                "SELECT id FROM users WHERE username=?", new String[]{u})) {
            if (c.moveToFirst()) return false;
        }
        ContentValues cv = new ContentValues();
        cv.put("username", u); cv.put("password", p);
        return getWritableDatabase().insert("users", null, cv) > 0;
    }

    public long insertWeight(long userId, String date, double val){
        ContentValues cv = new ContentValues();
        cv.put("user_id", userId); cv.put("date", date); cv.put("value", val);
        return getWritableDatabase().insert("weights", null, cv);
    }
    public void deleteWeight(long id){
        getWritableDatabase().delete("weights", "id=?", new String[]{String.valueOf(id)});
    }
    public List<DataGridActivity.WeightRow> getWeights(long userId){
        ArrayList<DataGridActivity.WeightRow> out = new ArrayList<>();
        try (Cursor c = getReadableDatabase().rawQuery(
                "SELECT id, date, value FROM weights WHERE user_id=? ORDER BY date DESC",
                new String[]{String.valueOf(userId)})) {
            while (c.moveToNext()){
                out.add(new DataGridActivity.WeightRow(
                        c.getLong(0), c.getString(1), c.getDouble(2)));
            }
        }
        return out;
    }
    public Double getGoal(long userId){
        try (Cursor c = getReadableDatabase().rawQuery(
                "SELECT goal FROM goals WHERE user_id=? LIMIT 1",
                new String[]{String.valueOf(userId)})) {
            if (c.moveToFirst()) return c.getDouble(0);
            return null;
        }
    }

    public int updateWeight(long id, String newDate, double newValue) {
        ContentValues cv = new ContentValues();
        cv.put("date", newDate);
        cv.put("value", newValue);
        return getWritableDatabase().update("weights", cv, "id=?", new String[]{String.valueOf(id)});
    }

    public int updateGoal(long userId, double goal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("goal", goal);
        return db.update("users", values, "id=?", new String[]{String.valueOf(userId)});
    }
}