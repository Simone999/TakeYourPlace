/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.simonemele.takeyourplace.classes;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

/**
 *
 * @author Simone
 */
public class Hash {
    
    private static final int iterations = 3;
    private static final int memory = 65536; //KiB
    private static final int threads = 4;
    private static final Argon2 argon2 = Argon2Factory.create(30, 100);
    
    public static String hash(String string){
        return argon2.hash(iterations, memory, threads, string);
    }
    
    public static boolean verify(String hash, String password){
        return argon2.verify(hash, password);
    }
}
