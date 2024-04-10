package com.sd.spartan.easyhealth.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.sd.spartan.easyhealth.AccessControl.AppConstants.*;


public class DatabaseHelperTest extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "EASYHEALTHFYD.DB";
    private static final String DIAGNOSTIC_CENTER_TBL = "diagonostic_center_tbl";
    private static final String DOCTOR_PROFILE_TBL = "doctor_profile_tbl";
    private static final String DIAG_NOTIFICATION_TBL = "diag_notification";
    private static final String MESSAGE_EMAIL_TBL = "message_email";

    public DatabaseHelperTest(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE " + DIAGNOSTIC_CENTER_TBL + " (" +
                    "diag_cen_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "diag_id INTEGER);"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            db.execSQL("CREATE TABLE " + DOCTOR_PROFILE_TBL + " (" +
                    "doc_pro_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "doc_id INTEGER);"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            db.execSQL("CREATE TABLE " + DIAG_NOTIFICATION_TBL + " (" +
                    "diag_notify_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "notify_id VARCHAR," +
                    "notify_title VARCHAR," +
                    "notify_msg VARCHAR," +
                    "notify_read VARCHAR," +
                    "notify_client_id VARCHAR);"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            db.execSQL("CREATE TABLE " + MESSAGE_EMAIL_TBL + " (" +
                    "msg_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "msg_sub VARCHAR," +
                    "msg_body VARCHAR," +
                    "msg_client_id VARCHAR);"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DIAGNOSTIC_CENTER_TBL);
        db.execSQL("DROP TABLE IF EXISTS " + DOCTOR_PROFILE_TBL);
        db.execSQL("DROP TABLE IF EXISTS " + DIAG_NOTIFICATION_TBL);
        db.execSQL("DROP TABLE IF EXISTS " + MESSAGE_EMAIL_TBL);
        onCreate(db);
    }

    @Override
    public synchronized void close () {
        SQLiteDatabase db = this.getWritableDatabase();
        if (db != null) {
            db.close();
            super.close();
        }
    }

    public boolean InsertDiagTbl(String diag_id ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DIAG_ID, diag_id);

        long l = db.insert(DIAGNOSTIC_CENTER_TBL, null, contentValues);
        return l != -1;
    }
    public boolean InsertDocTbl(String doc_id ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DOC_ID, doc_id);

        long l = db.insert(DOCTOR_PROFILE_TBL, null, contentValues);

        return l != -1;
    }
    public void InsertNotificationTbl(String notify_id, String notify_title, String notify_msg, String notify_client_id ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTIFY_ID, notify_id);
        contentValues.put(NOTIFY_TITLE, notify_title);
        contentValues.put(NOTIFY_READ, 0);
        contentValues.put(NOTIFY_MSG, notify_msg);
        contentValues.put(NOTIFY_CLIENT_ID, notify_client_id);

        db.insert(DIAG_NOTIFICATION_TBL, null, contentValues);

    }



    public Cursor checkTable( String table, String client_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;

        switch (table) {
            case DIAG:
                res = db.rawQuery("select * from " + DIAGNOSTIC_CENTER_TBL, null);
                break;
            case DOC:
                res = db.rawQuery("select * from " + DOCTOR_PROFILE_TBL, null);
                break;
            case NOTIFY_ALL:
                res = db.rawQuery("select * from " + DIAG_NOTIFICATION_TBL + " where notify_client_id =\"" + client_id + "\"", null);
                break;
            case NOTIFY_MAIN:
                res = db.rawQuery("select * from " + DIAG_NOTIFICATION_TBL + " where notify_read != 2 and notify_client_id =\"" + client_id + "\"Order by notify_read DESC", null);
                break;
            case MSG_DIAG:
            case MSG_USER:
                res = db.rawQuery("select * from " + MESSAGE_EMAIL_TBL + " WHERE msg_client_id =\"" + client_id + "\" ORDER BY msg_id DESC   ", null);
                break;
            case NOTIFY_COUNT:
                res = db.rawQuery("select count(*) from " + DIAG_NOTIFICATION_TBL + " where notify_read == 0 and notify_client_id =\"" + client_id + "\"", null);
                break;
        }
        return res;
    }

    public boolean DeleteDiagnosticTbl(String diag_id) {
        SQLiteDatabase db1 = this.getWritableDatabase();
        long l = db1.delete(DIAGNOSTIC_CENTER_TBL, DIAG_ID+"=? ", new String[]{diag_id });
        return l != -1;
    }
    public boolean DeleteDoctorTbl(String doc_id) {
        SQLiteDatabase db1 = this.getWritableDatabase();
        long l = db1.delete(DOCTOR_PROFILE_TBL, DOC_ID+"=? ", new String[]{doc_id });
        return l != -1;
    }
    public void DeleteFavouriteTbl() {
        SQLiteDatabase db1 = this.getWritableDatabase();
        db1.delete(DOCTOR_PROFILE_TBL, "", new String[]{});
        db1.delete(DIAGNOSTIC_CENTER_TBL, "", new String[]{});

    }
    public boolean DeleteNotificationTbl(String notify_id, String notify_read, String notify_client_id) {
        SQLiteDatabase db1 = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTIFY_READ, notify_read);
        long l = db1.update(DIAG_NOTIFICATION_TBL, contentValues, "notify_id=? and notify_client_id=?", new String[]{ notify_id, notify_client_id });
        return l != -1;
    }

    public void NotificationUpdate(String notify_id, String notify_read, String notify_client_id) {
        SQLiteDatabase db1 = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTIFY_READ, notify_read);
        db1.update(DIAG_NOTIFICATION_TBL, contentValues, "notify_id=? and notify_client_id=?", new String[]{ notify_id, notify_client_id });
    }
}