package com.example.studienarbeit_geocaching_app_albert_hahn;

import androidx.annotation.NonNull;

/**
 * Class UserModel for sql queries and table data
 * includes all necessary parameter to define a user entry
 */

public class UserModel {

    /**
     * @value _id unique identifier of a user
     * @value name of the user
     * @value password of the user
     * @value level currentlevel of the user
     * @value experience current experience that has been gathered by the user
     */

    private int _id;
    private String name;
    private String password;
    private int level;
    private int experience;

    /**
     * constructer of the UserModel
     * @param _id unique identifier of a user
     * @param name of the user
     * @param password of the user
     * @param level currentlevel of the user
     * @param experience current experience that has been gathered by the user
     */

    public UserModel(int _id, String name, String password , int level, int experience) {
        this._id = _id;
        this.name = name;
        this.password = password;
        this.level = level;
        this.experience = experience;
    }

    /**
     * important to display the whole model as a string
     * @return string of UserModel
     */

    @NonNull
    @Override
    public String toString() {
        return "UserModel{" +
                "_id=" + _id +
                ", name=" + name +
                ", password=" + password +
                ", level=" + level +
                ", experience=" + experience +
                '}';
    }

    /**
     * getter of the UserModel
     */

    public int getid() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    /**
     * setter of the UserModel
     */

    public void setId(int _id) {
        this._id = _id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) { this.password = password; }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setExperience(int experience) { this.experience = experience; }

}
