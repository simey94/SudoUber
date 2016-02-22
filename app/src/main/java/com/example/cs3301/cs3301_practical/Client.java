package com.example.cs3301.cs3301_practical;


public class Client {
    String name, username, password;
    int age, id;

    public Client(int id, String name, String username, String password, int age) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.age = age;
    }

    public Client(String name, String username, String password, int age){
        this.name = name;
        this.username = username;
        this.password = password;
        this.age = age;
    }

    public Client(String username, String password){
        this.username = username;
        this.password = password;
        // when we don't receive age & name
        this.age = -1;
        this.name = "";
    }
}
