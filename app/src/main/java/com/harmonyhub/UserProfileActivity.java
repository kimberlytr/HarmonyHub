package com.harmonyhub;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {

    private EditText fullNameInput, emailInput;
    private TextView fullNameText, emailText;
    private Button editButton, saveButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fullNameInput = findViewById(R.id.edit_full_name);
        emailInput = findViewById(R.id.edit_email);
        fullNameText = findViewById(R.id.text_full_name);
        emailText = findViewById(R.id.text_email);
        editButton = findViewById(R.id.button_edit_profile);
        saveButton = findViewById(R.id.button_save_profile);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        editButton.setOnClickListener(view -> setEditMode(true));
        saveButton.setOnClickListener(view -> updateProfileInformation());

        displayProfileInformation();
    }

    private void displayProfileInformation() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            fullNameInput.setText(user.getFullName());
                            emailInput.setText(user.getEmail());
                            fullNameText.setText(user.getFullName());
                            emailText.setText(user.getEmail());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });
        }
    }

    private void updateProfileInformation() {
        String updatedFullName = fullNameInput.getText().toString();
        String updatedEmail = emailInput.getText().toString();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            User updatedUser = new User();
            updatedUser.setFullName(updatedFullName);
            updatedUser.setEmail(updatedEmail);

            mDatabase.child(userId).setValue(updatedUser)
                    .addOnSuccessListener(aVoid -> {
                        fullNameText.setText(updatedFullName);
                        emailText.setText(updatedEmail);
                        setEditMode(false);
                    })
                    .addOnFailureListener(e -> {
                        // Handle error
                    });
        }
    }

    private void setEditMode(boolean isEditMode) {
        if (isEditMode) {
            fullNameInput.setVisibility(android.view.View.VISIBLE);
            emailInput.setVisibility(android.view.View.VISIBLE);
            fullNameText.setVisibility(android.view.View.GONE);
            emailText.setVisibility(android.view.View.GONE);
            editButton.setVisibility(android.view.View.GONE);
            saveButton.setVisibility(android.view.View.VISIBLE);
        } else {
            fullNameInput.setVisibility(android.view.View.GONE);
            emailInput.setVisibility(android.view.View.GONE);
            fullNameText.setVisibility(android.view.View.VISIBLE);
            emailText.setVisibility(android.view.View.VISIBLE);
            editButton.setVisibility(android.view.View.VISIBLE);
            saveButton.setVisibility(android.view.View.GONE);
        }
    }


}
