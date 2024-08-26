package com.licenta.healthapp.Requests;

public class SignUpRequest {
    private String username;
    private String password;
    private String email;
    private String gender;

    private double weight;

    public int getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(int targetWeight) {
        this.targetWeight = targetWeight;
    }

    private double height;

    private int targetWeight;

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public SignUpRequest(String username, String password, String email, String gender,double weight, double height,int targetWeight) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.gender = gender;
        this.height=height;
        this.weight=weight;
        this.targetWeight=targetWeight;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
