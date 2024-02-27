package com.harmonyhub;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Meal history table
    public static final String TABLE_MEAL = "meal_history";
    public static final String COLUMN_MEAL_ID = "meal_id";
    public static final String COLUMN_MEAL_NAME = "meal_name";
    public static final String COLUMN_MEAL_CALORIE = "meal_calorie_intake";
    public static final String COLUMN_MEAL_DATE = "meal_date";
    // Hydration history table
    public static final String TABLE_WATER = "water_history";
    public static final String COLUMN_WATER_ID = "water_id";
    public static final String COLUMN_WATER_CUPS = "water_cups";
    public static final String COLUMN_WATER_DATE = "water_date";

    // Mood history table
    public static final String TABLE_MOOD = "mood_history";
    public static final String COLUMN_MOOD_ID = "mood_id";
    public static final String COLUMN_MOOD_TYPE = "mood";
    public static final String COLUMN_MOOD_DATE = "day_of_week";
    // Medication history table
    public static final String TABLE_MEDICATION = "medication";
    public static final String COLUMN_MEDICATION_ID = "_id";
    public static final String COLUMN_MEDICATION_TYPE = "type";
    public static final String COLUMN_MEDICATION_QTY = "quantity";
    public static final String COLUMN_MEDICATION_DATE = "date";
    public static final String COLUMN_MEDICATION_TIME = "time";

    // meal, water, mood, medication
    private static final String DATABASE_NAME = "HarmonyHub.db";
    private static final int DATABASE_VERSION = 9;
    // Create meal history table query
    private static final String CREATE_TABLE_MEAL =
            "CREATE TABLE " + TABLE_MEAL + "(" +
                    COLUMN_MEAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_MEAL_NAME + " TEXT," +
                    COLUMN_MEAL_CALORIE + " INTEGER," +
                    COLUMN_MEAL_DATE + " TEXT" +
                    ")";

    // Create water history table query
    private static final String CREATE_TABLE_WATER =
            "CREATE TABLE " + TABLE_WATER + "(" +
                    COLUMN_WATER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_WATER_CUPS + " INTEGER," +
                    COLUMN_WATER_DATE + " TEXT" +
                    ")";

    // Create mood history table query
    private static final String CREATE_TABLE_MOOD =
            "CREATE TABLE " + TABLE_MOOD + "(" +
                    COLUMN_MOOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_MOOD_TYPE + " TEXT," +
                    COLUMN_MOOD_DATE + " INTEGER" +
                    ")";

    // Create medication history table query
    private static final String CREATE_TABLE_MEDICATION = "CREATE TABLE " +
            TABLE_MEDICATION + "(" +
            COLUMN_MEDICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_MEDICATION_TYPE + " TEXT, " +
            COLUMN_MEDICATION_QTY + " INTEGER, " +
            COLUMN_MEDICATION_DATE + " INTEGER, " +
            COLUMN_MEDICATION_TIME + " INTEGER)";

    // Constructor
    public DatabaseHelper(Context context) {
        super( context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create all four tables when the database is created
        db.execSQL( CREATE_TABLE_MEAL );
        db.execSQL( CREATE_TABLE_WATER );
        db.execSQL( CREATE_TABLE_MOOD );
        db.execSQL( CREATE_TABLE_MEDICATION );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Upgrade policy, currently it drop and recreate all four tables
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_MEAL );
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_WATER );
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_MOOD );
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_MEDICATION );
        onCreate( db );
    }

    //SETTER WATER
    // INSERT WATER: Add this method to insert water consumption details
    public long insertHydration(int cups, int dayOfWeek) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_WATER_CUPS, cups);
        values.put(COLUMN_WATER_DATE, dayOfWeek);
        long newRowId = db.insert(TABLE_WATER, null, values);
        db.close();
        return newRowId;
    }

    //GETTER WATER
    public String getHydrationHistory(int selectedDay) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {COLUMN_WATER_CUPS};
        String selection = COLUMN_WATER_DATE + " = ?";
        String[] selectionArgs = {String.valueOf(selectedDay)};
        Cursor cursor = db.query(TABLE_WATER, projection, selection, selectionArgs, null, null, null);
        int totalCups = 0;
        while (cursor.moveToNext()) {
            int cups = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WATER_CUPS));
            totalCups += cups;
        }
        cursor.close();
        db.close();

        // Append "cups" to the totalCups integer value and return as a String
        return String.valueOf(totalCups) + " cups";
    }



    //SETTER MOOD
    // Insert mood into the database
    public long insertMood(String moodType, int dayOfWeek) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MOOD_TYPE, moodType);
        values.put(COLUMN_MOOD_DATE, dayOfWeek);
        long newRowId = db.insert(TABLE_MOOD, null, values);
        db.close();
        return newRowId;
    }

    //GETTER MOOD
    // Get mood history for a specific day of the week
    public String getMood(int dayOfWeek) {
        SQLiteDatabase db = this.getReadableDatabase();
        String moodHistory = "";
        String[] projection = {COLUMN_MOOD_TYPE};
        String selection = COLUMN_MOOD_DATE+ " = ?";
        String[] selectionArgs = {String.valueOf(dayOfWeek)};
        Cursor cursor = db.query(TABLE_MOOD, projection, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String moodType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOOD_TYPE));
                moodHistory += moodType + "\n";
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return moodHistory;
    }

    //SETTER MEDICATIONS
    // INSERT MEDS: Insert medication history for a specific day
    public long insertMedication(String type, int quantity, int dayOfWeek, int hourOfDay, int minute) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MEDICATION_TYPE, type);
        values.put(COLUMN_MEDICATION_QTY, quantity);
        values.put(COLUMN_MEDICATION_DATE, dayOfWeek);
        values.put(COLUMN_MEDICATION_TIME, convertTimeToMillis(hourOfDay, minute));
        long result = db.insert(TABLE_MEDICATION, null, values);
        db.close();
        return result;
    }

    //GETTER MEDICATIONS
    public String getMedicationHistory(int dayOfWeek) {
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder medicationHistory = new StringBuilder();
        String[] columns = {COLUMN_MEDICATION_TYPE, COLUMN_MEDICATION_QTY};
        String selection = COLUMN_MEDICATION_DATE + " = ?";
        String[] selectionArgs = {String.valueOf(dayOfWeek)};
        Cursor cursor = db.query(TABLE_MEDICATION, columns, selection, selectionArgs, null, null, null);
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String type = cursor.getString(cursor.getColumnIndex(COLUMN_MEDICATION_TYPE));
            @SuppressLint("Range") int quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_MEDICATION_QTY));
            medicationHistory.append(type)
                    .append(" - ")
                    .append(quantity + " total")
                    .append("\n");
        }
        cursor.close();
        db.close();
        return medicationHistory.toString();
    }

    private long convertTimeToMillis(int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    //SETTER MEALS
    public long insertMeal(String mealName, int calorieIntake, int selectedDay) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MEAL_NAME, mealName);
        values.put(COLUMN_MEAL_CALORIE, calorieIntake);
        values.put(COLUMN_MEAL_DATE, selectedDay);
        long newRowId = db.insert(TABLE_MEAL, null, values);
        db.close();
        return newRowId;
    }

    //GETTER MEALS
    public String getMealHistory(int selectedDay) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {COLUMN_MEAL_NAME, COLUMN_MEAL_CALORIE, COLUMN_MEAL_DATE};
        String selection = COLUMN_MEAL_DATE + " = ?";
        String[] selectionArgs = {String.valueOf(selectedDay)};
        Cursor cursor = db.query(TABLE_MEAL, projection, selection, selectionArgs, null, null, null);
        StringBuilder mealHistory = new StringBuilder();
        while (cursor.moveToNext()) {
            String mealName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEAL_NAME));
            int calorieIntake = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MEAL_CALORIE));
            mealHistory.append("Meal: ").append(mealName)
                    .append(", Calories: ").append(calorieIntake)
                    .append("\n");
        }
        cursor.close();
        db.close();
        return mealHistory.toString();
    }



    public void deleteHistoryEntry(int selectedDay, String tableName, String dateColumnName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = dateColumnName + " = ?";
        String[] whereArgs = {String.valueOf(selectedDay)};

        db.delete(tableName, whereClause, whereArgs);
        db.close();
    }




}
