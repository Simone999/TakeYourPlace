/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.simonemele.takeyourplace.exceptions;

/**
 *
 * @author Simone
 */
public class NotLoggedInException extends Exception{

    public NotLoggedInException() {
        super("User not logged in");
    }
    
}
