package com.petadopt.model;

public class Adopter extends User {
    private static final long serialVersionUID = 1L;

    public Adopter() {
        super(Role.ADOPTER);
    }

    @Override
    public String getDashboardPath() {
        return "/adopter/dashboard";
    }

    @Override
    public String getRoleDescription() {
        return "Find your new best friend, apply and track your applications.";
    }
}
