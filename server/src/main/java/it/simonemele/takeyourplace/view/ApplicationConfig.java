/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.simonemele.takeyourplace.view;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author Simone
 */
@javax.ws.rs.ApplicationPath("api/v1")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(it.simonemele.takeyourplace.classes.CorsFilter.class);
        resources.add(it.simonemele.takeyourplace.view.Auth.class);
        resources.add(it.simonemele.takeyourplace.view.Places.class);
        resources.add(it.simonemele.takeyourplace.view.Promotions.class);
        resources.add(it.simonemele.takeyourplace.view.Users.class);
        resources.add(org.glassfish.jersey.server.wadl.internal.WadlResource.class);
    }
    
}
