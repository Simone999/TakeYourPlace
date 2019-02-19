/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.simonemele.takeyourplace.view;

import it.simonemele.takeyourplace.classes.Promotion;
import it.simonemele.takeyourplace.controller.PromotionsController;
import it.simonemele.takeyourplace.exceptions.InvalidPromotionException;
import it.simonemele.takeyourplace.exceptions.ManagerException;
import it.simonemele.takeyourplace.exceptions.NotLoggedInException;
import it.simonemele.takeyourplace.exceptions.PromotionExpiredException;
import it.simonemele.takeyourplace.exceptions.PromotionFullException;
import it.simonemele.takeyourplace.exceptions.PromotionUsedException;
import it.simonemele.takeyourplace.exceptions.UnauthorizedException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * REST Web Service
 *
 * @author Simone
 */
@Path("promotions")
public class Promotions extends GeneralView{
    
    @Context
    UriInfo context;

    private PromotionsController controller;
    
    public Promotions() {
    }
    
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPromotion(
            @HeaderParam("Authorization") String jwt,
            @PathParam("id") long id
    ) throws 
            IOException,
            FileNotFoundException, 
            ClassNotFoundException, 
            SQLException, 
            InstantiationException, 
            IllegalAccessException 
    {
        Promotion promotion;
        controller = new PromotionsController();
        try{
            promotion = controller.getPromotion(jwt, id);
        }catch(NotLoggedInException e){
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        
        return Response.ok(promotion).build();
    }
    
    @GET
    @Path("place/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPromotionsByPlace(
            @HeaderParam("Authorization") String jwt,
            @PathParam("id") long placeId
    ) throws 
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException
    {   
        List<Promotion> promotions;
        GenericEntity<List<Promotion>> promotionsResponse;
        controller = new PromotionsController();
        try{
            promotions = controller.getByPlace(jwt, placeId);
            promotionsResponse = new GenericEntity<List<Promotion>>(promotions) {};
        }catch(NotLoggedInException e){
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        
        return Response.ok(promotionsResponse).build();
    }
    
    @GET
    @Path("user/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPromotionsByUser(
            @HeaderParam("Authorization") String jwt,
            @PathParam("userId") long userId
    ) throws 
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException
    {
        List<Promotion> promotions;
        GenericEntity<List<Promotion>> promotionsResponse;
        controller = new PromotionsController();
        try{
            promotions = controller.getByUser(jwt, userId);
            promotionsResponse = new GenericEntity<List<Promotion>>(promotions) {};
        }catch(NotLoggedInException e){
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        
        return Response.ok(promotionsResponse).build();
    }
    
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPromotion(
            @HeaderParam("Authorization") String jwt,
            Promotion promotion
    ) throws 
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException
    {
        controller = new PromotionsController();
        try{
            controller.createPromotion(jwt, promotion);
        }catch(NotLoggedInException e){
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }catch(UnauthorizedException ex){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        
        return Response.ok().build();
    }
    
    /*@DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePromotion(
            @HeaderParam("Authorization") String jwt,
            @PathParam("id") long id
    ) throws 
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException
    {
        controller = new PromotionsController();
        try{
            controller.deletePromotion(jwt, id);
        }catch(NotLoggedInException e){
            return Response.status(Response.Status.UNAUTHORIZED)
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        }catch(UnauthorizedException ex){
            return Response.status(Response.Status.FORBIDDEN)
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        }
        
        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }*/
    
    @GET
    @Path("{userId}/{promotionId}")
    public Response giveUserPromotion(
            @HeaderParam("Authorization") String jwt,
            @PathParam("userId") long userId,
            @PathParam("promotionId") long promotionId
    ) throws 
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException
    {
        controller = new PromotionsController(context);
        String link;
        try{
            link = controller.giveUserPromotion(jwt, userId, promotionId);
        }catch(NotLoggedInException e){
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }catch(UnauthorizedException ex){
            return Response.status(Response.Status.FORBIDDEN).build();
        }catch(PromotionExpiredException ex){
            return Response.status(PROMOTION_EXPIRED).build();
        } catch (InvalidPromotionException ex) {        
            return Response.status(INVALID_PROMOTION).build();
        } catch (ManagerException ex) {
            return Response.status(MANAGER_EXCEPTION).build();
        } catch (PromotionFullException ex) {
            return Response.status(PROMOTION_FULL).build();
        }
        return Response.ok(link).build();
    }
    
    @GET
    @Path("link/{promotionId}")
    public Response getPromotionLink(
            @HeaderParam("Authorization") String jwt,
            @PathParam("promotionId") long promotionId
    ) throws
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException
    {
        controller = new PromotionsController(context);
        String link;
        try{
            link = controller.getPromotionLink(jwt, promotionId);
        }catch(NotLoggedInException e){
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } catch (InvalidPromotionException ex) {        
            return Response.status(INVALID_PROMOTION).build();
        }
        return Response.ok(link).build();
    }
    
    @GET
    @Path("use/{id}/{code}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response usePromotion(
            @HeaderParam("Authorization") String managerJwt,
            @PathParam("id") long id, //promotions_given ID
            @PathParam("code") String code
    ) throws 
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException
    {
        Promotion p;
        controller = new PromotionsController();
        System.out.println("id " + id + "code " + code);
        try{
            p = controller.usePromotion(managerJwt, id, code);
        }catch(NotLoggedInException e){
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }catch(InvalidPromotionException e){
            return Response.status(INVALID_PROMOTION).build();
        }catch(UnauthorizedException ex){
            return Response.status(Response.Status.FORBIDDEN).build();
        }catch(PromotionExpiredException ex){
            return Response.status(PROMOTION_EXPIRED).build();
        } catch (ManagerException ex) {
            return Response.status(MANAGER_EXCEPTION).build();
        } catch (PromotionUsedException ex) {
            return Response.status(PROMOTION_USED).build();
        }
        
        return Response.ok(p).build();
    }
    
    @GET
    @Path("isTaken/{promotionId}")
    public Response isTaken(
            @HeaderParam("Authorization") String jwt,
            @PathParam("promotionId") long promotionId
    ) throws 
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException
    {
        controller = new PromotionsController();
        boolean isTaken;
        try{
            isTaken = controller.isTaken(jwt, promotionId);
        }catch(NotLoggedInException e){
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        
        return Response.ok(isTaken).build();
    }    
    
}
