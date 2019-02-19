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
public class PromotionFullException extends Exception{

    public PromotionFullException(String message) {
        super("Promotion full exception");
    }    

    public PromotionFullException() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
