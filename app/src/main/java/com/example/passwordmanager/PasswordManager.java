package com.example.passwordmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class PasswordManager extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PasswordEntryAdapter adapter;
    private List<PasswordEntry> passwordEntries;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_manager);

        recyclerView = findViewById(R.id.recyclerViewPasswordEntries);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseHelper = new DatabaseHelper(this);

        // Fetch all password entries from database
        passwordEntries = databaseHelper.getAllPasswordEntries();

        adapter = new PasswordEntryAdapter(passwordEntries);
        recyclerView.setAdapter(adapter);

        // Implement button click to add new entry
        FloatingActionButton fabAddEntry = findViewById(R.id.fabAddEntry);
        fabAddEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PasswordManager.this, AddEditEntryActivity.class);
                intent.putExtra("mode", "add");
                startActivity(intent);
            }
        });

        // Implement item click listener for editing or deleting entries
        adapter.setOnItemClickListener(new PasswordEntryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                PasswordEntry entry = passwordEntries.get(position);
                showOptionsDialog(entry);
            }
        });
    }







    // Method to show options dialog for editing or deleting an entry
    private void showOptionsDialog(final PasswordEntry entry) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Options");
        String[] options;
        if (entry.isDeleted()) {
            options = new String[]{"Restore", "Delete Permanently"};
        } else {
            options = new String[]{"Edit", "Delete", "Move to Recycle Bin"};
        }
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        if (entry.isDeleted()) {
                            restoreEntry(entry);
                        } else {
                            editEntry(entry);
                        }
                        break;
                    case 1:
                        if (entry.isDeleted()) {
                            deletePermanently(entry);
                        } else {
                            deleteEntry(entry);
                        }
                        break;
                    case 2:
                        moveToRecycleBin(entry);
                        break;
                }
            }
        });
        builder.create().show();
    }

    private void moveToRecycleBin(PasswordEntry entry) {
        databaseHelper.softDeletePasswordEntry(entry);
        passwordEntries.remove(entry);
        adapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(), "Entry moved to Recycle Bin", Toast.LENGTH_SHORT).show();
    }

    private void restoreEntry(PasswordEntry entry) {
        databaseHelper.restorePasswordEntry(entry);
        passwordEntries.add(entry);
        adapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(), "Entry restored", Toast.LENGTH_SHORT).show();
    }

    private void deletePermanently(PasswordEntry entry) {
        databaseHelper.deletePasswordEntry(entry);
        passwordEntries.remove(entry);
        adapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(), "Entry deleted permanently", Toast.LENGTH_SHORT).show();
    }


    // Method to edit an existing entry
    private void editEntry(PasswordEntry entry) {
        Intent intent = new Intent(PasswordManager.this, AddEditEntryActivity.class);
        intent.putExtra("mode", "edit");
        intent.putExtra("entry", (Parcelable) entry);
        startActivity(intent);
    }

    // Method to delete an entry
    private void deleteEntry(PasswordEntry entry) {
        databaseHelper.deletePasswordEntry(entry);
        passwordEntries.remove(entry);
        adapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(), "Entry deleted", Toast.LENGTH_SHORT).show();
    }

    // RecyclerView Adapter for password entries
    private static class PasswordEntryAdapter extends RecyclerView.Adapter<PasswordEntryAdapter.PasswordEntryViewHolder> {

        private List<PasswordEntry> entries;
        private OnItemClickListener listener;

        public PasswordEntryAdapter(List<PasswordEntry> entries) {
            this.entries = entries;
        }

        @NonNull
        @Override
        public PasswordEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_password_entry, parent, false);
            return new PasswordEntryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PasswordEntryViewHolder holder, int position) {
            PasswordEntry entry = entries.get(position);
            holder.bind(entry);
        }

        @Override
        public int getItemCount() {
            return entries.size();
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        // ViewHolder class
        public class PasswordEntryViewHolder extends RecyclerView.ViewHolder {

            private TextView textViewUsername;
            private TextView textViewWebsiteUrl;

            public PasswordEntryViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewUsername = itemView.findViewById(R.id.textViewUsername);
                textViewWebsiteUrl = itemView.findViewById(R.id.textViewWebsiteUrl);

                // Implement item click listener
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                listener.onItemClick(position);
                            }
                        }
                    }
                });
            }

            public void bind(PasswordEntry entry) {
                textViewUsername.setText(entry.getUsername());
                textViewWebsiteUrl.setText(entry.getWebsiteUrl());
            }
        }
    }

}
