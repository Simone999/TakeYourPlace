/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.simonemele.takeyourplace.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.simonemele.takeyourplace.classes.Hash;
import it.simonemele.takeyourplace.classes.Jwt;
import it.simonemele.takeyourplace.classes.MailSender;
import it.simonemele.takeyourplace.classes.User;
import it.simonemele.takeyourplace.classes.UserAndPassword;
import it.simonemele.takeyourplace.exceptions.NotActivatedException;
import it.simonemele.takeyourplace.exceptions.NotLoggedInException;
import it.simonemele.takeyourplace.exceptions.UnauthorizedException;
import it.simonemele.takeyourplace.model.AuthModel;
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
public class AuthController extends GeneralController{
        
    public AuthController() {
    }

    public AuthController(UriInfo uri) {
        super(uri);
    }
    
    public User login(String clientId, String username, String password) throws 
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException,
            UnauthorizedException,
            NotActivatedException
    {   
        UserAndPassword userAndPassword;
        User user;
        String savedPassword;
        AuthModel authModel = new AuthModel();
        authModel.connect();
        userAndPassword = authModel.login(username);
        authModel.close();
        
        if(userAndPassword == null)
            throw new UnauthorizedException();
        
        user = userAndPassword.getUser();
        savedPassword = userAndPassword.getPassword();
        
        if(savedPassword == null || !Hash.verify(savedPassword, password))
            throw new UnauthorizedException();        
        
        if(!user.isActivated())
            throw new NotActivatedException();
        
        user.setToken(this.createToken(clientId, user.getId()));
        return user;
    }
    
    public void confirmEmail(int activationId, String activationCode) throws 
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException,
            UnauthorizedException
    {
        AuthModel authModel = new AuthModel();
        authModel.connect();
        String json = authModel.checkEmailAction(
                activationId,
                activationCode,
                "confirm");
        
        if(json == null)
            throw new UnauthorizedException();
        
        
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);
        long userId = node.get("userId").asLong();
        String email = node.get("email").asText();
                
        authModel.activateAccount(email, userId);
        
        authModel.deleteUsersActivations(email, "confirm");
        
        authModel.close();
    }
    
    public void changeEmail(int activationId, String activationCode) throws
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException,
            UnauthorizedException,
            MessagingException
    {
        AuthModel authModel = new AuthModel();
        authModel.connect();
        String json = authModel.checkEmailAction(
                activationId,
                activationCode,
                "changeEmail");
        
        if(json == null)
            throw new UnauthorizedException();
        
        
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);
        long userId = node.get("userId").asLong();
        String email = node.get("email").asText();
        
        authModel.deactivateAccount(userId);
        
        String code = getRandomCode();
        authModel.deleteUsersActivations(email, "changeEmail");
        activationId = authModel.insertUserActivationMail(userId, email, code, "confirm");
        authModel.updateUserEmail(userId, email);
        super.deleteUserJwt(userId);
        authModel.close();
        
        MailSender mailSender = new MailSender(uri.getBaseUri());
        mailSender.send(
                email,
                activationId,
                code,
                MailSender.CONFIRM_EMAIL
        );
    }
    
    public void changePassword(int activationId, String activationCode) throws            
            SQLException,
            ClassNotFoundException,
            IOException,
            FileNotFoundException,
            InstantiationException,
            IllegalAccessException,
            UnauthorizedException
            
    {
        AuthModel authModel = new AuthModel();
        authModel.connect();
        String json = authModel.checkPasswordAction(
                activationId,
                activationCode);
        
        if(json == null)
            throw new UnauthorizedException();
        
        
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);
        long userId = node.get("userId").asLong();
        String psw = node.get("psw").asText();
        
        
        authModel.changeUserPassword(userId, psw);
        authModel.deleteUsersActivations(activationId);
        super.deleteUserJwt(userId);
        
        authModel.close();
    }
    
    public void deleteUser(int activationId, String activationCode) throws            
            SQLException,
            ClassNotFoundException,
            IOException,
            FileNotFoundException,
            InstantiationException,
            IllegalAccessException,
            UnauthorizedException
            
    {
        AuthModel authModel = new AuthModel();
        authModel.connect();
        long userId = authModel.checkDeleteUserAction(
                activationId,
                activationCode);
        
        if(userId == -1)
            throw new UnauthorizedException();
        
        
        authModel.deleteUser(userId);        
        authModel.close();
    }
    
    public boolean checkPassword(String jwt, String password) throws 
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException,
            NotLoggedInException
    {
        if(!super.checkToken(jwt))
            throw new  NotLoggedInException();
        
        long userId = Jwt.getUserId(jwt);
        
        AuthModel authModel = new AuthModel();
        authModel.connect();
        String storedPsw = authModel.getPassword(userId);
        authModel.close();
        
        return Hash.verify(storedPsw, password);
    }
    
    public void resetPassword(String email) throws 
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException,
            UnauthorizedException,
            MessagingException
    {
        UsersController uC = new UsersController();
        if(!uC.exists(email))
            throw new UnauthorizedException();
        
        AuthModel authModel = new AuthModel();
        authModel.connect();
        
        String clearPassword = this.getRandomCode();
        String codedPassword = Hash.hash(clearPassword);
        
        authModel.changeUserPassword(email, codedPassword);
        authModel.close();
        super.deleteUserJwt(email);
        
        MailSender mailSender = new MailSender(this.uri.getBaseUri());
        mailSender.sendResetPasswordEmail(email, clearPassword);
    }
    
    private String createToken(String clientId, long userId) throws 
            SQLException,
            IllegalArgumentException,
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            InstantiationException,
            IllegalAccessException
    {   
        AuthModel authModel = new AuthModel();
        authModel.connect();
        long jwtId = authModel.createEmptyToken(clientId, userId);
        Jwt jwt = new Jwt(jwtId, userId, clientId);
        String token = jwt.create();
        
        authModel.insertToken(jwtId, token);
        authModel.close();
        
        return token;
    }
    
}
