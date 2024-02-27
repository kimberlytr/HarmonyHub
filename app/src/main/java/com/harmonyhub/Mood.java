package com.harmonyhub;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class Mood extends AppCompatActivity {

    private TextInputEditText dateInput;
    private int selectedDay = -1;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood);

        dbHelper = new DatabaseHelper(this);

        MaterialToolbar toolbar = findViewById(R.id.mood_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        AppCompatButton happyButton = findViewById(R.id.button_happy);
        AppCompatButton sadButton = findViewById(R.id.button_sad);
        AppCompatButton angryButton = findViewById(R.id.button_angry);
        AppCompatButton annoyedButton = findViewById(R.id.button_annoyed);
        AppCompatButton nervousButton = findViewById(R.id.button_nervous);
        AppCompatButton calmButton = findViewById(R.id.button_calm);

        happyButton.setOnClickListener(view -> saveMood("Happy"));
        sadButton.setOnClickListener(view -> saveMood("Sad"));
        angryButton.setOnClickListener(view -> saveMood("Angry"));
        annoyedButton.setOnClickListener(view -> saveMood("Annoyed"));
        nervousButton.setOnClickListener(view -> saveMood("Nervous"));
        calmButton.setOnClickListener(view -> saveMood("Calm"));

        dateInput = findViewById(R.id.edit_text_date);
        dateInput.setOnClickListener(view -> showDatePicker());

        Button saveButton = findViewById(R.id.button_save_mood);
        saveButton.setOnClickListener(v -> {
            if (selectedDay != -1) {
                saveMood("");
                // Start HistoryActivity after saving water consumption
                Intent intent = new Intent(Mood.this, HistoryActivity.class);
                intent.putExtra("selected_day", selectedDay);
                startActivity(intent);
            } else {
                showSnackbar("Please select a date first!");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (datePicker, yearSelected, monthOfYear, dayOfMonthSelected) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(yearSelected, monthOfYear, dayOfMonthSelected);
                    selectedDay = selectedDate.get(Calendar.DAY_OF_WEEK);
                    Snackbar.make(findViewById(android.R.id.content), "Selected Day: " + selectedDate.getTime(), Snackbar.LENGTH_SHORT).show();
                },
                year, month, dayOfMonth);

        datePickerDialog.show();
    }

    private void saveMood(String mood) {
        if (selectedDay == -1) {
            Snackbar.make(findViewById(android.R.id.content), "Select a date first!", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (mood == null || mood.isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), "Please enter a mood.", Snackbar.LENGTH_SHORT).show();
            return;
        }

        long newRowId = dbHelper.insertMood(mood, selectedDay);

        if (newRowId != -1) {
            showSnackbar("Mood saved!");
        } else {
            showSnackbar("Error saving mood.");
        }
    }


    private void showSnackbar(String message) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show();
    }
}
