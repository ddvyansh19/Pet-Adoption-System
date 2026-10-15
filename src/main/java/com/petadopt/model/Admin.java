package com.petadopt.model;

public class Admin extends User {
    private static final long serialVersionUID = 1L;

    public Admin() {
        super(Role.ADMIN);
    }

    @Override
    public String getDashboardPath() {
        return "/admin/dashboard";
    }

    @Override
    public String getRoleDescription() {
        return "Manage users, approve pet listings and configure the platform.";
    }
}
