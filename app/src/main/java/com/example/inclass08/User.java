//Assignment Inclass 08
//File Name: Group12_InClass08
//Sanika Pol
//Snehal Kekane

package com.example.inclass08;

import android.os.Parcelable;

import java.io.Serializable;

public class User implements Serializable {
    String fname,lname;
    int id;

    public User() {
    }

    @Override
    public String toString() {
        return fname + " " + lname;
    }
}
