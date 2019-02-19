/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.simonemele.takeyourplace.classes;

import java.sql.Timestamp;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.TimeZone;

/**
 *
 * @author Simone
 */
public class Promotion {
    
    private long id;
    private String title;
    private String description;
    private java.util.Date start;
    private java.util.Date end;
    private int max;
    private int given;
    private long placeId;

    public Promotion() {
    }

    public Promotion(long id, String title, String description, java.util.Date start, java.util.Date end, int max, int given, long placeId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.start = start;
        this.end = end;
        this.max = max;
        this.given = given;
        this.placeId = placeId;
    }
    
    public boolean isAvailable(){
        //Check if the promotion is already available
        
        return !isEnded() && !isFull();
    }
    
    public boolean isEnded(){
        ZonedDateTime zonedEnd = ZonedDateTime.ofInstant(end.toInstant(), ZoneOffset.UTC);
        ZonedDateTime now = ZonedDateTime.now( ZoneOffset.UTC );
        
        return now.isAfter(zonedEnd) || now.isEqual(zonedEnd) ;
    }
    
    public boolean isFull(){
        return given >= max;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getGiven() {
        return given;
    }

    public void setGiven(int given) {
        this.given = given;
    }

    public long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(long placeId) {
        this.placeId = placeId;
    }
    
    public String getStartDateTime(){
        return getDateTime(start);
    }
    
    public String getEndDateTime(){
        return getDateTime(end);
    }
    
    private String getDateTime(Date date){
        System.out.println("date " + date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }
    
    public void setStartByTimestamp(Timestamp timestamp){
        this.start = toDate(timestamp);
    }
    
    public void setEndtByTimestamp(Timestamp timestamp){
        this.end = toDate(timestamp);
    }
    
    private Date toDate(Timestamp timestamp){
        return Date.from(ZonedDateTime.ofInstant(timestamp.toInstant(), ZoneId.of("UTC")).toInstant());
        
    }
    
}
