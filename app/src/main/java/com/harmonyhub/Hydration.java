package com.harmonyhub;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Objects;

public class Hydration extends AppCompatActivity {

    private TextInputEditText cupsInput, dateInput;
    private int selectedDay = -1;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hydration);

        dbHelper = new DatabaseHelper(this);

        Toolbar toolbar = findViewById(R.id.hydration_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        cupsInput = findViewById(R.id.edit_cups_input);

        dateInput = findViewById(R.id.edit_text_date);
        dateInput.setOnClickListener(view -> showDatePicker());

        Button saveButton = findViewById(R.id.button_save_water);
        saveButton.setOnClickListener(v -> {
            if (selectedDay != -1) {
                saveWaterConsumption();
                // Start HistoryActivity after saving water consumption
                Intent intent = new Intent(Hydration.this, HistoryActivity.class);
                intent.putExtra("selected_day", selectedDay);
                startActivity(intent);
            } else {
                showSnackbar("Please select a date first!");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
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

    private void saveWaterConsumption() {
        String cups = Objects.requireNonNull(cupsInput.getText()).toString();
        long newRowId = dbHelper.insertHydration(Integer.parseInt(cups), selectedDay);

        if (newRowId != -1) {
            showSnackbar("Water saved!");
        } else {
            showSnackbar("Error saving water.");
        }

        cupsInput.setText("");
    }

    private void showSnackbar(String message) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show();
    }
}
