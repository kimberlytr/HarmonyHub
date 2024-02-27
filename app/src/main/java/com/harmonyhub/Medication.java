package com.harmonyhub;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import com.harmonyhub.NotificationReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class Medication extends AppCompatActivity {

    private TextInputEditText medicationTypeInput, quantity, dateInput, timeInput;
    private int selectedDay = -1;
    private int selectedHour = -1;
    private int selectedMinute = -1;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_medication );

        dbHelper = new DatabaseHelper( this );

        Toolbar toolbar = findViewById( R.id.medication_toolbar );
        setSupportActionBar( toolbar );
        Objects.requireNonNull( getSupportActionBar() ).setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setDisplayShowHomeEnabled( true );

        medicationTypeInput = findViewById( R.id.edit_medication_type );
        quantity = findViewById( R.id.edit_quantity );

        dateInput = findViewById( R.id.edit_text_med_date );
        dateInput.setOnClickListener( view -> showDatePicker() );

        timeInput = findViewById( R.id.edit_text_time );
        timeInput.setOnClickListener( view -> showTimePicker() );

        Button saveMedicationButton = findViewById( R.id.button_save_medication );
        saveMedicationButton.setOnClickListener( v -> {
            if (selectedDay != -1) {
                saveMedication();
                // Start HistoryActivity after saving water consumption
                Intent intent = new Intent( Medication.this, HistoryActivity.class );
                intent.putExtra( "selected_day", selectedDay );
                startActivity( intent );
            } else {
                showSnackbar( "Please select a date first!" );
            }

        } );

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
                    dateInput.setText(new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(selectedDate.getTime()));
                    Snackbar.make(findViewById(android.R.id.content), "Selected Day: " + selectedDate.getTime(), Snackbar.LENGTH_SHORT).show();
                },
                year, month, dayOfMonth);

        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (timePicker, hourOfDay, minuteSelected) -> {
                    selectedHour = hourOfDay;
                    selectedMinute = minuteSelected;
                    timeInput.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteSelected));
                    Snackbar.make(findViewById(android.R.id.content),
                            "Selected Time: " + hourOfDay + ":" + minuteSelected, Snackbar.LENGTH_SHORT).show();
                },
                hour, minute, false);

        timePickerDialog.show();
    }

    private void saveMedication() {
        String type = medicationTypeInput.getText().toString().trim();
        String quantityStr = quantity.getText().toString().trim();
        int quantity = quantityStr.isEmpty() ? 0 : Integer.parseInt(quantityStr);

        // Check if medication type is empty
        if (type.isEmpty()) {
            showSnackbar("Please enter medication type.");
            return;
        }

        // Check if quantity is valid (greater than 0)
        if (quantity <= 0) {
            showSnackbar("Please enter a valid quantity.");
            return;
        }

        // Check if dayOfWeek, hourOfDay, and minute are initialized
        if (selectedDay == -1 || selectedHour == -1 || selectedMinute == -1) {
            showSnackbar("Please select a date and time for medication.");
            return;
        }

        long newRowId = dbHelper.insertMedication(type, quantity, selectedDay, selectedHour, selectedMinute);

        if (newRowId != -1) {
            showSnackbar("Medication saved!");

            // Schedule notification for medication
            scheduleNotification(newRowId, selectedDay);
        } else {
            showSnackbar("Error saving medication.");
        }
    }


    private void scheduleNotification(long medicationId, int dayOfWeek) {
        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        notificationIntent.putExtra("medication_id", medicationId);
        notificationIntent.putExtra("day_of_week", dayOfWeek);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        // Schedule the notification using AlarmManager
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {

            long triggerTimeMillis = 1000; // Replace with the desired calculated trigger time
           // long intervalMillis = 1000; // Replace with the desired interval for repeating notifications (if any)

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent);

            //To repeat the notification, uncomment the following line and specify the interval
            // alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTimeMillis, intervalMillis, pendingIntent);
        }
    }


    private void showSnackbar(String message) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}
