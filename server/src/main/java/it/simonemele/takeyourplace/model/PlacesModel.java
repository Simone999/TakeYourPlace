/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.simonemele.takeyourplace.model;

import it.simonemele.takeyourplace.classes.Coords;
import it.simonemele.takeyourplace.classes.Place;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Simone
 */
public class PlacesModel extends Database{
    
    public ArrayList<Place> getPlaces (Coords coords, double maxDistance) 
            throws SQLException
    {
        ArrayList<Place> places = new ArrayList<>();
        
        String distance = "( 6372000 * (acos(sin(radians(?)) * "
                + "sin(radians(lat)) + cos(radians(?)) * cos(radians(lat)) * "
                + "cos(abs(radians(lng) - radians(?))))))";
        
        query = "SELECT *, " + distance + "  AS distance "
                + "FROM places p INNER JOIN coords c "
                + "ON p.coords_id = c.id "
                + "HAVING distance < ? "
                + "ORDER BY distance ASC";
        
        stmt = conn.prepareStatement(query);
        stmt.setDouble(1, coords.getLat());
        stmt.setDouble(2, coords.getLat());
        stmt.setDouble(3, coords.getLng());
        stmt.setDouble(4, maxDistance);
        
        rs = stmt.executeQuery();
        
        Place place;
        while(rs.next()){
            place = this.buildPlace(rs);
            place.setDistance(rs.getFloat("distance"));
            places.add(place);
        }
        
        return places;
    }
    
    public ArrayList<Place> getUserPlaces(long userId) throws SQLException{
        query = "SELECT * "
                + "FROM places p INNER JOIN coords c "
                + "ON p.coords_id = c.id "
                + "WHERE manager_id = ?";
        stmt = conn.prepareStatement(query);
        stmt.setLong(1, userId);
        rs = stmt.executeQuery();
        
        ArrayList<Place> places = new ArrayList<>();
        
        while(rs.next())
            places.add(this.buildPlace(rs));
        
        return places;
    }
    
    public Place getPlace(long id) throws SQLException{
        query = "SELECT * "
                + "FROM places p INNER JOIN coords c "
                + "ON p.coords_id = c.id "
                + "WHERE p.id = ?";
        stmt = conn.prepareStatement(query);
        stmt.setLong(1, id);
        rs = stmt.executeQuery();
        
        if(rs.next())
            return this.buildPlace(rs);
            
        return new Place();
    }
    
    public void createPlace(Place place) throws SQLException{
        double lat = place.getCoords().getLat();
        double lng = place.getCoords().getLng();
        long coordsId = this.insertCoords(lat,lng);
        
        query = "INSERT INTO places("
                + "name, address, email, telephone, coords_id, type, manager_id) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?)";
        
        stmt = conn.prepareStatement(query);
        stmt.setString(1, place.getName());
        stmt.setString(2, place.getAddress());
        stmt.setString(3, place.getEmail());
        stmt.setString(4, place.getTelephone());
        stmt.setLong(5, coordsId);
        stmt.setShort(6, place.getType());        
        stmt.setLong(7, place.getManagerId());
        stmt.executeUpdate();        
    }
    
    public void deletePlace(long id) throws SQLException{
        query = "DELETE FROM places, coords "
                + "USING places, coords "
                + "WHERE places.coords_id = coords.id "
                + "AND places.id = ?";
        stmt = conn.prepareStatement(query);        
        stmt.setLong(1, id);
        stmt.executeUpdate();
    }
    
    public void updatePlace(Place place) throws SQLException{
        query = "UPDATE places p INNER JOIN coords c "
                + "ON c.id = p.coords_id "
                + "SET name = ?, address = ?, email = ?, telephone = ?, "
                + "type = ?, lat = ?, lng = ? "
                + "WHERE p.id = ?";
        
        stmt = conn.prepareStatement(query);
        stmt.setString(1, place.getName());
        stmt.setString(2, place.getAddress());
        stmt.setString(3, place.getEmail());
        stmt.setString(4, place.getTelephone());
        stmt.setInt(5, place.getType());
        stmt.setDouble(6, place.getCoords().getLat());        
        stmt.setDouble(7, place.getCoords().getLng());     
        stmt.setLong(8, place.getId());
        stmt.executeUpdate();        
    }
    
    public long getPlaceManager(long id) throws SQLException{
        query = "SELECT manager_id FROM places WHERE id = ?";
        stmt = conn.prepareStatement(query);
        stmt.setLong(1, id);
        rs = stmt.executeQuery();
        
        if(!rs.next())
            return -1;
        
        return rs.getBigDecimal("manager_id").longValue();
    }
    
    /* private methods */
    private Place buildPlace(ResultSet rs) throws SQLException{
        Place p = new Place();
        double lat, lng;
        
        p.setId(rs.getLong("id"));
        p.setName(rs.getString("name"));
        p.setAddress(rs.getString("address"));
        p.setTelephone(rs.getString("telephone"));
        p.setEmail(rs.getString("email"));
        p.setRegistrationDate(this.rs.getDate("registration_date"));

        lat = rs.getDouble("lat");
        lng = rs.getDouble("lng");
        p.setCoords(new Coords(lat,lng));

        p.setType(rs.getShort("type"));
        p.setManagerId(rs.getLong("manager_id"));

        return p;
    }
    
    private long insertCoords(double lat, double lng) throws SQLException{
        query = "INSERT INTO coords(lat, lng) VALUES(?,?)";
        stmt = conn.prepareStatement(
                query,
                PreparedStatement.RETURN_GENERATED_KEYS
        );
        stmt.setDouble(1, lat);
        stmt.setDouble(2, lng);        
        stmt.executeUpdate();
        
        return this.getLastLongId(stmt.getGeneratedKeys());
    }
}
