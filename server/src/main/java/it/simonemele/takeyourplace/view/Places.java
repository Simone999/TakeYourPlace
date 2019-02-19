/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.simonemele.takeyourplace.view;

import it.simonemele.takeyourplace.classes.Coords;
import it.simonemele.takeyourplace.classes.Place;
import it.simonemele.takeyourplace.controller.PlacesController;
import it.simonemele.takeyourplace.exceptions.NotLoggedInException;
import it.simonemele.takeyourplace.exceptions.UnauthorizedException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author Simone
 */
@Path("places")
public class Places extends GeneralView{

    private PlacesController controller;

    /**
     * Creates a new instance of Places
     */
    public Places() {
    }
    
    @GET
    @Path("@{lat},{lng},{maxDistance}m")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlaces(
            @HeaderParam("Authorization") String jwt,
            @PathParam("lat") double lat, 
            @PathParam("lng") double lng, 
            @PathParam("maxDistance") double maxDistance) throws Exception
    {
        controller = new PlacesController();
        List<Place> places;
        GenericEntity<List<Place>> placesResponse;
        
        try{
            places = controller.getPlaces(
                   jwt,
                   new Coords(lat, lng),
                   maxDistance
           );
            placesResponse = new GenericEntity<List<Place>>(places) {};
        }catch(NotLoggedInException e){
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        
        return Response.ok(placesResponse).build();
    }
    
    @GET
    @Path("user/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserPlaces(
            @HeaderParam("Authorization") String jwt,
            @PathParam("userId") long userId
    ) throws IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException
    {
        controller = new PlacesController();
        List<Place> places;
        GenericEntity<List<Place>> placesResponse;
        
        try{
            places = controller.getUserPlaces(jwt, userId);
            placesResponse = new GenericEntity<List<Place>>(places) {};
        }catch(NotLoggedInException e){
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        
        return Response.ok(placesResponse).build();
    }
    
    @GET
    @Path("{id}")    
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlace (
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
        
        controller = new PlacesController();
        Place place;
        
        try{
            place = controller.getPlace(jwt, id);
        }catch(NotLoggedInException e){
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        
        return Response.ok(place).build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(
            @HeaderParam("Authorization") String jwt,
            Place place
    ) throws 
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException
    {
        controller = new PlacesController();
        try{
            controller.createPlace(jwt, place);
        }catch(NotLoggedInException e){
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }catch(UnauthorizedException ex){
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
            IllegalAccessException
    {
        controller = new PlacesController();
        
        try{
            controller.deletePlace(jwt, id);
        }catch(NotLoggedInException e){
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }catch(UnauthorizedException ex){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        
        return Response.ok().build();
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update (
            @HeaderParam("Authorization") String jwt,
            Place place
    ) throws 
            IOException,
            FileNotFoundException, 
            ClassNotFoundException, 
            SQLException, 
            InstantiationException, 
            IllegalAccessException 
    {
        controller = new PlacesController();
        
        try{
            controller.updatePlace(jwt, place);
        }catch(NotLoggedInException e){
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }catch(UnauthorizedException ex){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        
        return Response.ok().build();
    }    
}