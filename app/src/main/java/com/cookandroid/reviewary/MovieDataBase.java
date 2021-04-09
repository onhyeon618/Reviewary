package com.cookandroid.reviewary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MovieDataBase {

    private static final String TAG = "MovieDatabase";

    /**
     * 싱글톤 인스턴스
     */
    private static MovieDataBase database;

    /**
     * table name for BOOK
     */
    public static String TABLE_NOTE = "MOVIE";

    /**
     * version
     */
    public static int DATABASE_VERSION = 1;


    /**
     * Helper class defined
     */
    private DatabaseHelper dbHelper;

    /**
     * SQLiteDatabase 인스턴스
     */
    private SQLiteDatabase db;

    /**
     * 컨텍스트 객체
     */
    private Context context;

    /**
     * 생성자
     */
    private MovieDataBase(Context context) {
        this.context = context;
    }

    /**
     * 인스턴스 가져오기
     */
    public static MovieDataBase getInstance(Context context) {
        if (database == null) {
            database = new MovieDataBase(context);
        }

        return database;
    }

    /**
     * 데이터베이스 열기
     */
    public boolean open() {
        println("opening database [" + AppConstants.MOVIEDATABASE_NAME + "].");

        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();

        return true;
    }

    /**
     * 데이터베이스 닫기
     */
    public void close() {
        println("closing database [" + AppConstants.MOVIEDATABASE_NAME + "].");
        db.close();

        database = null;
    }

    /**
     * 데이터베이스 삭제
     */
    public boolean delete() {
        println("deleting database [" + AppConstants.MOVIEDATABASE_NAME + "].");

        context.deleteDatabase(AppConstants.MOVIEDATABASE_NAME);

        return true;
    }

    /**
     * execute raw query using the input SQL
     * close the cursor after fetching any result
     *
     * @param SQL
     * @return
     */
    public Cursor rawQuery(String SQL) {
        println("\nexecuteQuery called.\n");

        Cursor c1 = null;
        try {
            c1 = db.rawQuery(SQL, null);
            println("cursor count : " + c1.getCount());
        } catch(Exception ex) {
            Log.e(TAG, "Exception in executeQuery", ex);
        }

        return c1;
    }

    public boolean execSQL(String SQL) {
        println("\nexecute called.\n");

        try {
            Log.d(TAG, "SQL : " + SQL);
            db.execSQL(SQL);
        } catch(Exception ex) {
            Log.e(TAG, "Exception in executeQuery", ex);
            return false;
        }

        return true;
    }



    /**
     * Database Helper inner class
     */
    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, AppConstants.MOVIEDATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            println("creating database [" + AppConstants.MOVIEDATABASE_NAME + "].");

            // TABLE_BOOK
            println("creating table [" + TABLE_NOTE + "].");

            // drop existing table
            String DROP_SQL = "drop table if exists " + TABLE_NOTE;
            try {
                db.execSQL(DROP_SQL);
            } catch(Exception ex) {
                Log.e(TAG, "Exception in DROP_SQL", ex);
            }

            // create table
            String CREATE_SQL = "create table " + TABLE_NOTE + "("
                    + "  _id INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + "  IMAGE STRING DEFAULT '', "
                    + "  NAME STRING DEFAULT '', "
                    + "  DIRECTOR STRING DEFAULT '', "
                    + "  ACTOR STRING DEFAULT '', "
                    + "  GENRE STRING DEFAULT '', "
                    + "  RATING DOUBLE DEFAULT 0, "
                    + "  DATE STRING DEFAULT '', "
                    + "  PLACE STRING DEFAULT '', "
                    + "  IMPRESSIVE_SENTENCE STRING DEFAULT '', "
                    + "  REVIEW STRING DEFAULT '' "
                    + ")";
            try {
                db.execSQL(CREATE_SQL);
            } catch(Exception ex) {
                Log.e(TAG, "Exception in CREATE_SQL", ex);
            }

            // create index
            String CREATE_INDEX_SQL = "create index " + TABLE_NOTE + "_IDX ON " + TABLE_NOTE + "("
                    + "CREATE_DATE"
                    + ")";
            try {
                db.execSQL(CREATE_INDEX_SQL);
            } catch(Exception ex) {
                Log.e(TAG, "Exception in CREATE_INDEX_SQL", ex);
            }
        }

        public void onOpen(SQLiteDatabase db) {
            println("opened database [" + AppConstants.MOVIEDATABASE_NAME + "].");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            println("Upgrading database from version " + oldVersion + " to " + newVersion + ".");
        }
    }

    private void println(String msg) {
        Log.d(TAG, msg);
    }
}
