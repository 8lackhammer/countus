package com.techupstudio.countus.Models;

import java.util.HashMap;
import java.util.Map;

public class Person {

    private String firstName, middleName, lastName, gender, phone, address, displayImageUri;
    private int age;
    private Map<String, Object> properties;

    public Person(String firstName, String middleName, String lastName, String gender, String phone,
                  String address, String displayImageUri, int age, Map<String, Object> properties) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.gender = gender;
        this.phone = phone;
        this.address = address;
        this.age = age;
        this.displayImageUri = displayImageUri;
        this.properties = properties;
    }

    public Person(String firstName, String middleName, String lastName, String gender, String phone,
                  String address, String displayImageUri, int age) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.gender = gender;
        this.phone = phone;
        this.address = address;
        this.age = age;
        this.displayImageUri = displayImageUri;
        this.properties = new HashMap<>();
    }

    public Person() {
        this.properties = new HashMap<>();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDisplayImageUri() {
        return displayImageUri;
    }

    public void setDisplayImageUri(String displayImageUri) {
        this.displayImageUri = displayImageUri;
    }


    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
