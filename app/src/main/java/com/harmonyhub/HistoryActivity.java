
package com.harmonyhub;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class HistoryActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private Button btnSelectDate;
    private int selectedDayOfWeek;
    private TextView selectedDateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);
        btnSelectDate = findViewById(R.id.button_select_date);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("History");

        // Initialize selectedDayOfWeek with the current date
        Calendar calendar = Calendar.getInstance();
        selectedDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // Retrieve data and display in TextViews
        displayHistory();

        // Show DatePicker when toolbar is clicked
        btnSelectDate.setOnClickListener(v -> showDatePicker());

        // Initialize TextView for selected date
        selectedDateTextView = findViewById(R.id.text_selected_date);
        updateSelectedDateTextView(selectedDayOfWeek, calendar);
    }

    private void updateSelectedDateTextView(int dayOfWeek, Calendar selectedDate) {
        String formattedSelectedDate = getDayOfWeek(dayOfWeek) + ", " + new SimpleDateFormat("MMMM dd", Locale.getDefault()).format(selectedDate.getTime());
        selectedDateTextView.setText(formattedSelectedDate);
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
                (DatePicker datePicker, int yearSelected, int monthOfYear, int dayOfMonthSelected) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(yearSelected, monthOfYear, dayOfMonthSelected);
                    selectedDayOfWeek = selectedDate.get(Calendar.DAY_OF_WEEK);
                    displayHistory();
                    updateSelectedDateTextView(selectedDayOfWeek, selectedDate); // Pass the selected date
                },
                year, month, dayOfMonth);

        datePickerDialog.show();
    }

    private void displayHistory() {
        // Retrieve data from the database
        String mealHistory = databaseHelper.getMealHistory(selectedDayOfWeek);
        String medicationHistory = databaseHelper.getMedicationHistory(selectedDayOfWeek);
        String hydrationHistory = databaseHelper.getHydrationHistory(selectedDayOfWeek);
        String moodHistory = databaseHelper.getMood(selectedDayOfWeek);

        // Display data in TextViews
        TextView mealHistoryTextView = findViewById(R.id.text_meal_history);
        TextView medicationHistoryTextView = findViewById(R.id.text_medication_history);
        TextView hydrationHistoryTextView = findViewById(R.id.text_hydration_history);
        TextView moodHistoryTextView = findViewById(R.id.text_mood_history);

        mealHistoryTextView.setText(formatHistory(mealHistory));
        medicationHistoryTextView.setText(formatHistory(medicationHistory));
        hydrationHistoryTextView.setText(formatHistory(hydrationHistory));
        moodHistoryTextView.setText(formatHistory(moodHistory));

        // Set click listeners to delete entries
        setDeleteClickListener(mealHistoryTextView, DatabaseHelper.TABLE_MEAL, DatabaseHelper.COLUMN_MEAL_DATE);
        setDeleteClickListener(medicationHistoryTextView, DatabaseHelper.TABLE_MEDICATION, DatabaseHelper.COLUMN_MEDICATION_DATE);
        setDeleteClickListener(hydrationHistoryTextView, DatabaseHelper.TABLE_WATER, DatabaseHelper.COLUMN_WATER_DATE);
        setDeleteClickListener(moodHistoryTextView, DatabaseHelper.TABLE_MOOD, DatabaseHelper.COLUMN_MOOD_DATE);

    }

    private void setDeleteClickListener(TextView textView, String tableName, String columnName) {
        textView.setOnClickListener( v -> {
            // Show a confirmation dialog
            AlertDialog.Builder builder = new AlertDialog.Builder( this );
            builder.setMessage( "Are you sure you want to delete this entry?" )
                    .setPositiveButton( "Delete", (dialog, which) -> {
                        // Get the selected day of week and delete the entry from the database
                        databaseHelper.deleteHistoryEntry(selectedDayOfWeek, tableName, columnName);
                        // Update the displayed history
                        displayHistory();
                    } )
                    .setNegativeButton( "Cancel", (dialog, which) -> {
                        // User cancelled the delete operation
                        dialog.dismiss();
                    } )
                    .show();
        } );
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the database connection when the activity is destroyed
        databaseHelper.close();
    }

    // Method to get the day of the week in string format
    private String getDayOfWeek(int dayOfWeek) {
        String[] daysOfWeek = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        return daysOfWeek[dayOfWeek - 1]; // Subtract 1 because Calendar.DAY_OF_WEEK starts from 1
    }

    // Method to format the history data as bullet points with line breaks
    private String formatHistory(String history) {
        if (history == null || history.isEmpty()) {
            return "• No entries";
        }

        // Split the history string into individual lines
        String[] lines = history.split("\n");

        // Add a bullet point to the beginning of each line
        StringBuilder formattedHistory = new StringBuilder();
        for (String line : lines) {
            formattedHistory.append("• ").append(line).append("\n");
        }

        return formattedHistory.toString().trim();
    }
}
