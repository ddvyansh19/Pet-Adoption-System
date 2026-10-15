package com.petadopt.model;

public class Shelter extends User {
    private static final long serialVersionUID = 1L;

    public Shelter() {
        super(Role.SHELTER);
    }

    @Override
    public String getDashboardPath() {
        return "/shelter/dashboard";
    }

    @Override
    public String getRoleDescription() {
        return "List pets, review adoption applications and talk to adopters.";
    }
}
