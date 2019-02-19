/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.simonemele.takeyourplace.classes;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

/**
 *
 * @author Simone
 */
public class Jwt{
    
    private final String secretKey;
    private final String JWTId;
    private final String userId;
    private final String clientId;
    private final Algorithm algorithm;

    public Jwt(long JWTId, long userId, String clientId)
            throws IllegalArgumentException,
            UnsupportedEncodingException,
            IOException 
    {   
        this.JWTId = String.valueOf(JWTId);
        this.userId = String.valueOf(userId);
        this.clientId = clientId;
        this.secretKey = loadKey();
        this.algorithm = Algorithm.HMAC256(this.secretKey);
    }
    
    private String loadKey() throws IOException{
        
        Properties properties = new Properties();
        InputStream fileStream = this.getClass().getClassLoader()
                .getResourceAsStream("token.conf");
        
        properties.load(fileStream);
        
        return properties.getProperty("key");
    }
    
    public String create(){
        Date now = new Date();
        
        final long oneMonthMs = 2628000000l; //2,628e9 ms
        Date oneMonth = new Date();
        oneMonth.setTime(now.getTime() + oneMonthMs);
        
        Builder b = JWT.create();
        String token = b
                .withIssuer(this.userId)
                .withSubject(this.clientId)
                .withNotBefore(now)
                .withExpiresAt(oneMonth)
                .withJWTId(this.JWTId)
                .sign(this.algorithm);
        
        return token;
    }
    
    public boolean verify(String token){
        JWTVerifier verifier = JWT.require(this.algorithm)
                .withIssuer(this.userId)
                .withSubject(this.clientId)
                .withJWTId(this.JWTId)
                .build();
        try{
            verifier.verify(token);
            return true;
        }catch(JWTVerificationException ex){
            return false;
        }
    }
    
    public static DecodedJWT decode(String token) throws JWTDecodeException{
        return JWT.decode(token);
    }
    
    public static long getJWTId(String token) throws 
            JWTDecodeException,
            NullPointerException
    {        
        return Long.valueOf(decode(token).getId());
        
        //return decodedToken.getClaim("jti").asLong();
    }
    
    public static long getUserId(String token) throws
            JWTDecodeException,
            NullPointerException
    {
        return Long.valueOf(decode(token).getIssuer());
    }
    
}
