/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.simonemele.takeyourplace.controller;


import it.simonemele.takeyourplace.classes.Hash;
import it.simonemele.takeyourplace.classes.Jwt;
import it.simonemele.takeyourplace.classes.MailSender;
import it.simonemele.takeyourplace.classes.User;
import it.simonemele.takeyourplace.exceptions.NotLoggedInException;
import it.simonemele.takeyourplace.exceptions.UnauthorizedException;
import it.simonemele.takeyourplace.model.UsersModel;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import javax.mail.MessagingException;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author Simone
 */
public class UsersController extends GeneralController{
    
    public UsersController() {
    }
    
    public UsersController(UriInfo uri){
        super(uri);
    }
    
    public User getUser(String jwt, long id) throws
            IOException,
            ClassNotFoundException,
            InstantiationException,
            FileNotFoundException,
            SQLException,
            IllegalAccessException,
            NotLoggedInException
    {
        if(!super.checkToken(jwt))
            throw new  NotLoggedInException();
        
        UsersModel usersModel = new UsersModel();
        usersModel.connect();
        
        User user = usersModel.getUser(id);
        usersModel.close();
        
        return user;
    }
    
    public void createUser(User user, String password) throws 
            SQLException,
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            InstantiationException,
            IllegalAccessException,
            MessagingException,
            UnauthorizedException
    {
        String psw = Hash.hash(password);
        String activationCode = getRandomCode();
        String email = user.getEmail();
        
        UsersModel usersModel = new UsersModel();
        usersModel.connect();
        
        if(exists(email))
            throw new UnauthorizedException();
        
        long userId = usersModel.createUser(user, psw);
        int activationId = usersModel.insertUserActivationMail(
                userId, email,
                activationCode,
                "confirm"
        );
        
        MailSender mailSender = new MailSender(uri.getBaseUri());
        mailSender.send(email, activationId, activationCode, MailSender.SIGN_UP);
        usersModel.close();
    }
    
    public boolean exists(String email) throws 
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException
    {
        UsersModel usersModel  = new UsersModel();
        usersModel.connect();
        boolean exists = usersModel.exists(email);
        usersModel.close();
        
        return exists;
    }
    
    public void deleteUser(String jwt, long userId) throws 
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException,
            NotLoggedInException,
            UnauthorizedException,
            MessagingException
    {
        if(!checkToken(jwt))
            throw new NotLoggedInException();
        
        if(Jwt.getUserId(jwt) != userId)
            throw new UnauthorizedException();
        
        UsersModel usersModel = new UsersModel();
        usersModel.connect();
        
        String code = getRandomCode();
        long activationId = usersModel.insertUserActivationDelete(code, userId);
        String email = usersModel.getEmail(userId);
        
        usersModel.close();
        
        if(email == null)
            throw new MessagingException();        
        
        MailSender mailSender = new MailSender(uri.getBaseUri());
        mailSender.send(email, activationId, code, MailSender.DELETE_USER);
        
    }
    
    public void updateUser(String jwt, User user)throws
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException,
            NotLoggedInException,
            UnauthorizedException,
            IOException
    {
        if(!checkToken(jwt))
            throw new NotLoggedInException();
        
        if(Jwt.getUserId(jwt) != user.getId())
            throw new UnauthorizedException();
        
        UsersModel usersModel = new UsersModel();
        usersModel.connect();
        
        usersModel.updateUser(user);
        super.deleteJwtDifferentFrom(jwt);
        
        usersModel.close();
    }
    
    public void setManager(String jwt, long id, boolean status) throws 
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException,
            NotLoggedInException,
            UnauthorizedException
    {
        if(!checkToken(jwt))
            throw new NotLoggedInException();
        
        if(Jwt.getUserId(jwt) != id)
            throw new UnauthorizedException();
        
        UsersModel usersModel = new UsersModel();
        usersModel.connect();
        
        usersModel.setManager(id, status);
        
        usersModel.close();
    }
    
    public void changeEmail(String jwt, long id, String newEmail) throws 
            NotLoggedInException,
            UnauthorizedException,
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException,
            MessagingException
    {
        if(!checkToken(jwt))
            throw new NotLoggedInException();
        
        if(Jwt.getUserId(jwt) != id || exists(newEmail))
            throw new UnauthorizedException();
        
        UsersModel usersModel = new UsersModel();
        usersModel.connect();
        String code = getRandomCode();
        
        int activationId = usersModel.insertUserActivationMail(
                id,
                newEmail,
                code,
                "changeEmail"
        );
        
        String email = usersModel.getEmail(id);
        usersModel.close();
        
        if(email == null)
            throw new MessagingException();
        
        MailSender mailSender = new MailSender(uri.getBaseUri());
        mailSender.send(email, activationId, code, MailSender.CHANGE_MAIL);
    }
    
    public void changePassword(String jwt, long id, String password) throws 
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException,
            NotLoggedInException,
            UnauthorizedException,
            MessagingException
    {
        if(!checkToken(jwt))
            throw new NotLoggedInException();
        
        if(Jwt.getUserId(jwt) != id)
            throw new UnauthorizedException();
        
        
        String code = getRandomCode();
        String psw = Hash.hash(password);
        
        UsersModel usersModel = new UsersModel();
        usersModel.connect();
        
        int activationId;
        activationId = usersModel.insertUserActivationPassword(id, psw, code);        
        String email = usersModel.getEmail(id);
        
        usersModel.close();
        
        if(email == null)
            throw new MessagingException();
        
        MailSender mailSender = new MailSender(uri.getBaseUri());
        mailSender.send(email, activationId, code, MailSender.CHANGE_PASSWORD);
    }
    
    
    public boolean isManager(long id) throws 
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException
    {
        UsersModel usersModel = new UsersModel();
        usersModel.connect();
        boolean isManager = usersModel.isManager(id);
        usersModel.close();
        
        return isManager;
    }
}
