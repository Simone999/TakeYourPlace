/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.simonemele.takeyourplace.classes;



/**
 *
 * @author Simone
 */
public class UserAndPassword {
    private User user;
    private String password;

    public UserAndPassword(User user, String password){
        this.user = user;
        this.password = password;
    }

    public UserAndPassword() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserPassword{" + "user:" + user + ", password:" + password + '}';
    }
}
