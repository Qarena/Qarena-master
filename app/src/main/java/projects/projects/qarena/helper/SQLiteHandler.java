/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 */
package projects.projects.qarena.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "qarena";

    // Login table name
    private static final String TABLE_USER = "login_master";

    // Login Table Columns names
    private static final String KEY_ID = "user_id";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_FIRSTNAME = "firstname";
    private static final String KEY_LASTNAME = "lastname";
    private static final String KEY_DOB = "dob";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_STATE = "state";
    private static final String KEY_CITY = "city";


    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " TEXT PRIMARY KEY," + KEY_EMAIL + " TEXT UNIQUE,"
                + KEY_PASSWORD + " TEXT,"+ KEY_FIRSTNAME + " TEXT," + KEY_LASTNAME + " TEXT," +
                KEY_DOB + " TEXT," + KEY_COUNTRY + " TEXT," + KEY_STATE + " TEXT,"+ KEY_CITY + " TEXT )";
        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     */
    public void addUser(String uid, String email, String password,String firstname, String lastname,
                        String dob,String country,String state,String city) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, uid); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_PASSWORD, password); // Password
        values.put(KEY_FIRSTNAME,firstname);//firstname
        values.put(KEY_LASTNAME, lastname);//lastname
        values.put(KEY_DOB,dob);//date of birth
        values.put(KEY_COUNTRY,country);//country
        values.put(KEY_STATE,state);//state
        values.put(KEY_CITY,city);//city
        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("user_id", cursor.getString(cursor.getColumnIndex("user_id")));
            user.put("email", cursor.getString(cursor.getColumnIndex("email")));
            user.put("password", cursor.getString(cursor.getColumnIndex("password")));
            user.put("firstname",cursor.getString(cursor.getColumnIndex("firstname")));
            user.put("lastname",cursor.getString(cursor.getColumnIndex("lastname")));
            user.put("dob",cursor.getString(cursor.getColumnIndex("dob")));
            user.put("country",cursor.getString(cursor.getColumnIndex("country")));
            user.put("state",cursor.getString(cursor.getColumnIndex("state")));
            user.put("city",cursor.getString(cursor.getColumnIndex("city")));


            //System.out.println( cursor.getString(3));

        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Re crate database Delete all tables and create them again
     */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

}
