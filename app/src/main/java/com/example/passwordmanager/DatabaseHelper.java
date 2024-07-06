package com.example.passwordmanager;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "PasswordManagerDB";

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String TABLE_PASSWORDS = "password_entries";
    private static final String PASSWORD_ID = "id";
    private static final String PASSWORD_USERNAME = "username";
    private static final String PASSWORD_PASSWORD = "password";
    private static final String PASSWORD_WEBSITE_URL = "websiteUrl";

    private static final String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NAME + " TEXT,"
            + COLUMN_EMAIL + " TEXT,"
            + COLUMN_PASSWORD + " TEXT"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Add a new user
    public long addUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    // Check if a user exists
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_EMAIL + " = ?" + " AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs,
                null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count > 0;
    }

    // Check if a user exists by email
    public boolean checkUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs,
                null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count > 0;
    }
    public long addPasswordEntry(PasswordEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = -1; // Initialize id to -1 to indicate insertion failure

        try {
            ContentValues values = new ContentValues();
            values.put("username", entry.getUsername());
            values.put("password", entry.getPassword());
            values.put("websiteUrl", entry.getWebsiteUrl());

            // Insert row
            id = db.insert("password_entries", null, values);
            Log.d("DatabaseHelper", "Inserted new password entry with ID: " + id);
        } catch (SQLiteException e) {
            // Log the error or handle it appropriately
            Log.e("DatabaseHelper", "Error inserting password entry: " + e.getMessage());
        } finally {
            db.close(); // Close database connection
        }

        return id; // Return the inserted row ID or -1 if insertion failed
    }


    // Get all password entries

    @SuppressLint("Range")
    public List<PasswordEntry> getAllPasswordEntries() {
        List<PasswordEntry> passwordEntries = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_PASSWORDS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(selectQuery, null);

            // Loop through all rows and add to list
            while (cursor.moveToNext()) {
                PasswordEntry passwordEntry = new PasswordEntry();
                passwordEntry.setId(cursor.getLong(cursor.getColumnIndex(PASSWORD_ID)));
                passwordEntry.setUsername(cursor.getString(cursor.getColumnIndex(PASSWORD_USERNAME)));
                passwordEntry.setPassword(cursor.getString(cursor.getColumnIndex(PASSWORD_PASSWORD)));
                passwordEntry.setWebsiteUrl(cursor.getString(cursor.getColumnIndex(PASSWORD_WEBSITE_URL)));

                passwordEntries.add(passwordEntry);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error fetching password entries: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return passwordEntries;
    }


    // Updating single password entry
    public int updatePasswordEntry(long id, String newUsername, String newPassword, String newWebsiteUrl) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("username", newUsername);
        values.put("password", newPassword);
        values.put("websiteUrl", newWebsiteUrl);

        // Updating row
        return db.update("password_entries", values, "id" + " = ?",
                new String[]{String.valueOf(id)});
    }
    public void deletePasswordEntry(PasswordEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PASSWORDS, PASSWORD_ID + " = ?",
                new String[]{String.valueOf(entry.getId())});
        db.close();
    }

    public void softDeletePasswordEntry(PasswordEntry entry) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("deleted", 1);
        values.put("deletion_timestamp", System.currentTimeMillis());
        db.update("password_entries", values, "id=?", new String[]{String.valueOf(entry.getId())});
        db.close();
    }
    public void restorePasswordEntry(PasswordEntry entry) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("deleted", 0);
        values.put("deletion_timestamp", 0);
        db.update("password_entries", values, "id=?", new String[]{String.valueOf(entry.getId())});
        db.close();
    }

}

