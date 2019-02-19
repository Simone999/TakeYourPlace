/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.simonemele.takeyourplace.controller;

import com.auth0.jwt.exceptions.JWTDecodeException;
import it.simonemele.takeyourplace.classes.Jwt;
import it.simonemele.takeyourplace.classes.User;
import it.simonemele.takeyourplace.model.Database;
import it.simonemele.takeyourplace.model.UsersModel;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import javax.ws.rs.core.UriInfo;
import org.apache.commons.lang3.RandomStringUtils;

/**
 *
 * @author Simone
 */
public class GeneralController {
    
    protected UriInfo uri;
    public static final int TOKEN_ERROR = 0;

    public GeneralController() {
    }

    public GeneralController(UriInfo uri) {
        this.uri = uri;
    }    
    
    
    public boolean checkToken(String token) throws 
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException
    {
        Database db = new Database();
        db.connect();
        
        try{            
            long jwtId = Jwt.getJWTId(token);
            Jwt jwt = db.getToken(jwtId);
            
            return jwt.verify(token);
            
        }catch(NullPointerException | JWTDecodeException npe){
            return false;
        }finally{
            db.close();
        }
    }
    
    public void deleteJwtDifferentFrom(String jwt) throws 
            SQLException,
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            InstantiationException,
            IllegalAccessException
    {
        long jwtId = Jwt.getJWTId(jwt);
        long userId = Jwt.getUserId(jwt);
        Database db = new Database();
        db.connect();
        db.deleteJwtDifferentFrom(jwtId, userId);
        db.close();
    }
    
    public void deleteJwtDifferentFrom(long jwtId, long userId) throws 
            SQLException,
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            InstantiationException,
            IllegalAccessException
    {
        Database db = new Database();
        db.connect();
        db.deleteJwtDifferentFrom(jwtId, userId);
        db.close();
    }
    
    public void deleteUserJwt(long userId) throws 
            SQLException,
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            InstantiationException,
            IllegalAccessException
    {
        Database db = new Database();
        db.connect();
        db.deleteUserJwt(userId);
        db.close();
    }
    
    public void deleteUserJwt(String email) throws 
            SQLException,
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            InstantiationException,
            IllegalAccessException
    {
        Database db = new Database();
        db.connect();
        long userId = this.getUserByEmail(email).getId();
        db.deleteUserJwt(userId);
        db.close();
    }
    
    public User getUserByEmail(String email) throws 
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException
    {
        Database db = new Database();
        db.connect();
        User u = db.getUserByEmail(email);    
        db.close();
        return u;
    }
           
    protected String getRandomCode(){
        return RandomStringUtils.randomAlphanumeric(20);
    }
}
