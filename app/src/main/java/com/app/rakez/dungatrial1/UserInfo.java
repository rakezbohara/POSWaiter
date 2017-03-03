package com.app.rakez.dungatrial1;

/**
 * Created by RAKEZ on 2/15/2017.
 */
public class UserInfo {
    private String id;
    private String name;
    private String phoneno;
    private String address;
    private String username;
    private String role_id;

    public UserInfo(String address, String id, String name, String phoneno, String role_id, String username) {
        this.address = address;
        this.id = id;
        this.name = name;
        this.phoneno = phoneno;
        this.role_id = role_id;
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public String getRole_id() {
        return role_id;
    }

    public String getUsername() {
        return username;
    }
}
