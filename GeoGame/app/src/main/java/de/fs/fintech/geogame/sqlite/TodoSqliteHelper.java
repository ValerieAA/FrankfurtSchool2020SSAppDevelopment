package de.fs.fintech.geogame.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import androidx.annotation.Nullable;

import de.fs.fintech.geogame.data.Todo;


public class TodoSqliteHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = TodoSqliteHelper.class.getSimpleName();

    private static final String DB_FILE_NAME = "todo.db";
    private static final int DB_CURRENT_VERSION = 1;

    public static final String TODO_TABLE = "TODO";

    public interface TodoColumns extends BaseColumns {
        String BODY = "body";
        String PRIORITY = "priority";
    }

    public TodoSqliteHelper(@Nullable Context context) {
        super(context, DB_FILE_NAME, null, DB_CURRENT_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // Create to-do table
            final String sql = "CREATE TABLE IF NOT EXISTS " + TODO_TABLE + " (" +
                    TodoColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TodoColumns.BODY + " TEXT NOT NULL DEFAULT '', " +
                    TodoColumns.PRIORITY + " INTEGER " +
                    ");";
            db.execSQL(sql);
        } catch (SQLiteException e) {
            Log.e(LOG_TAG, "Failed to create table in " + DB_FILE_NAME, e);
        }

        try {
            // Pre-populate to-do table
            final String sql = "INSERT INTO " + TODO_TABLE + "( " +
                    TodoColumns.BODY + ", " + TodoColumns.PRIORITY +
                    ") VALUES " +
                    "('Task1', 1), " +
                    "('Task2', 3), " +
                    "('Task3', 2), " +
                    "('Task4', 2), " +
                    "('Task5', 1), " +
                    "('Task6', 1), " +
                    "('Task7', 1), " +
                    "('Task8', 2), " +
                    "('Task9', 9001), " +
                    "('Task10', 1), " +
                    "('Task11', 2), " +
                    "('Task12', 3), " +
                    "('Task13', 2), " +
                    "('Task14', 2), " +
                    "('Task15', 1), " +
                    "('Task16', 2), " +
                    "('Task17', 3), " +
                    "('Task18', 1), " +
                    "('Task19', 2), " +
                    "('Task20', 2);";
            db.execSQL(sql);
        } catch (SQLiteException e) {
            Log.e(LOG_TAG, "Failed to populate " + DB_FILE_NAME, e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static Todo readTodoFromCursor(Cursor cursor){
        int idIndex = cursor.getColumnIndexOrThrow(TodoColumns._ID);
        long id = cursor.getLong(idIndex);

        int bodyIndex = cursor.getColumnIndexOrThrow(TodoColumns.BODY);
        String body = cursor.getString(bodyIndex);

        int priorityIndex = cursor.getColumnIndexOrThrow(TodoColumns.PRIORITY);
        int priority = cursor.getInt(priorityIndex);

        return new Todo(id, body, priority);
    }
}
