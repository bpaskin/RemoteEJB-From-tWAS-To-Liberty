package com.ibm.example;

import jakarta.ejb.Remote;

/**
 * Remote interface for HelloWorld EJB
 * Defines the business methods available to remote clients
 */
@Remote
public interface HelloWorldRemote {
    
    /**
     * Returns a simple Hello World greeting
     * @return Hello World message
     */
    String sayHello();
    
    /**
     * Returns a personalized Hello World greeting
     * @param name the name to include in the greeting
     * @return personalized Hello World message
     */
    String sayHello(String name);
}