/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.simonemele.takeyourplace.classes;

import java.sql.Date;


/**
 *
 * @author Simone
 */
public class User {
    
    private long id;
    private String name;
    private String surname;
    private String email;
    private String district;
    private String county;
    private String country;
    private Date registrationDate;
    private boolean manager;
    private String token;
    private boolean activated;

    public User(long id,
            String name,
            String surname,
            String email,
            String district,
            String county,
            String country,
            Date registrationDate,
            boolean manager,
            String token,
            boolean activated) 
    {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.district = district;
        this.county = county;
        this.country = country;
        this.registrationDate = registrationDate;
        this.manager = manager;
        this.token = token;
        this.activated = activated;
    }

    public User() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public boolean isManager() {
        return manager;
    }

    public void setManager(boolean manager) {
        this.manager = manager;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", name=" + name + ", surname=" + surname + ", email=" + email + ", district=" + district + ", county=" + county + ", country=" + country + ", registrationDate=" + registrationDate + ", manager=" + manager + ", token=" + token + ", activated=" + activated + '}';
    }
    
    
    
}
