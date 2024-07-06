package com.example.passwordmanager;

public class PasswordEntry {
    private long id;
    private String username;
    private String password;
    private String websiteUrl;
    private boolean deleted;

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public PasswordEntry() {
        // Default constructor
    }

    public PasswordEntry( String username, String password, String websiteUrl) {
        this.username = username;
        this.password = password;
        this.websiteUrl = websiteUrl;
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }
}

