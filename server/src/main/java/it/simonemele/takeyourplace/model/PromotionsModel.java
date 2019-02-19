/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.simonemele.takeyourplace.model;

import com.mysql.jdbc.Statement;
import it.simonemele.takeyourplace.classes.Promotion;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Simone
 */
public class PromotionsModel extends Database{
    
    public Promotion getPromotion(long id) throws SQLException{
        query = "SELECT * "
                + "FROM promotions "
                + "WHERE id = ?";
        stmt = conn.prepareStatement(query);
        stmt.setLong(1, id);
        rs = stmt.executeQuery();
        
        if(!rs.next())
            return null;
        
        return this.buildPromotion(rs);
    }
    
    public ArrayList<Promotion> getByPlace(long placeId) throws SQLException{
        ArrayList<Promotion> promotions = new ArrayList<>();
        query = "SELECT * "
                + "FROM promotions "
                + "WHERE place_id = ? AND end > NOW()";
        stmt = conn.prepareStatement(query);
        stmt.setLong(1, placeId);
        rs = stmt.executeQuery();
        
        while(rs.next())
            promotions.add(buildPromotion(rs));
        
        return promotions;
    }
    
    public ArrayList<Promotion> getByUser(long userId) throws SQLException{
        ArrayList<Promotion> promotions = new ArrayList<>();
        query = "SELECT p.*, pg.used "
                + "FROM promotions p INNER JOIN promotions_given pg "
                + "ON p.id = pg.promotion_id "
                + "WHERE pg.used = ? AND pg.user_id = ?";
        stmt = conn.prepareStatement(query);
        stmt.setBoolean(1, false);
        stmt.setLong(2, userId);
        rs = stmt.executeQuery();
        
        while(rs.next())
            promotions.add(buildPromotion(rs));
        
        return promotions;
    }
    
    public void createPromotion(Promotion promotion) throws SQLException{
        query = "INSERT INTO promotions"
                + "(id, title, description, start, end, max, given, place_id) "
                + "VALUES( ?, ?, ?, ?, ?, ?, ?, ?)";
        
        System.out.println("start " + promotion.getStart() + " end " + promotion.getEnd());
        
        stmt = conn.prepareStatement(query);
        stmt.setLong(1, promotion.getId());
        stmt.setString(2, promotion.getTitle());
        stmt.setString(3, promotion.getDescription());
        stmt.setString(4, promotion.getStartDateTime());
        stmt.setString(5, promotion.getEndDateTime());
        stmt.setInt(6, promotion.getMax());
        stmt.setInt(7, promotion.getGiven());
        stmt.setLong(8, promotion.getPlaceId());
        
        stmt.executeUpdate();
    }
    
    /*public void deletePromotion(long id) throws SQLException{
        query = "DELETE FROM promotions WHERE id = ?";
        stmt = conn.prepareStatement(query);
        stmt.setLong(1, id);
        stmt.executeUpdate();
    }*/
    
    public long getPromotionPlaceId(long id) throws SQLException{
        query = "SELECT place_id FROM promotions WHERE id = ?";
        stmt = conn.prepareStatement(query);
        stmt.setLong(1, id);
        rs = stmt.executeQuery();
        
        rs.next();
        return rs.getLong("place_id");
    }
    
    public long getPromotionId(long promGivenId) throws SQLException{
        query = "SELECT promotion_id FROM promotions_given WHERE id = ?";
        stmt = conn.prepareStatement(query);
        stmt.setLong(1, promGivenId);
        rs = stmt.executeQuery();
        
        if(!rs.next())
            return -1;              
        
        return rs.getBigDecimal("promotion_id").longValue();
    }
    
    public boolean isUsed(long id) throws SQLException{
        query = "SELECT used FROM promotions_given WHERE id = ?";
        stmt = conn.prepareStatement(query);
        stmt.setLong(1, id);
        rs = stmt.executeQuery();
        rs.next();
        return rs.getBoolean("used");
    }
    
    public void usePromotion(long id) throws SQLException{
        query = "UPDATE promotions_given SET used = ? WHERE id = ?";
        stmt = conn.prepareStatement(query);
        stmt.setBoolean(1, true);
        stmt.setLong(2, id);
        stmt.executeUpdate();
    }
    
    public boolean checkCode(long id, String code) throws SQLException{
        query = "SELECT code "
                + "FROM promotions_given "
                + "WHERE id = ? AND code = ?";
        stmt = conn.prepareStatement(query);
        stmt.setLong(1, id);
        stmt.setString(2, code);
        rs = stmt.executeQuery();
        return rs.next();
    }
    
    public boolean isAlreadyTaken(long userId, long promotionId) 
            throws SQLException
    {
        query = "SELECT * "
                + "FROM promotions_given "
                + "WHERE user_id = ? AND promotion_id = ?";
        stmt = conn.prepareStatement(query);
        stmt.setLong(1, userId);
        stmt.setLong(2, promotionId);
        rs = stmt.executeQuery();
        return rs.next();
    }
    
    public boolean isAvailable(long promotionId) throws SQLException{
        query = "SELECT * "
                + "FROM promotions "
                + "WHERE id = ? AND NOW() < end AND given < max";
        stmt = conn.prepareStatement(query);
        stmt.setLong(1, promotionId);
        rs = stmt.executeQuery();
        return rs.next();
    }
    
    public void incrementGivenPromotion(long promotionId) throws SQLException{
        query = "UPDATE promotions SET given = given + 1 WHERE id = ?";
        stmt = conn.prepareStatement(query);
        stmt.setLong(1, promotionId);
        stmt.executeUpdate();
    }
    
    public long givePromotion(long promotionId, long userId, String code) throws SQLException{
        query = "INSERT INTO promotions_given(code, user_id, promotion_id) "
                + "VALUES(?, ?, ?)";
        stmt = conn.prepareStatement(
                query,
                PreparedStatement.RETURN_GENERATED_KEYS
        );
        stmt.setString(1, code);
        stmt.setLong(2, userId);
        stmt.setLong(3, promotionId);
        stmt.executeUpdate();
        return getLastLongId(stmt.getGeneratedKeys());
    }
    
    public String getPromotionGiven(long userId, long promotionId) 
            throws SQLException
    {
        query = "SELECT id, code "
                + "FROM promotions_given "
                + "WHERE user_id = ? AND promotion_id = ?";
        stmt = conn.prepareStatement(query);
        stmt.setLong(1, userId);
        stmt.setLong(2, promotionId);
        rs = stmt.executeQuery();
        
        if(!rs.next())
            return null;
        
        long id = rs.getBigDecimal("id").longValue();
        String code = rs.getString("code");
        
        return id + "/" + code;
    }
    
    private Promotion buildPromotion(ResultSet rs) throws SQLException{
        Promotion p = new Promotion();
        
        p.setId(rs.getLong("id"));
        p.setTitle(rs.getString("title"));
        p.setDescription(rs.getString("description"));
        p.setStartByTimestamp(rs.getTimestamp("start"));
        p.setEndtByTimestamp(rs.getTimestamp("end"));
        p.setMax(rs.getInt("max"));
        p.setGiven(rs.getInt("given"));
        p.setPlaceId(rs.getLong("place_id"));
        
        return p;
    }
    
}
