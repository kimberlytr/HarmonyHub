package com.harmonyhub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    Button btn_meals, btn_medication, btn_mood, btn_hydration, btn_profile, btn_history;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        FirebaseApp.initializeApp( this );
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference( "users" );

        // Create a new user
        User newUser = new User( "John Doe", "john.doe@example.com" );

        // Add the user to the "users" node in Firebase
        DatabaseReference newUserRef = usersRef.push();
        newUserRef.setValue( newUser );

        btn_meals = findViewById( R.id.btn_meals );
        btn_medication = findViewById( R.id.btn_medication );
        btn_mood = findViewById( R.id.btn_mood );
        btn_hydration = findViewById( R.id.btn_hyrdation );
        btn_profile = findViewById( R.id.btn_profile );
        btn_history = findViewById( R.id.btn_history );


        btn_meals.setOnClickListener( view -> {
            Intent i = new Intent( MainActivity.this, Meal.class );
            startActivity( i );
        } );

        btn_medication.setOnClickListener( view -> {
            Intent i = new Intent( MainActivity.this, Medication.class );
            startActivity( i );

        } );


        btn_mood.setOnClickListener( view -> {
            Intent i = new Intent( MainActivity.this, Mood.class );
            startActivity( i );

        } );

        btn_hydration.setOnClickListener( view -> {
            Intent i = new Intent( MainActivity.this, Hydration.class );
            startActivity( i );

        } );

        btn_profile.setOnClickListener( view -> {
            Intent i = new Intent( MainActivity.this, LoginAndRegister.class );
            startActivity( i );
        } );

        btn_history.setOnClickListener( view -> {
            Intent i = new Intent( MainActivity.this, HistoryActivity.class );
            startActivity( i );
        } );
    }
}