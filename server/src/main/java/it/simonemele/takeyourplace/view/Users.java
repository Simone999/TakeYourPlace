/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.simonemele.takeyourplace.view;

import it.simonemele.takeyourplace.classes.User;
import it.simonemele.takeyourplace.classes.UserAndPassword;
import it.simonemele.takeyourplace.controller.UsersController;
import it.simonemele.takeyourplace.exceptions.NotLoggedInException;
import it.simonemele.takeyourplace.exceptions.UnauthorizedException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import javax.mail.MessagingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * REST Web Service
 *
 * @author Simone
 */
@Path("users")
public class Users extends GeneralView{

    @Context
    UriInfo context;
    private UsersController controller;
    

    /**
     * Creates a new instance of Users
     */
    public Users(){
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(
            @HeaderParam("Authorization") String jwt,
            @PathParam("id") long id
    ) throws 
            IOException,
            ClassNotFoundException,
            InstantiationException,
            FileNotFoundException,
            SQLException,
            IllegalAccessException
    {
        controller = new UsersController();
        User user;
        
        try{
            user = controller.getUser(jwt, id);
        }catch(NotLoggedInException e){
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        
        return Response.ok(user).build();
    }
    
    @GET
    @Path("exists/{email}")
    public Response exists(@PathParam("email") String email) throws 
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException
    {
        controller = new UsersController();
        return Response.ok(controller.exists(email)).build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(
            UserAndPassword userAndPassword
    ) throws 
            SQLException,
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            InstantiationException,
            IllegalAccessException
    {
        controller = new UsersController(this.context);
        try {
            controller.createUser(
                    userAndPassword.getUser(),
                    userAndPassword.getPassword()
            );
        }catch (MessagingException e) {
            return Response.status(MAIL_NOT_SENT).build();
        }catch(UnauthorizedException e){
            return Response.status(Response.Status.FORBIDDEN).build();
        } 
        
        return Response.ok().build();
    }
    
    
    @DELETE
    @Path("{id}")
    public Response delete (
            @HeaderParam("Authorization") String jwt,
            @PathParam("id") long id
    ) throws 
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException{
        
        controller = new UsersController(this.context);
        
        try{
            controller.deleteUser(jwt, id);
        }catch(NotLoggedInException e){
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }catch(UnauthorizedException e){
            return Response.status(Response.Status.FORBIDDEN).build();
        } catch (MessagingException ex) {
            return Response.status(MAIL_NOT_SENT).build();
        }
        
        return Response.ok().build();
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update (
            @HeaderParam("Authorization") String jwt,
            User user
    ) throws 
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException,
            IOException
    {
        controller = new UsersController();
        
        try{
            controller.updateUser(jwt, user);
        }catch(NotLoggedInException e){
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }catch(UnauthorizedException e){
            return Response.status(Response.Status.FORBIDDEN).build();
        } 
        
        return Response.ok().build();
    }
    
    @PUT
    @Path("{id}")
    public Response setManager (
            @HeaderParam("Authorization") String jwt,
            @PathParam("id") long id,
            boolean status
    ) throws 
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException
    {
        controller = new UsersController();
        
        try{
            controller.setManager(jwt, id, status);
        }catch(NotLoggedInException e){
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }catch(UnauthorizedException e){
            return Response.status(Response.Status.FORBIDDEN).build();
        } 
        
        return Response.ok().build();
    }
    
    @PUT
    @Path("{userId}/changeEmail")
    public Response changeEmail(
            @HeaderParam("Authorization") String jwt,
            @PathParam("userId") long userId,
            String newEmail
    )throws 
            IOException,
            FileNotFoundException, 
            ClassNotFoundException, 
            SQLException, 
            InstantiationException, 
            IllegalAccessException 
    {
        
        controller = new UsersController(this.context);
        
        try{
            controller.changeEmail(jwt, userId, newEmail);
        }catch(NotLoggedInException e){
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }catch(UnauthorizedException e){
            return Response.status(Response.Status.FORBIDDEN).build();
        } catch (MessagingException ex) {
            return Response.status(MAIL_NOT_SENT).build();
        }
        
        return Response.ok().build();
    }
    
    @PUT
    @Path("{userId}/changePassword")
    public Response changePassword(
            @HeaderParam("Authorization") String jwt,
            @PathParam("userId") long userId,
            String password
    ) 
            throws IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException
    {
        controller = new UsersController(this.context);
        
        try{
            controller.changePassword(jwt, userId, password);
        }catch(NotLoggedInException e){
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }catch(UnauthorizedException e){
            return Response.status(Response.Status.FORBIDDEN).build();
        } catch (MessagingException ex) {
            return Response.status(MAIL_NOT_SENT).build();
        }
        
        return Response.ok().build();
    }
}
