package de.fs.fintech.geogame;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import de.fs.fintech.geogame.data.Todo;
import de.fs.fintech.geogame.sqlite.TodoSqliteHelper;

public class CursorAdapterActivity extends AppCompatActivity {

    private TodoCursorAdapter adapter;
    private ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cursor_adapter);

        listView = findViewById(android.R.id.list);

        View emptyView = findViewById(android.R.id.empty);
        listView.setEmptyView(emptyView);

        new TodoQueryTask().execute();
    }

    private static class TodoCursorAdapter extends CursorAdapter {

        public TodoCursorAdapter(Context context, Cursor c) {
            super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {

            return LayoutInflater.from(context).inflate(
                    R.layout.list_item_todo,
                    parent,
                    false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Todo todo = TodoSqliteHelper.readTodoFromCursor(cursor);

            TextView bodyTextView = view.findViewById(R.id.txt_todo_body);
            bodyTextView.setText(todo.getBody());

            TextView priorityTextView = view.findViewById(R.id.txt_todo_priority);
            priorityTextView.setText(String.valueOf(todo.getPriority()));
        }
    }

    private class TodoQueryTask extends AsyncTask<Void, Void, Cursor> {
        @Override
        protected Cursor doInBackground(Void... voids) {
            TodoSqliteHelper sqliteHelper = new TodoSqliteHelper(getApplicationContext());
            SQLiteDatabase db = sqliteHelper.getReadableDatabase();
            return db.query(TodoSqliteHelper.TODO_TABLE, new String[]{
                    TodoSqliteHelper.TodoColumns._ID,
                    TodoSqliteHelper.TodoColumns.BODY,
                    TodoSqliteHelper.TodoColumns.PRIORITY
            }, null, null, null, null, null);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);

            if (null == adapter) {
                adapter = new TodoCursorAdapter(CursorAdapterActivity.this, cursor);
                listView.setAdapter(adapter);
            } else {
                adapter.swapCursor(cursor);
            }
        }
    }

}
