package org.udesa.tp1.model;

import java.util.ArrayList;

public class User implements Identifiable {
    String username;
    String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public String id(){
        return username;
    }
    public String password() { return password; }
}
