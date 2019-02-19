/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.simonemele.takeyourplace.controller;

import it.simonemele.takeyourplace.classes.Jwt;
import it.simonemele.takeyourplace.classes.Promotion;
import it.simonemele.takeyourplace.exceptions.InvalidPromotionException;
import it.simonemele.takeyourplace.exceptions.ManagerException;
import it.simonemele.takeyourplace.exceptions.NotLoggedInException;
import it.simonemele.takeyourplace.exceptions.PromotionExpiredException;
import it.simonemele.takeyourplace.exceptions.PromotionFullException;
import it.simonemele.takeyourplace.exceptions.PromotionUsedException;
import it.simonemele.takeyourplace.exceptions.UnauthorizedException;
import it.simonemele.takeyourplace.model.PromotionsModel;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author Simone
 */
public class PromotionsController extends GeneralController{

    public PromotionsController() {
    }

    public PromotionsController(UriInfo uri) {
        super(uri);
    }
    
    public Promotion getPromotion(String jwt, long id) throws 
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
        
        PromotionsModel promotionsModel = new PromotionsModel();
        promotionsModel.connect();
        
        Promotion p = promotionsModel.getPromotion(id);
        promotionsModel.close();
        
        return p;
    }
    
    public ArrayList<Promotion> getByPlace(String jwt, long placeId) throws 
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
        
        ArrayList<Promotion> promotions;
        
        PromotionsModel promotionsModel = new PromotionsModel();
        promotionsModel.connect();
        
        promotions = promotionsModel.getByPlace(placeId);
        promotionsModel.close();
        
        return promotions;        
    }
    
    public ArrayList<Promotion> getByUser(String jwt, long userId) throws 
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
        
        ArrayList<Promotion> promotions;
        
        PromotionsModel promotionsModel = new PromotionsModel();
        promotionsModel.connect();
        
        promotions = promotionsModel.getByUser(userId);
        promotionsModel.close();
        
        return promotions;        
    }
    
    public void createPromotion(String jwt, Promotion promotion) throws 
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException,
            NotLoggedInException,
            UnauthorizedException
    {
        if(!super.checkToken(jwt))
            throw new  NotLoggedInException();
        
        PlacesController pC = new PlacesController();
        long manager = pC.getPlaceManager(promotion.getPlaceId());
        if(manager != Jwt.getUserId(jwt))
            throw new UnauthorizedException();
        
        PromotionsModel promotionsModel = new PromotionsModel();
        promotionsModel.connect();
        
        promotionsModel.createPromotion(promotion);
        
        promotionsModel.close();
    }
    
    /*public void deletePromotion(String jwt, long promotionId) throws 
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException,
            NotLoggedInException,
            UnauthorizedException
    {
        if(!super.checkToken(jwt))
            throw new  NotLoggedInException();
        
        PlacesController pC = new PlacesController();
        long manager = pC.getPlaceManager(getPromotionPlaceId(promotionId));
        if(manager != Jwt.getJWTId(jwt))
            throw new UnauthorizedException();
        
        promotionsModel = new PromotionsModel();
        promotionsModel.connect();
        
        promotionsModel.deletePromotion(promotionId);
        promotionsModel.close();
    }*/
    
    public String giveUserPromotion(
            String jwt,
            long userId,
            long promotionId
    ) throws 
            IOException,
            InvalidPromotionException,
            NotLoggedInException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException,
            ManagerException,
            UnauthorizedException,
            PromotionExpiredException,
            PromotionFullException
    {
        if(!super.checkToken(jwt))
            throw new  NotLoggedInException();
        
        PromotionsModel promotionsModel = new PromotionsModel();
        promotionsModel.connect();
        
        Promotion promotion = promotionsModel.getPromotion(promotionId);
        if(promotion == null)
            throw new InvalidPromotionException();
        
        PlacesController placeController = new PlacesController();
        long manager = placeController.getPlaceManager(promotion.getPlaceId());
        
        if(manager == userId)
            throw new ManagerException();
        
        if(promotionsModel.isAlreadyTaken(userId, promotionId))
            throw new UnauthorizedException();
        
        if(promotion.isEnded())
            throw new PromotionExpiredException();
        
        if(promotion.isFull())
            throw new PromotionFullException();
        
        String code = getRandomCode();
        promotionsModel.incrementGivenPromotion(promotionId);
        long proGivenId = promotionsModel.givePromotion(promotionId, userId, code);
        
        promotionsModel.close();
        return super.uri.getBaseUri().toString() + "promotions/use/" + proGivenId + "/" + code;
    }
    
    public String getPromotionLink(
            String jwt,
            long promotionId
    ) throws 
            IOException,
            InvalidPromotionException,
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
        
        PromotionsModel promotionsModel = new PromotionsModel();
        promotionsModel.connect();
        
        String endLink = promotionsModel.getPromotionGiven(userId, promotionId);
        if(endLink == null)
            throw new InvalidPromotionException();
        
        return super.uri.getBaseUri().toString() + "promotions/use/" + endLink;       
    }
    
    public Promotion usePromotion(String jwt, long id, String code) throws 
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException,
            NotLoggedInException,
            InvalidPromotionException,
            UnauthorizedException,
            PromotionExpiredException,
            ManagerException,
            PromotionUsedException
    {
        long promotionId;
        Promotion p;        
               
        if(!super.checkToken(jwt))
            throw new  NotLoggedInException();
        
        promotionId = this.getPromotionId(id);
        if(promotionId == -1)
            throw new InvalidPromotionException();
        
        PlacesController pC = new PlacesController();
        long manager = pC.getPlaceManager(this.getPromotionPlaceId(promotionId));
        
        if(manager != Jwt.getUserId(jwt))
            throw new ManagerException();
        
        if(!this.checkCode(id, code))
            throw new UnauthorizedException();
        
        PromotionsModel promotionsModel = new PromotionsModel();
        promotionsModel.connect();
        
        p = promotionsModel.getPromotion(promotionId);
        if(p == null)
            throw new InvalidPromotionException();
        
        if(promotionsModel.isUsed(id))
            throw new PromotionUsedException();
                 
        if(p.isEnded())
            throw new PromotionExpiredException();
        
        promotionsModel.usePromotion(id);
        promotionsModel.close();
        
        return p;
    }
    
    
    
    public long getPromotionPlaceId(long promotionId) throws 
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException
    {
        PromotionsModel pM = new PromotionsModel();
        pM.connect();
        
        return pM.getPromotionPlaceId(promotionId);
    }
    
    public long getPromotionId(long promGivenId) throws 
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException
    {
        PromotionsModel promotionsModel = new PromotionsModel();
        promotionsModel.connect();
        
        long promotionId = promotionsModel.getPromotionId(promGivenId);
        promotionsModel.close();
        
        return promotionId;
    }
    
    public boolean isUsed(long promGivenId) throws 
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException
    {
        PromotionsModel promotionsModel = new PromotionsModel();
        promotionsModel.connect();
        boolean isUsed = promotionsModel.isUsed(promGivenId);
        
        promotionsModel.close();
        
        return isUsed;
    }
    
    public boolean checkCode(long promGivenId, String code) throws 
            IOException,
            FileNotFoundException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException
    {
        PromotionsModel promotionsModel = new PromotionsModel();
        promotionsModel.connect();
        boolean isValid = promotionsModel.checkCode(promGivenId, code);
        promotionsModel.close();
        
        return isValid;
    }
    
    public boolean isTaken(String jwt, long promotionId) throws 
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
        
        long userId = Jwt.getUserId(jwt);
        
        PromotionsModel promotionsModel = new PromotionsModel();
        promotionsModel.connect();
        
        boolean isTaken = promotionsModel.isAlreadyTaken(userId, promotionId);
        promotionsModel.close();
        
        return isTaken;
    }
}
