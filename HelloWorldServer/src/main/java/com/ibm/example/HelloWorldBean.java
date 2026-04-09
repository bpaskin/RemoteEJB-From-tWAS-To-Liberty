package com.ibm.example;

import jakarta.ejb.CreateException;
import jakarta.ejb.SessionBean;
import jakarta.ejb.SessionContext;
import jakarta.ejb.Stateless;
import java.rmi.RemoteException;

/**
 * HelloWorld EJB implementation
 * A stateless session bean that provides simple greeting functionality
 */
@Stateless
public class HelloWorldBean implements SessionBean, HelloWorldRemote {
    
    private SessionContext sessionContext;
    
    /**
     * Default constructor
     */
    public HelloWorldBean() {
        // Default constructor
    }
    
    /**
     * EJB create method
     * @throws CreateException if the EJB cannot be created
     */
    public void ejbCreate() throws CreateException {
        // Initialization logic if needed
    }
    
    /**
     * Returns a simple Hello World greeting
     * @return Hello World message
     */
    @Override
    public String sayHello() {
        return "Hello World from Jakarta EE 10 EJB!";
    }
    
    /**
     * Returns a personalized Hello World greeting
     * @param name the name to include in the greeting
     * @return personalized Hello World message
     */
    @Override
    public String sayHello(String name) {
        if (name == null || name.trim().isEmpty()) {
            return sayHello();
        }
        return "Hello " + name + " from Jakarta EE 10 EJB!";
    }
    
    // SessionBean lifecycle methods
    
    @Override
    public void setSessionContext(SessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }
    
    @Override
    public void ejbRemove() {
        // Cleanup logic if needed
    }
    
    @Override
    public void ejbActivate() {
        // Activation logic if needed
    }
    
    @Override
    public void ejbPassivate() {
        // Passivation logic if needed
    }
}