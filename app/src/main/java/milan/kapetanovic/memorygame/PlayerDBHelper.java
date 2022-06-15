package milan.kapetanovic.memorygame;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class PlayerDBHelper extends SQLiteOpenHelper{
    SQLiteDatabase db;
    private int id = 0;

    private static final String DATABASE_NAME = "memory_game.db";
    private static final Integer DATABASE_VERSION = 1;
    private final String TABLE_NAME = "GAMES";
    public static final String COLUMN_GAME_ID = "ID";
    public static final String COLUMN_USERNAME = "USERNAME";
    public static final String COLUMN_EMAIL = "EMAIL";
    public static final String COLUMN_POINTS = "POINTS";

    //String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_GAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_USERNAME + " TEXT " + COLUMN_EMAIL + " TEXT " + COLUMN_POINTS +" INTEGER);";

    public PlayerDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + " (" + COLUMN_GAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_USERNAME + " TEXT ," + COLUMN_EMAIL + " TEXT ," + COLUMN_POINTS +" INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insertPlayer(String username, String email, int points) {
        Log.d("Test", "Uslo u insert");
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("Test", "Uzelo bazu");
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_POINTS, points);

        db.insert(TABLE_NAME, null, values);
        Log.d("Test", "Odradilo insert");
    }

    public void delete(String user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_USERNAME + " =?", new String[] {user});
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
    }

    public int playerExists(String username)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_USERNAME + " =?", new String[] {username}, null, null, COLUMN_POINTS + " DESC");

        if(cursor.getCount() <= 0)
        {
            cursor.close();
            return 0;
        }
        else
        {
            cursor.close();
            return 1;
        }

    }

    public Integer[] readResultForPlayer(String username)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_USERNAME + " =?", new String[] {username}, null, null, COLUMN_POINTS + " DESC");

        if(cursor.getCount() < 0)
        {
            return null;
        }

        Integer[] rezultati = new Integer[cursor.getCount()];

        int i = 0;

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            rezultati[i++] = getResults(cursor);
        }
        cursor.close();
        return rezultati;
    }

    public ArrayList<String> readAllPlayers()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true,TABLE_NAME,null,null,null,COLUMN_USERNAME,null,null,null);
        ArrayList<String> listaUsera = new ArrayList<String>();

        if(cursor.getCount() < 0){
            return null;
        }
        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            listaUsera.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
        }
        cursor.close();
        return listaUsera;
    }


    private Integer getResults(Cursor cursor)
    {
        Integer result = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_POINTS));

        return result;
    }
}
