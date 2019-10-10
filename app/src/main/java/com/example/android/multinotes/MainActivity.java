package com.example.android.multinotes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{

    private static final String TAG = "MainActivity";
    private static final String TITLE = "Multi Notes";
    private final List<Notes> noteslist = new ArrayList<>();
    private static final int IF_NEW_NOTE = 0;
    private static final int IF_EXISTING_NOTE = 1;
    private boolean noteListChanged = false;


    private RecyclerView recyclerView;
    private NotesAdapter notesAdapter;
    Notes notes = new Notes();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById((R.id.recyclerView));
        notesAdapter = new NotesAdapter(noteslist, this);

        recyclerView.setAdapter(notesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        notes = loadFile();
        setTitle(TITLE + " (" + noteslist.size() + ")");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == R.id.item1)
        {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, MainActivity.class.getSimpleName());
            startActivity(intent);
            return true;
        }

        else if(item.getItemId() == R.id.item2)
        {
            EditActivity(false,0,null);
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
   protected void onPause() {
        saveNotes();
        super.onPause();
    }



    private void EditActivity(boolean existingNote, int notePosition, Notes notes)
    {
        int requestCode = IF_NEW_NOTE;
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("IS_EXISTING_NOTE", existingNote);
        if (existingNote)
        {
            requestCode = IF_EXISTING_NOTE;
            if (notes != null) {
                intent.putExtra("EXISTING_NOTE", notes);
                intent.putExtra("EXISTING_NOTE_POSITION", notePosition);
                startActivityForResult(intent, requestCode);
            }
        }
        else {
            startActivityForResult(intent, requestCode);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IF_NEW_NOTE) {
            if (resultCode == RESULT_OK) {
                Notes new_note = (Notes) data.getSerializableExtra("NEW_NOTE");
                if (new_note != null) {
                    noteslist.add(new_note);
                    reloadRecycler();
                }
            } else {
                Log.d(TAG, "onActivityResult: result Code: " + resultCode);
            }

        } else if (requestCode == IF_EXISTING_NOTE) {
            if (resultCode == RESULT_OK) {
                boolean isNoteChanged = data.getBooleanExtra("NOTE_CHANGED", false);
                if (isNoteChanged) {
                    Notes existingNote = (Notes) data.getSerializableExtra("EXISTING_NOTE");
                    int notePosition = data.getIntExtra("EXISTING_NOTE_POSITION", 0);
                    if (existingNote != null) {
                        noteslist.set(notePosition, existingNote);
                        Log.d(TAG, "onActivityResult: ExistingNoteEdited: " + existingNote.toString());
                        reloadRecycler();
                    }
                }
            }
            else {
                Log.d(TAG, "onActivityResult: ExistingNoteEdited: " + resultCode);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: ");
        outState.putBoolean("IS_NOTE_LIST_CHANGED", noteListChanged);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        Log.d(TAG, "onRestoreInstanceState: ");
        super.onRestoreInstanceState(savedState);
        noteListChanged = savedState.getBoolean("IS_NOTE_LIST_CHANGED");
    }

    private void reloadRecycler() {
        noteListChanged = true;
        Collections.sort(noteslist);
        setTitle(TITLE + " (" + noteslist.size() + ")");
        notesAdapter.notifyDataSetChanged();
    }



    public void DeleteNote(final int notePosition, final String noteTitle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                    noteslist.remove(notePosition);
                    reloadRecycler();
                    Toast.makeText(MainActivity.this, "Note '" + noteTitle + "' deleted ", Toast.LENGTH_SHORT).show();

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.setMessage("Delete Note '" + noteTitle + "'?");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(View view) {

        int notePosition = recyclerView.getChildLayoutPosition(view);
        Notes notes = noteslist.get(notePosition);
        EditActivity(true, notePosition, notes);

    }

    @Override
    public boolean onLongClick(View view) {
        int position = recyclerView.getChildLayoutPosition(view);

        String title = "";
        Notes note = noteslist.get(position);
        if (note != null) {
            title = note.getTitle();
        }
        DeleteNote(position, title);
        return true;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private Notes loadFile()
    {

        try {
            InputStream inputStream = getApplicationContext().openFileInput(getString(R.string.file_name));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(line);
            }

            JSONObject jsonObject = new JSONObject(stringBuilder.toString());

            JSONArray notesJsonArray = jsonObject.getJSONArray("noteslist");
            if (notesJsonArray != null && notesJsonArray.length() > 0) {
                for (int i = 0; i < notesJsonArray.length(); i++) {
                    JSONObject notesJson = notesJsonArray.getJSONObject(i);
                    if (notesJson != null) {
                        noteslist.add(new Notes(notesJson.getString("title"), notesJson.getString("description"),
                                stringToDate(notesJson.getString("lastUpdatedTime"))));
                    }
                }
            }
            if (noteslist.size() > 0) {
                Collections.sort(noteslist);
            }
        }

        catch (FileNotFoundException e)
        {
            Toast.makeText(this, getString(R.string.no_file), Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return notes;
    }

    private void saveNotes()
    {
        try {

            Log.d(TAG, "saveNotes: ");


                FileOutputStream fos = getApplicationContext().
                        openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);
                JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos));
                writer.setIndent("  ");

                writer.beginObject();
                writer.name("noteslist");
                writer.beginArray();

                if (noteslist.size() > 0) {
                    for (Notes notes : noteslist) {
                        if (notes != null) {
                            writer.beginObject();
                            writer.name("title").value(notes.getTitle());
                            writer.name("description").value(notes.getDescription());
                            writer.name("lastUpdatedTime").value(dateToString(notes.getLastUpdatedTime()));
                            writer.endObject();
                        }
                    }
                }
                writer.endArray();
                writer.endObject();
                writer.close();
                noteListChanged = false;

        }
        catch (Exception e)
        {
            e.getStackTrace();
        }
    }


    private Date stringToDate(String lastUpdatedTime) {

        try {

            if (!TextUtils.isEmpty(lastUpdatedTime)) {
                SimpleDateFormat formatter = (SimpleDateFormat) DateFormat.getDateTimeInstance();
                formatter.applyPattern("EEE, d MMM yyyy HH:mm:ss");
                return formatter.parse(lastUpdatedTime);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String dateToString(Date lastUpdatedTime) {

        try {

            if (lastUpdatedTime != null) {
                SimpleDateFormat formatter = (SimpleDateFormat) DateFormat.getDateTimeInstance();
                formatter.applyPattern("EEE, d MMM yyyy HH:mm:ss");
                return formatter.format(lastUpdatedTime);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



}
