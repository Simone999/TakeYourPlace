/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.simonemele.takeyourplace.model;

import it.simonemele.takeyourplace.classes.User;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Simone
 */
public class UsersModel extends Database{
    
    public User getUser(long id) throws SQLException{
        User u = new User();
        query = "SELECT * "
                + "FROM users "
                + "WHERE id = ?";
        stmt = conn.prepareStatement(query);
        stmt.setLong(1, id);
        rs = stmt.executeQuery();
        
        if(rs.next())
           u = this.buildUser(rs);
            
        return u;
    }
    
    public long createUser(User user, String password) 
            throws SQLException
    {
        query = "INSERT INTO users(name, surname, district, "
                + "county, country, email, password) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?)";
        
        stmt = conn.prepareStatement(
                query,
                PreparedStatement.RETURN_GENERATED_KEYS
        );
        stmt.setString(1, user.getName());
        stmt.setString(2, user.getSurname());
        stmt.setString(3, user.getDistrict());
        stmt.setString(4, user.getCounty());
        stmt.setString(5, user.getCountry());
        stmt.setString(6, user.getEmail());
        stmt.setString(7, password);
        stmt.executeUpdate();
        
        return getLastLongId(stmt.getGeneratedKeys());
    }
    
    public int insertUserActivationDelete(String code, long userId) throws SQLException{
        query = "INSERT INTO users_activations(code, action, user_id) "
                + "VALUES(?, ?, ?)";
        stmt = conn.prepareStatement(
                query,
                PreparedStatement.RETURN_GENERATED_KEYS
        );
        stmt.setString(1, code);
        stmt.setString(2, "deleteUser");
        stmt.setLong(3, userId);
        stmt.executeUpdate();
        
        return getLastIntId(stmt.getGeneratedKeys());
    }
    
    public void deleteUser(long id) throws SQLException{
        query = "DELETE FROM users "
                + "WHERE id = ?";
        stmt = conn.prepareStatement(query);        
        stmt.setLong(1, id);
        stmt.executeUpdate();
    }
    
    public void updateUser(User user) throws SQLException{
        query = "UPDATE users "
                + "SET name = ?, surname = ?, "
                + "district = ?, county = ?, country = ? "
                + "WHERE id = ?";
        
        stmt = conn.prepareStatement(query);
        stmt.setString(1, user.getName());        
        stmt.setString(2, user.getSurname());
        stmt.setString(3, user.getDistrict());
        stmt.setString(4, user.getCounty());
        stmt.setString(5, user.getCountry());  
        stmt.setLong(6, user.getId());
        stmt.executeUpdate();
    }
    
    public void setManager(long id, boolean manager) throws SQLException{
        query = "UPDATE users "
                + "SET manager = ? "
                + "WHERE id = ?";
        
        stmt = conn.prepareStatement(query);
        stmt.setBoolean(1, manager);
        stmt.setLong(2, id);
        stmt.executeUpdate();
    }
    
    public String getEmail(long id) throws SQLException{
        query = "SELECT email "
                + "FROM users "
                + "WHERE id = ?";
        stmt = conn.prepareStatement(query);
        stmt.setLong(1, id);
        rs = stmt.executeQuery();
        if(!rs.next())
            return null;
        return rs.getString("email");
    }
    
    public int insertUserActivationPassword(
            long userId,
            String password,
            String code ) throws SQLException {
        
        query = "INSERT INTO users_activations(code, password, action, user_id) "
                + "VALUES(?, ?, ?, ?)";
        stmt = conn.prepareStatement(
                query,
                PreparedStatement.RETURN_GENERATED_KEYS
        );
        stmt.setString(1, code);
        stmt.setString(2, password);
        stmt.setString(3, "changePsw");
        stmt.setLong(4, userId);
        stmt.executeUpdate();
        
        return getLastIntId(stmt.getGeneratedKeys());
    }
    
    public boolean isManager(long userId) throws SQLException{
        query = "SELECT manager FROM users WHERE id = ?";
        stmt = conn.prepareStatement(query);
        stmt.setLong(1, userId);
        rs = stmt.executeQuery();
        rs.next();
        return rs.getBoolean("manager");
    }
    
    public boolean exists(String email) throws SQLException{
        query = "SELECT id FROM users WHERE email = ?";
        stmt = conn.prepareStatement(query);
        stmt.setString(1, email);
        rs = stmt.executeQuery();
        return rs.next();
    }
    
}
