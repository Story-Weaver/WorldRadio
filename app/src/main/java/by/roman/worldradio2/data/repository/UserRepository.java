package by.roman.worldradio2.data.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import by.roman.worldradio2.data.dto.FavoriteDTO;
import by.roman.worldradio2.data.dto.UserDTO;
import lombok.NonNull;

public class UserRepository {
    private SQLiteDatabase db;

    public UserRepository(SQLiteDatabase db) {
        this.db = db;
    }
    public long addUser(UserDTO dto) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_UUID_USER, dto.getId());
        values.put(DatabaseHelper.COLUMN_LOGIN_USER, dto.getLogin());
        values.put(DatabaseHelper.COLUMN_PASSWORD_USER,dto.getPassword());
        values.put(DatabaseHelper.COLUMN_IN_SYSTEM_USER, dto.getInSystem());
        return db.insert(DatabaseHelper.TABLE_USER, null, values);
    }
    public void setUserInSystem(int id, boolean inSystem){
        String checkQuery = "SELECT " + DatabaseHelper.COLUMN_UUID_USER + " FROM " + DatabaseHelper.TABLE_USER +
                " WHERE " + DatabaseHelper.COLUMN_IN_SYSTEM_USER + " = 1";
        Cursor cursor = db.rawQuery(checkQuery, null);
        if (cursor.moveToFirst()) {
            ContentValues resetValues = new ContentValues();
            resetValues.put(DatabaseHelper.COLUMN_IN_SYSTEM_USER, 0);
            db.update(DatabaseHelper.TABLE_USER, resetValues, null, null);
        }
        cursor.close();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_IN_SYSTEM_USER, inSystem ? 1 : 0);
        String selection = DatabaseHelper.COLUMN_UUID_USER + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        db.update(DatabaseHelper.TABLE_USER, values, selection, selectionArgs);
    }
    public void removeUserFromSystem(){
        String checkQuery = "SELECT * FROM " + DatabaseHelper.TABLE_USER +
                " WHERE " + DatabaseHelper.COLUMN_IN_SYSTEM_USER + " = 1";
        Cursor cursor = db.rawQuery(checkQuery, null);
        if (cursor.moveToFirst()) {
            ContentValues resetValues = new ContentValues();
            resetValues.put(DatabaseHelper.COLUMN_IN_SYSTEM_USER, 0);
            db.update(DatabaseHelper.TABLE_USER, resetValues, null, null);
        }
        cursor.close();
    }
    public int getUserInSystem(){
        int id = 0;
        return id;
    }
    public boolean entranceUser(String login, String password) {
        String query = "SELECT " + DatabaseHelper.COLUMN_UUID_USER + " FROM " + DatabaseHelper.TABLE_USER + " WHERE " +
                DatabaseHelper.COLUMN_LOGIN_USER + " = ? AND " + DatabaseHelper.COLUMN_PASSWORD_USER + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{login, password});
        int userId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getInt(0);
        }
        if(userId != -1){
            setUserInSystem(userId,true);
        }
        cursor.close();
        return userId != -1;
    }
    public boolean checkUserData(String login) {
        String query = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_USER +
                " WHERE " + DatabaseHelper.COLUMN_LOGIN_USER + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{login});

        boolean exists = false;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                exists = cursor.getInt(0) > 0;
            }
            cursor.close();
        }

        return exists;
    }
    public int removeUser(int id) {
        String selection = DatabaseHelper.COLUMN_UUID_USER + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        return db.delete(DatabaseHelper.TABLE_USER, selection, selectionArgs);
    }
}
