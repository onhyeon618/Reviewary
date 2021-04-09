package com.cookandroid.reviewary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BookDataBase {

    private static final String TAG = "BookDatabase";

    /**
     * 싱글톤 인스턴스
     */
    private static BookDataBase database;

    /**
     * table name for MEMO
     */
    public static String TABLE_NOTE = "BOOK";

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
    private BookDataBase(Context context) {
        this.context = context;
    }

    /**
     * 인스턴스 가져오기
     */
    public static BookDataBase getInstance(Context context) {
        if (database == null) {
            database = new BookDataBase(context);
        }

        return database;
    }

    /**
     * 데이터베이스 열기
     */
    public boolean open() {
        println("opening database [" + AppConstants.BOOKDATABASE_NAME + "].");

        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();

        return true;
    }

    /**
     * 데이터베이스 닫기
     */
    public void close() {
        println("closing database [" + AppConstants.BOOKDATABASE_NAME + "].");
        db.close();

        database = null;
    }

    /**
     * 데이터베이스 삭제
     */
    public boolean delete() {
        println("deleting database [" + AppConstants.BOOKDATABASE_NAME + "].");

        context.deleteDatabase(AppConstants.BOOKDATABASE_NAME);

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
            super(context, AppConstants.BOOKDATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            println("creating database [" + AppConstants.BOOKDATABASE_NAME + "].");

            // TABLE_NOTE
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
                    + "  IMAGE TEXT DEFAULT '', "
                    + "  NAME TEXT DEFAULT '', "
                    + "  AUTHOR TEXT DEFAULT '', "
                    + "  PUBLISHER TEXT DEFAULT '', "
                    + "  GENRE TEXT DEFAULT '', "
                    + "  RATING INTEGER DEFAULT 0, "
                    + "  READ_DATE TEXT DEFAULT '', "
                    + "  IMPRESSIVE_SENTENCE TEXT DEFAULT '', "
                    + "  REVIEW TEXT DEFAULT '' "
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
            println("opened database [" + AppConstants.BOOKDATABASE_NAME + "].");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            println("Upgrading database from version " + oldVersion + " to " + newVersion + ".");
        }
    }

    private void println(String msg) {
        Log.d(TAG, msg);
    }

}
