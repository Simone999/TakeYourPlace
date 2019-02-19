/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.simonemele.takeyourplace.controller;

import it.simonemele.takeyourplace.classes.Coords;
import it.simonemele.takeyourplace.classes.Jwt;
import it.simonemele.takeyourplace.classes.Place;
import it.simonemele.takeyourplace.exceptions.NotLoggedInException;
import it.simonemele.takeyourplace.exceptions.UnauthorizedException;
import it.simonemele.takeyourplace.model.PlacesModel;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Simone
 */
public class PlacesController extends GeneralController{
    
    public PlacesController() {
    }
    
    
    public ArrayList<Place> getPlaces(
            String jwt,
            Coords coords,
            double maxDistance
    ) throws 
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException, 
            InstantiationException,
            IllegalAccessException,
            NotLoggedInException
    {
        if(!super.checkToken(jwt))
            throw new  NotLoggedInException();
        
        PlacesModel placesModel = new PlacesModel();
        placesModel.connect();
        
        ArrayList<Place> places = placesModel.getPlaces(coords, maxDistance);
        placesModel.close();
        
        return places;
    }
    
    public ArrayList<Place> getUserPlaces(String jwt, long userId) throws 
            NotLoggedInException,
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException
    {
        if(!super.checkToken(jwt))
            throw new  NotLoggedInException();
        
        PlacesModel placesModel = new PlacesModel();
        placesModel.connect();
        
        ArrayList<Place> places = placesModel.getUserPlaces(userId);
        placesModel.close();
        
        return places;
    }
    
    public Place getPlace(String jwt, long id) throws 
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
        
        PlacesModel placesModel = new PlacesModel();
        placesModel.connect();
        
        Place place = placesModel.getPlace(id);
        placesModel.close();
        
        return place;
    }
    
    public void createPlace(String jwt, Place place) throws 
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException,
            UnauthorizedException,
            NotLoggedInException
    {
        if(!super.checkToken(jwt))
            throw new  NotLoggedInException();
        
        UsersController uC = new UsersController();
        if(!uC.isManager(place.getManagerId()))
            throw new UnauthorizedException();
        
        PlacesModel placesModel = new PlacesModel();
        placesModel.connect();
        
        placesModel.createPlace(place);
        
        placesModel.close();        
    }
    
    public void deletePlace(String jwt, long id) throws 
            IOException,
            FileNotFoundException,
            NotLoggedInException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException,
            UnauthorizedException
    {
        if(!super.checkToken(jwt))
            throw new  NotLoggedInException();
        
        PlacesModel placesModel = new PlacesModel();
        placesModel.connect();
        
        long placeManager = getPlaceManager(id);
        if(placeManager ==-1 || placeManager != Jwt.getUserId(jwt))
            throw new UnauthorizedException();
        
        placesModel.deletePlace(id);
        placesModel.close();
    }
    
    public void updatePlace(String jwt, Place place) throws 
            NotLoggedInException,
            UnauthorizedException,
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException
    {
        if(!super.checkToken(jwt))
            throw new  NotLoggedInException();
        
        PlacesModel placesModel = new PlacesModel();
        placesModel.connect();
        
        long placeManager = getPlaceManager(place.getId());
        if(placeManager ==-1 || placeManager != Jwt.getUserId(jwt))
            throw new UnauthorizedException();
        
        placesModel.updatePlace(place);
        placesModel.close();
    }
    
    
    public long getPlaceManager(long id) throws 
            SQLException,
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            InstantiationException,
            IllegalAccessException
    {   
        PlacesModel placesModel = new PlacesModel();
        placesModel.connect();
        long placeManager = placesModel.getPlaceManager(id);
        placesModel.close();
        return placeManager;
    }
}
