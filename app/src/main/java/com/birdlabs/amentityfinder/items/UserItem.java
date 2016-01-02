package com.birdlabs.amentityfinder.items;

/**
 * the user item
 * Created by bijoy on 1/2/16.
 */
public class UserItem {
    public Integer id;
    public String email;
    public String name;

    public UserItem(Integer id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }
}
