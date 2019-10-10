package com.example.android.multinotes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

public class EditActivity extends AppCompatActivity {

    private static final String TAG = "EditActivity";
    private EditText titleView;
    private EditText descriptionView;

    private String existingTitleValue = "";
    private String existingDesValue = "";


    private int notePosition;
    private boolean isExistingNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        titleView = findViewById(R.id.editText);
        descriptionView = findViewById(R.id.editText2);
        descriptionView.setMovementMethod(new ScrollingMovementMethod());

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("IS_EXISTING_NOTE")) {

            isExistingNote = intent.getBooleanExtra("IS_EXISTING_NOTE", false);
            if (isExistingNote) {

                Notes exitingNote = (Notes) intent.getSerializableExtra("EXISTING_NOTE");
                notePosition = intent.getIntExtra("EXISTING_NOTE_POSITION", 0);
                if (exitingNote != null) {

                    existingTitleValue = exitingNote.getTitle();
                    existingDesValue = exitingNote.getDescription();
                    titleView.setText(existingTitleValue);
                    descriptionView.setText(existingDesValue);
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.item3) {
            checkandSave(false);
            return true;
        } else {

            return super.onOptionsItemSelected(item);
        }
    }

    private void checkandSave(boolean backButtonPressed) {

        String title = titleView.getText().toString();
        String description = descriptionView.getText().toString();
        if (isExistingNote) {

            if (!TextUtils.isEmpty(title)) {

                boolean ifTitleChanged = !TextUtils.isEmpty(title) &&
                        !TextUtils.isEmpty(existingTitleValue) &&
                        !TextUtils.equals(title, existingTitleValue);

                if (ifTitleChanged || !TextUtils.equals(description, existingDesValue)) {
                    if (backButtonPressed) {
                        confirmationDialog(false);
                    } else {
                        saveExistingNote();
                    }
                } else {
                    exitActivity();
                }
            } else {
                Toast.makeText(this, "Title is not entered", Toast.LENGTH_SHORT).show();
                exitActivity();
            }

        } else {
            if (!TextUtils.isEmpty(title)) {
                if (backButtonPressed) {
                    confirmationDialog(true);
                } else {
                    saveNote();
                    Toast.makeText(this, "New Note Added", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Title is not entered", Toast.LENGTH_SHORT).show();
                exitActivity();
            }
        }
    }

    public void exitActivity() {

        Intent data = new Intent();
        data.putExtra("NOTE_CHANGED", false);
        setResult(RESULT_OK, data);
        finish();
    }

    private void saveExistingNote() {
        String title = titleView.getText().toString();
        String des = descriptionView.getText().toString();
        Intent data = new Intent();
        Notes existingNote = new Notes(title, des, new Date());
        data.putExtra("EXISTING_NOTE", existingNote);
        data.putExtra("EXISTING_NOTE_POSITION", notePosition);
        data.putExtra("NOTE_CHANGED", true);
        setResult(RESULT_OK, data);
        finish();
    }

    private void saveNote() {
        String title = titleView.getText().toString();
        String des = descriptionView.getText().toString();
        Intent data = new Intent();
        Notes newNote = new Notes(title, des, new Date());
        data.putExtra("NEW_NOTE", newNote);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: ");
        outState.putInt("EXISTING_NOTE_POS", notePosition);
        outState.putBoolean("IS_EXISTING_NOTE", isExistingNote);
        outState.putString("TITLE_VAL_EXISTING_NOTE", existingTitleValue);
        outState.putString("DES_VAL_EXISTING_NOTE", existingDesValue);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        Log.d(TAG, "onRestoreInstanceState: ");
        super.onRestoreInstanceState(savedState);
        notePosition = savedState.getInt("EXISTING_NOTE_POS");
        isExistingNote = savedState.getBoolean("IS_EXISTING_NOTE");
        existingTitleValue = savedState.getString("TITLE_VAL_EXISTING_NOTE");
        existingDesValue = savedState.getString("DES_VAL_EXISTING_NOTE");
    }


    @Override
    public void onBackPressed() {
        checkandSave(true);
    }

    public void confirmationDialog(final boolean newNote) {
       AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String titleEntered = titleView.getText().toString();

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (newNote) {
                    saveNote();
                } else {
                    saveExistingNote();
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                exitActivity();
            }
        });
        builder.setMessage("Your note is not saved!\nSave note '" + titleEntered + "'?");
        AlertDialog dialog = builder.create();
        dialog.show();

    }

}
