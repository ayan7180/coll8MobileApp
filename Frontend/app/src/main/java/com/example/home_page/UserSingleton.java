package com.example.home_page;

public class UserSingleton {

    private static volatile UserSingleton instance;
    private int userId;
    private String username;
    private String userType;
    private String password;


    private UserSingleton() {}

    public static synchronized UserSingleton getInstance() {
        if (instance == null) {
            instance = new UserSingleton();
        }
        return instance;
    }

    //setters
    public void setUserId(int userId){
        this.userId = userId;
    }
    public void setUsername(String username){
        this.username = username;
    }

    public void setUserType(String userType){this.userType = userType;}
    public void setPassword(String password){this.password = password;}



    //getters
    public int getUserId(){
        return userId;
    }
    public String getUsername(){
        return username;
    }
    public String getUserType(){return userType;}
    public String getPassword(){return password;}

}
