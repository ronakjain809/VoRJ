package com.example.vorj.Models;

public class User {
    public String name,email,password,userid;

   public  User()
   {

   }
    public User(String name,String email,String password,String userdid){
        this.name=name;
        this.email=email;
        this.password=password;
        this.userid=userid;
    }



    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUserid() {
        return userid;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
