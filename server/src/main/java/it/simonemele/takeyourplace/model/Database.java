/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.simonemele.takeyourplace.model;

import it.simonemele.takeyourplace.classes.DatabaseCredentials;
import it.simonemele.takeyourplace.classes.Jwt;
import it.simonemele.takeyourplace.classes.User;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author Simone
 */

/* createUser() da FINIRE*/

public class Database {
    
    protected Connection conn;
    protected PreparedStatement stmt;
    protected ResultSet rs;
    protected String query;

    public Database() {}
        
    public void connect() throws 
            FileNotFoundException,
            IOException,
            ClassNotFoundException,
            SQLException,
            InstantiationException,
            IllegalAccessException
    {   
       
        DatabaseCredentials credentials = this.readCredentials();
        
        //connection
        Class.forName("com.mysql.jdbc.Driver");
        this.conn = DriverManager.getConnection(
                credentials.getUrl(),
                credentials.getUsername(),
                credentials.getPassword()
        );
    }
    
    public Jwt getToken(long jwtId) throws 
            SQLException,
            IllegalArgumentException,
            IOException,
            UnsupportedEncodingException
    {
        query = "SELECT j.client_id, j.user_id "
                + "FROM jwt j INNER JOIN users u "
                + "ON u.id = j.user_id "
                + "WHERE j.id = ? AND u.activated = ?";
        stmt = conn.prepareStatement(query);
        stmt.setLong(1, jwtId);
        stmt.setBoolean(2, true);
        rs = stmt.executeQuery();
        
        if(!rs.next())
            return null;
        
        return new Jwt(jwtId, rs.getLong("user_id"), rs.getString("client_id"));
    }
    
    private DatabaseCredentials readCredentials() throws IOException{
        
        Properties properties = new Properties();
        DatabaseCredentials credentials = new DatabaseCredentials();
        InputStream fileStream = this.getClass().getClassLoader()
                                          .getResourceAsStream("database.conf");
        
        /*Reading properties... */
        properties.load(fileStream);        
        credentials.setUrl(properties.getProperty("url"));
        credentials.setUsername(properties.getProperty("username"));
        credentials.setPassword(properties.getProperty("password"));
        
        return credentials;
    }
    
    public void close() throws SQLException{
        this.conn.close();
    }
    
    protected long getLastLongId(ResultSet res) throws SQLException{
        res.next();
        return res.getLong(1);
    }
    
    protected int getLastIntId(ResultSet res) throws SQLException{
        res.next();
        return res.getInt(1);
    }
    
    public int insertUserActivationMail(
            long userId,
            String email,
            String code,
            String action ) throws SQLException {
        
        query = "INSERT INTO users_activations(code, email, action, user_id) "
                + "VALUES(?, ?, ?, ?)";
        stmt = conn.prepareStatement(
                query,
                PreparedStatement.RETURN_GENERATED_KEYS
        );
        stmt.setString(1, code);
        stmt.setString(2, email);
        stmt.setString(3, action);
        stmt.setLong(4, userId);
        stmt.executeUpdate();
        
        return getLastIntId(stmt.getGeneratedKeys());
    }
    
    public void updateUserEmail(long userId, String email) throws SQLException {
        query = "UPDATE users SET email = ? WHERE id = ?";
        stmt = conn.prepareStatement(query);
        stmt.setString(1, email);
        stmt.setLong(2, userId);
        stmt.executeUpdate();
    }
    
    public void deleteJwtDifferentFrom(long jwtId, long userId) throws SQLException{
        query = "DELETE from jwt WHERE id <> ? AND user_id = ?";
        stmt = conn.prepareStatement(query);
        stmt.setLong(1, jwtId);
        stmt.setLong(2, userId);
        stmt.executeUpdate();
    }
    
    public void deleteUserJwt(long userId) throws SQLException{
        query = "DELETE from jwt WHERE user_id = ?";
        stmt = conn.prepareStatement(query);
        stmt.setLong(1, userId);
        stmt.executeUpdate();
    }
    
    public User getUserByEmail(String email) throws SQLException{
        query = "SELECT * FROM users WHERE email = ?";
        stmt = conn.prepareStatement(query);
        stmt.setString(1, email);
        rs = stmt.executeQuery();
        if(!rs.next())
            return null;
        
        return this.buildUser(rs);
    }
    
    protected User buildUser(ResultSet rs) throws SQLException{
        User u = new User();
        
        u.setId(rs.getLong("id"));
        u.setName(rs.getString("name"));
        u.setSurname(rs.getString("surname"));
        u.setEmail(rs.getString("email"));
        u.setDistrict(rs.getString("district"));
        u.setCounty(rs.getString("county"));
        u.setCountry(rs.getString("country"));
        u.setRegistrationDate(rs.getDate("registration_date"));
        u.setManager(rs.getBoolean("manager"));
        u.setActivated(rs.getBoolean("activated"));
        
        return u;
    }
            
}
