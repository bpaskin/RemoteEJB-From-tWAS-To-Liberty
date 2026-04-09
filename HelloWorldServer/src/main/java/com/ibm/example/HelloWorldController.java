package com.ibm.example;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * CDI Managed Bean for Jakarta Faces to interact with HelloWorld EJB
 * Provides the same functionality as the HelloWorldTestServlet
 */
@Named("helloController")
@ApplicationScoped
public class HelloWorldController implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // Direct EJB injection (modern approach)
    @EJB
    private HelloWorldRemote helloWorldEJB;
    
    // Properties for the form
    private String name = "";
    private List<String> results = new ArrayList<>();
    private List<String> errors = new ArrayList<>();
    
    /**
     * Default constructor
     */
    public HelloWorldController() {
        // Initialize with basic test results
        performInitialTests();
    }
    
    /**
     * Perform initial tests similar to the servlet
     */
    public void performInitialTests() {
        results.clear();
        errors.clear();
        
        // Test 1: Direct EJB injection
        testDirectInjection();
        
        // Test 2: JNDI lookup
        testJNDILookup();
    }
    
    /**
     * Test direct EJB injection (modern Jakarta EE approach)
     */
    private void testDirectInjection() {
        try {
            if (helloWorldEJB != null) {
                String result = helloWorldEJB.sayHello();
                results.add("✓ Direct EJB Injection: " + result);
            } else {
                errors.add("✗ EJB injection failed - helloWorldEJB is null");
            }
        } catch (Exception e) {
            errors.add("✗ Error with direct injection: " + e.getMessage());
        }
    }
    
    /**
     * Test JNDI lookup using remote interface
     */
    private void testJNDILookup() {
        try {
            Context context = new InitialContext();
            
            // Try different JNDI names for remote interfaces
            String[] remoteJndiNames = {
                "java:global/HelloWorldServer/HelloWorldBean!com.ibm.example.HelloWorldRemote",
                "ejb/HelloWorldServer/HelloWorldServer.war/HelloWorldBean#com.ibm.example.HelloWorldRemote",
                "com.ibm.example.HelloWorldRemote"          
            };
            
            HelloWorldRemote remote = null;
            boolean found = false;
            
            for (String jndiName : remoteJndiNames) {
                try {
                    remote = (HelloWorldRemote) context.lookup(jndiName);
                    if (remote != null) {
                        String result = remote.sayHello();
                        results.add("✓ JNDI Lookup Success: " + result);
                        results.add("  JNDI Name: " + jndiName);
                        found = true;
                        break;
                    }
                } catch (Exception e) {
                    // Continue to next JNDI name
                    continue;
                }
            }
            
            if (!found) {
                errors.add("✗ Could not find Remote interface via JNDI lookup");
            }
            
        } catch (Exception e) {
            errors.add("✗ Error with JNDI lookup: " + e.getMessage());
        }
    }
    
    /**
     * Action method for personalized greeting
     */
    public void sayPersonalizedHello() {
        try {
            if (helloWorldEJB != null) {
                String result;
                if (name != null && !name.trim().isEmpty()) {
                    result = helloWorldEJB.sayHello(name.trim());
                } else {
                    result = helloWorldEJB.sayHello();
                }
                results.add("✓ Personalized Greeting: " + result);
            } else {
                errors.add("✗ EJB not available for personalized greeting");
            }
        } catch (Exception e) {
            errors.add("✗ Error with personalized greeting: " + e.getMessage());
        }
    }
    
    /**
     * Clear all results and errors
     */
    public void clearResults() {
        results.clear();
        errors.clear();
        name = "";
    }
    
    /**
     * Refresh all tests
     */
    public void refreshTests() {
        performInitialTests();
    }
    
    // Getters and Setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<String> getResults() {
        return results;
    }
    
    public List<String> getErrors() {
        return errors;
    }
    
    public boolean isHasResults() {
        return !results.isEmpty();
    }
    
    public boolean isHasErrors() {
        return !errors.isEmpty();
    }
}