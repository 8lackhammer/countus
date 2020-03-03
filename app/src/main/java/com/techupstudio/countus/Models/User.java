package com.techupstudio.countus.Models;

import java.util.HashMap;
import java.util.Map;

public class User {

    private String name,
            email;

    private Map<String, Object> properties;


    public User(String name, String email, Map<String, Object> properties) {
        this.name = name;
        this.email = email;
        this.properties = properties;
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        this.properties = new HashMap<>();
    }

    public User() {
        this.properties = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
