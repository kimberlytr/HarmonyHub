package com.harmonyhub;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


import android.content.Intent;


import androidx.annotation.NonNull;


public class Meal extends AppCompatActivity {

    private TextInputEditText calorieInput, dateInput;
    private AutoCompleteTextView mealNameInput;
    private int selectedDay = -1; // -1 indicates no day selected
    private DatabaseHelper dbHelper;
    private View parentLayout;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);

        MaterialToolbar toolbar = findViewById(R.id.meal_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dbHelper = new DatabaseHelper(this);

        mealNameInput = findViewById(R.id.mealNameInput);
        calorieInput = findViewById(R.id.edit_calorie_intake);
        parentLayout = findViewById(android.R.id.content);

        dateInput = findViewById(R.id.edit_text_date);
        dateInput.setOnClickListener(view -> showDatePicker());

        Button saveButton = findViewById(R.id.button_save_meal);
        saveButton.setOnClickListener(view -> {
            if (selectedDay != -1) {
                saveMeal();
                // Start HistoryActivity after saving meal
                Intent intent = new Intent(Meal.this, HistoryActivity.class);
                intent.putExtra("selected_day", selectedDay);
                startActivity(intent);
            } else {
                showSnackbar("Please select a date first!");
            }
        });


        // Asynchronously fetch meal names from the API and update the adapter
        executor.execute(new FetchMealNamesTask(mealNameInput));
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

    private void saveMeal() {
        String mealName = mealNameInput.getText().toString();
        String calorieIntake = calorieInput.getText().toString();

        long newRowId = dbHelper.insertMeal(mealName, Integer.parseInt(calorieIntake), selectedDay);

        if (newRowId != -1) {
            showSnackbar("Meal saved!");


        } else {
            showSnackbar("Error saving meal.");
        }
        mealNameInput.setText("");
        calorieInput.setText("");
    }



    private void showSnackbar(String message) {
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
