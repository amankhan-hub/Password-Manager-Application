package com.example.passwordmanager;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddEditEntryActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword, editTextWebsiteUrl;
    private Button buttonSave;

    private DatabaseHelper databaseHelper;
    private String mode; // "add" or "edit"
    private PasswordEntry entryToEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_entry);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextWebsiteUrl = findViewById(R.id.editTextWebsiteUrl);
        buttonSave = findViewById(R.id.buttonSave);

        databaseHelper = new DatabaseHelper(this);

        mode = getIntent().getStringExtra("mode");

        if (mode.equals("edit")) {
            entryToEdit = getIntent().getParcelableExtra("entry");
            if (entryToEdit != null) {
                editTextUsername.setText(entryToEdit.getUsername());
                editTextPassword.setText(entryToEdit.getPassword());
                editTextWebsiteUrl.setText(entryToEdit.getWebsiteUrl());
            }
        }

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEntry();
            }
        });
    }

    private void saveEntry() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String websiteUrl = editTextWebsiteUrl.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            editTextUsername.setError("Please enter username");
            editTextUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Please enter password");
            editTextPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(websiteUrl)) {
            editTextWebsiteUrl.setError("Please enter website URL");
            editTextWebsiteUrl.requestFocus();
            return;
        }

        if (mode.equals("add")) {
            // Add new entry
            long id = databaseHelper.addPasswordEntry(new PasswordEntry(username, password, websiteUrl));
            if (id != -1) {
                Toast.makeText(getApplicationContext(), "Entry added successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to add entry", Toast.LENGTH_SHORT).show();
            }
        } else if (mode.equals("edit")) {
            // Edit existing entry
            if (entryToEdit != null) { // Ensure entryToEdit is not null
                entryToEdit.setUsername(username);
                entryToEdit.setPassword(password);
                entryToEdit.setWebsiteUrl(websiteUrl);

                int rowsAffected = databaseHelper.updatePasswordEntry(entryToEdit.getId(), entryToEdit.getUsername(), entryToEdit.getPassword(), entryToEdit.getWebsiteUrl());
                if (rowsAffected > 0) {
                    Toast.makeText(getApplicationContext(), "Entry updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to update entry", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Failed to update entry: entryToEdit is null", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
