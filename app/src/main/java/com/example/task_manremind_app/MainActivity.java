package com.example.task_manremind_app;

import android.content.DialogInterface;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;

    private FloatingActionButton fab;
    private AlertDialog createTaskDialog;
    private AlertDialog editTaskDialog;
    private TaskDbHelper dbHelper;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dbHelper = new TaskDbHelper(this);
        database = dbHelper.getWritableDatabase();

        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList, this);
        recyclerView.setAdapter(taskAdapter);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateTaskDialog();
            }
        });

        loadTasksFromDatabase();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

    private void loadTasksFromDatabase() {
        taskList.clear();

        Cursor cursor = database.query(
                TaskContract.TaskEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            int idColumnIndex = cursor.getColumnIndex(TaskContract.TaskEntry._ID);
            int titleColumnIndex = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TITLE);
            int descriptionColumnIndex = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_DESCRIPTION);
            int dueDateColumnIndex = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_DUE_DATE);
            int priorityColumnIndex = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_PRIORITY);

            int id = cursor.getInt(idColumnIndex);
            String title = cursor.getString(titleColumnIndex);
            String description = cursor.getString(descriptionColumnIndex);
            String dueDate = cursor.getString(dueDateColumnIndex);
            int priority = cursor.getInt(priorityColumnIndex);

            Task task = new Task(id, title, description, dueDate, priority);
            taskList.add(task);
        }

        cursor.close();
        taskAdapter.notifyDataSetChanged();
    }

    private long insertTaskIntoDatabase(Task task) {
        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_TITLE, task.getTitle());
        values.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, task.getDescription());
        values.put(TaskContract.TaskEntry.COLUMN_DUE_DATE, task.getDueDate());
        values.put(TaskContract.TaskEntry.COLUMN_PRIORITY, task.getPriority());

        return database.insert(TaskContract.TaskEntry.TABLE_NAME, null, values);
    }

    private void updateTaskInDatabase(Task task) {
        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_TITLE, task.getTitle());
        values.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, task.getDescription());
        values.put(TaskContract.TaskEntry.COLUMN_DUE_DATE, task.getDueDate());
        values.put(TaskContract.TaskEntry.COLUMN_PRIORITY, task.getPriority());

        String selection = TaskContract.TaskEntry._ID + "=?";
        String[] selectionArgs = {String.valueOf(task.getId())};

        database.update(TaskContract.TaskEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    private void deleteTaskFromDatabase(Task task) {
        String selection = TaskContract.TaskEntry._ID + "=?";
        String[] selectionArgs = {String.valueOf(task.getId())};

        database.delete(TaskContract.TaskEntry.TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public void onTaskClick(int position) {
        Task task = taskList.get(position);
        showEditTaskDialog(position, task);
    }

    @Override
    public void onEditTaskClick(int position, Task task) {
        showEditTaskDialog(position, task);
    }

    @Override
    public void onDeleteTaskClick(int position, Task task) {
        deleteTaskFromDatabase(task);

        taskList.remove(position);
        taskAdapter.notifyItemRemoved(position);
    }

    private void showCreateTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_task, null);
        builder.setView(dialogView);

        final EditText titleEditText = dialogView.findViewById(R.id.titleEditText);
        final EditText descriptionEditText = dialogView.findViewById(R.id.descriptionEditText);
        final EditText dueDateEditText = dialogView.findViewById(R.id.dueDateEditText);
        final EditText priorityEditText = dialogView.findViewById(R.id.priorityEditText);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = titleEditText.getText().toString().trim();
                String description = descriptionEditText.getText().toString().trim();
                String dueDate = dueDateEditText.getText().toString().trim();
                String priorityText = priorityEditText.getText().toString().trim();

                if (title.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter a title", Toast.LENGTH_SHORT).show();
                    return;
                }

                int priority = 0;
                if (!priorityText.isEmpty()) {
                    priority = Integer.parseInt(priorityText);
                }

                Task newTask = new Task(0, title, description, dueDate, priority);
                taskList.add(newTask);
                taskAdapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        createTaskDialog = builder.create();
        createTaskDialog.show();
    }

    private void showEditTaskDialog(final int position, Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_task, null);
        builder.setView(dialogView);

        final EditText titleEditText = dialogView.findViewById(R.id.titleEditText);
        final EditText descriptionEditText = dialogView.findViewById(R.id.descriptionEditText);
        final EditText dueDateEditText = dialogView.findViewById(R.id.dueDateEditText);
        final EditText priorityEditText = dialogView.findViewById(R.id.priorityEditText);

        titleEditText.setText(task.getTitle());
        descriptionEditText.setText(task.getDescription());
        dueDateEditText.setText(task.getDueDate());
        priorityEditText.setText(String.valueOf(task.getPriority()));

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = titleEditText.getText().toString();
                String description = descriptionEditText.getText().toString();
                String dueDate = dueDateEditText.getText().toString();
                int priority = Integer.parseInt(priorityEditText.getText().toString());

                Task updatedTask = new Task(task.getId(), title, description, dueDate, priority);
                updateTaskInDatabase(updatedTask);
                taskList.set(position, updatedTask);
                taskAdapter.notifyItemChanged(position);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        editTaskDialog = builder.create();
        editTaskDialog.show();
    }

    // Other methods and menu handling code
}