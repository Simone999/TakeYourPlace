/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.simonemele.takeyourplace.view;


import it.simonemele.takeyourplace.classes.User;
import it.simonemele.takeyourplace.classes.UsernameAndPassword;
import it.simonemele.takeyourplace.controller.AuthController;
import it.simonemele.takeyourplace.exceptions.NotActivatedException;
import it.simonemele.takeyourplace.exceptions.NotLoggedInException;
import it.simonemele.takeyourplace.exceptions.UnauthorizedException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import javax.mail.MessagingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * REST Web Service
 *
 * @author Simone
 */
@Path("auth")
public class Auth extends GeneralView{

    @Context
    UriInfo context;
    private AuthController controller;

    /**
     * Creates a new instance of Auth
     */
    public Auth() {
    }   
    
    @POST
    @Path("login/{clientId}")
    @Produces (MediaType.APPLICATION_JSON)
    @Consumes (MediaType.APPLICATION_JSON)
    public Response login(
            @PathParam("clientId") String clientId,
            UsernameAndPassword usernameAndPassword
    ) throws
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException
    {
        
        controller = new AuthController();
        User user;
        String username = usernameAndPassword.getUsername();
        String password = usernameAndPassword.getPassword();
                         
        try{
            user = controller.login(clientId, username, password);
        }catch(UnauthorizedException e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }catch(NotActivatedException e){
            return Response.status(USER_NOT_ACTIVATED).build();
        }
        
        return Response.ok(user).build();        
    }
    
    @POST
    @Path("check")
    @Produces(MediaType.TEXT_PLAIN)
    public Response checkToken(String token) throws 
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException
    {
        controller = new AuthController();
        return Response.ok(controller.checkToken(token)).build();
    }
    
    @POST
    @Path("psw")
    public Response checkPassword(
            @HeaderParam("Authorization") String jwt,
            String password
    ) throws 
            ClassNotFoundException,
            SQLException,
            IOException,
            FileNotFoundException,
            InstantiationException,
            IllegalAccessException
    {
        boolean isEqual;
        controller = new AuthController();
        try {
            isEqual = controller.checkPassword(jwt, password);
        } catch (NotLoggedInException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        
        return Response.ok(isEqual).build();
    }
    
    @GET
    @Path("confirm/{activationId}/{activationCode}")
    @Produces(MediaType.TEXT_HTML)
    public String confirmEmail(
            @PathParam("activationId") int activationId,
            @PathParam("activationCode") String activationCode
    ) throws 
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException
    {
        controller = new AuthController();
        try{
            controller.confirmEmail(activationId, activationCode);
        }catch(UnauthorizedException e){
            return this.showUnauthorizedErrorMessage();
        }
        
        return this.showAccountActivatedMessage();        
    }
    
    @GET
    @Path("changeEmail/{activationId}/{activationCode}")
    @Produces(MediaType.TEXT_HTML)
    public String changeEmail(
            @PathParam("activationId") int activationId,
            @PathParam("activationCode") String activationCode
    ) throws 
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException
    {
        controller = new AuthController(this.context);
        try{
            controller.changeEmail(activationId, activationCode);
        }catch(UnauthorizedException e){
            return this.showUnauthorizedErrorMessage();
        }catch(MessagingException e){
            return this.showErrorEmailMessage();
        }
        
        return this.showEmailSentMessage();        
    }
    
    @GET
    @Path("changePsw/{activationId}/{activationCode}")
    @Produces(MediaType.TEXT_HTML)
    public String changePassword(
            @PathParam("activationId") int activationId,
            @PathParam("activationCode") String activationCode
    ) throws 
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            IOException,
            InstantiationException,
            IllegalAccessException
    {
        controller = new AuthController();
        try{
            controller.changePassword(activationId, activationCode);
        }catch(UnauthorizedException e){
            return this.showUnauthorizedErrorMessage();
        }
        
        return this.showPasswordChangedMessage();        
    }
    
    @GET
    @Path("reset/{email}")
    public Response resetPassword(
            @PathParam("email") String email
    ) throws 
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException
    {
        controller = new AuthController(this.context);
        try{
            controller.resetPassword(email);
        }catch(UnauthorizedException e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }catch(MessagingException e){
            return Response.status(MAIL_NOT_SENT).build();
        }
        
        return Response.ok().build();
    }
    
    @GET
    @Path("delete/{activationId}/{activationCode}")
    @Produces(MediaType.TEXT_HTML)
    public String closeAccount(
            @PathParam("activationId") int activationId,
            @PathParam("activationCode") String activationCode
    ) throws 
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            IOException,
            InstantiationException,
            IllegalAccessException
    {
        controller = new AuthController();
        try{
            controller.deleteUser(activationId, activationCode);
        }catch(UnauthorizedException e){
            return this.showUnauthorizedErrorMessage();
        }
        
        return this.showAccountClosedMessage();        
    }    
    
    
    @Produces(MediaType.TEXT_HTML)
    private String showUnauthorizedErrorMessage(){
        return "<h1>Action not allowed!<h1>";
    }
    
    @Produces(MediaType.TEXT_HTML)
    private String showAccountActivatedMessage(){
        return "<h1>Your email is confirmed and your account is activated!</h1>";
    }
    
    @Produces(MediaType.TEXT_HTML)
    private String showErrorEmailMessage(){
        return "<h1>An error occurred while we were sending confirmation email"
                + " to the new email address</h1>";
    }
    
    @Produces(MediaType.TEXT_HTML)
    private String showEmailSentMessage(){
        return "<h1>A new email is sent to your new email address. "
                + "Check your mailbox!</h1>";
    }
    
    @Produces(MediaType.TEXT_HTML)
    private String showPasswordChangedMessage(){
        return "<h1>Password successfully changed!</h1>";
    }
    
    @Produces(MediaType.TEXT_HTML)
    private String showAccountClosedMessage(){
        return "<h1>Your account is permanently closed!</h1>";
    }
}
