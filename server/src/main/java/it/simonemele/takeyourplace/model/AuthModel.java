/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.simonemele.takeyourplace.model;


import it.simonemele.takeyourplace.classes.UserAndPassword;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Simone
 */
public class AuthModel extends Database{
    
    public UserAndPassword login(String email) throws SQLException{
        query = "SELECT * "
                + "FROM users "
                + "WHERE email = ?";
        stmt = conn.prepareStatement(query);
        stmt.setString(1, email);
        rs = stmt.executeQuery();
        
        
        if(!rs.next())
            return null;
        
        UserAndPassword userAndPassword = new UserAndPassword();
        userAndPassword.setUser(this.buildUser(rs));
        userAndPassword.setPassword(rs.getString("password"));
        
        return userAndPassword;
    }
    
    public long createEmptyToken(String clientId, long userId) throws SQLException{
        query = "INSERT INTO jwt(client_id, user_id) "
                + "VALUES(?, ?)";
        stmt = conn.prepareStatement(
                query, PreparedStatement.RETURN_GENERATED_KEYS
        );
        stmt.setString(1, clientId);
        stmt.setLong(2, userId);
        stmt.executeUpdate();        
        return getLastLongId(stmt.getGeneratedKeys());
    }
    
    public void insertToken(long jwtId, String token) throws SQLException{
        query = "UPDATE jwt SET token = ? WHERE id = ?";
        stmt = conn.prepareStatement(query);
        stmt.setString(1, token);
        stmt.setLong(2, jwtId);
        stmt.executeUpdate();
    }
    
    
    
    public void activateAccount(String email, long userId) throws SQLException{        
        query = "UPDATE users SET email = ?, activated = ? WHERE id = ?";
        stmt = conn.prepareStatement(query);
        stmt.setString(1, email);
        stmt.setBoolean(2, true);
        stmt.setLong(3, userId);
        stmt.executeUpdate();        
    }
    
    public void deactivateAccount(long userId) throws SQLException{
        query = "UPDATE users SET activated = ? WHERE id = ?";
        stmt = conn.prepareStatement(query);
        stmt.setBoolean(1, false);
        stmt.setLong(2, userId);
        stmt.executeUpdate();
    }
    
    public String checkEmailAction(int tableId, String code, String action)
            throws SQLException
    {
        query = "SELECT user_id, email "
                + "FROM users_activations "
                + "WHERE id = ? AND code = ? AND action = ?";
        
        stmt = conn.prepareStatement(query);
        stmt.setInt(1, tableId);
        stmt.setString(2, code);
        stmt.setString(3, action);
        rs = stmt.executeQuery();
        if(!rs.next())
            return null;
        
        String email = rs.getString("email");
        long userId = rs.getLong("user_id");
        return "{\"email\":\"" + email + "\",\"userId\":\"" + userId + "\"}";
    }
    
    public void deleteUsersActivations(int id) throws SQLException{
        query = "DELETE from users_activations "
                + "WHERE id = ?";
        
        stmt = conn.prepareStatement(query);
        stmt.setInt(1, id);
        stmt.executeUpdate();
    }
    
    public void deleteUsersActivations(String email, String action) throws SQLException{
        query = "DELETE from users_activations "
                + "WHERE email = ? AND action = ?";
        
        stmt = conn.prepareStatement(query);
        stmt.setString(1, email);
        stmt.setString(2, action);
        stmt.executeUpdate();
    }
    
    public String getPassword(long userId) throws SQLException{
        query = "SELECT password FROM users WHERE id = ?";
        stmt = conn.prepareStatement(query);
        stmt.setLong(1, userId);
        rs = stmt.executeQuery();
        
        if(!rs.next())  return null;
        
        return rs.getString("password");
    }
    
    public String checkPasswordAction(int tableId, String code)
            throws SQLException
    {
        query = "SELECT user_id, password "
                + "FROM users_activations "
                + "WHERE id = ? AND code = ? AND action = ?";
        
        stmt = conn.prepareStatement(query);
        stmt.setInt(1, tableId);
        stmt.setString(2, code);
        stmt.setString(3, "changePsw");
        rs = stmt.executeQuery();
        
        if(!rs.next())
            return null;
        
        String password = rs.getString("password");
        long userId = rs.getLong("user_id");
        return "{\"psw\":\"" + password + "\",\"userId\":\"" + userId + "\"}";
    }
    
    public void changeUserPassword(long userId, String password) throws SQLException{
        query = "UPDATE users SET password = ? WHERE id = ?";
        stmt = conn.prepareStatement(query);
        stmt.setString(1, password);
        stmt.setLong(2, userId);
        stmt.executeUpdate();   
    }
    
    public void changeUserPassword(String email, String password) throws SQLException{
        query = "UPDATE users SET password = ? WHERE email = ?";
        stmt = conn.prepareStatement(query);
        stmt.setString(1, password);
        stmt.setString(2, email);
        stmt.executeUpdate();
    }    
    
    public long checkDeleteUserAction(int tableId, String code)
            throws SQLException
    {
        query = "SELECT user_id"
                + "FROM users_activations "
                + "WHERE id = ? AND code = ? AND action = ?";
        
        stmt = conn.prepareStatement(query);
        stmt.setInt(1, tableId);
        stmt.setString(2, code);
        stmt.setString(3, "deleteUser");
        rs = stmt.executeQuery();
        
        if(!rs.next())
            return -1;
        
        return rs.getLong("user_id");
    }
    
    public void deleteUser(long userId) throws SQLException{
        query = "DELETE from users "
                + "WHERE id = ?";
        
        stmt = conn.prepareStatement(query);
        stmt.setLong(1, userId);
        stmt.executeUpdate();
    }
    
}
