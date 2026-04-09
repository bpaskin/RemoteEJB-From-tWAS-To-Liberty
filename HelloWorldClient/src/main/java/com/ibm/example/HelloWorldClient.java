package com.ibm.example;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * EJB Client for HelloWorld remote EJB
 * Demonstrates how to lookup and invoke remote EJB methods on WebSphere 9.0.5
 * to Liberty server using IIOP protocol.
 * 
 * When going to Liberty with SSL, it is important to use the non SSL IIOP and
 * that will then transfer the request to the IIOPs port.
 */
public class HelloWorldClient {
    
    private HelloWorldRemote helloWorldRemote;
    
    // Liberty server configuration
    private static final String WAS_HOST = "localhost";
    private static final String WAS_IIOP_PORT = "2809";
    private static final String WAS_SCHEMA = "corbaname";
    private static final String WAS_PROTOCOL = "";


    /**
     * Initialize the EJB client and lookup the remote EJB using IIOP
     * @throws NamingException if the EJB lookup fails
     */
    public void initialize() throws NamingException {
        initialize(WAS_SCHEMA, WAS_PROTOCOL, WAS_HOST, WAS_IIOP_PORT);
    }
    
    /**
     * Initialize the EJB client with custom host and port using non-SSL IIOP
     * @param host Liberty server host
     * @param port Liberty IIOP port (typically 2809 for Liberty, 2809 for traditional WAS)
     * @throws NamingException if the EJB lookup fails
     */
    public void initialize(String host, String port) throws NamingException {
        initialize(WAS_SCHEMA, WAS_PROTOCOL, host, port);
    }
    
    /**
     * Initialize the EJB client with custom schema, protocol, host, and port.
     * @param schema provider URL schema, for example corbaloc
     * @param protocol transport protocol, for example iiop or iiops
     * @param host Liberty server host
     * @param port Liberty server port
     * @throws NamingException if the EJB lookup fails
     */
    public void initialize(String schema, String protocol, String host, String port) throws NamingException {
        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, "com.ibm.websphere.naming.WsnInitialContextFactory");
        props.put(Context.PROVIDER_URL, schema + ":" + protocol + ":" + host + ":" + port);

        InitialContext ctx = new InitialContext(props);
        //InitialContext ctx = new InitialContext();
    
        String jndiName =  "ejb/global/HelloWorldServer/HelloWorldBean!com.ibm.example.HelloWorldRemote";

        try {
            helloWorldRemote = (HelloWorldRemote) ctx.lookup(jndiName);
            System.out.println("Successfully looked up HelloWorld EJB from Liberty at " + host + ":" + port + " using " + protocol.toUpperCase());
        } catch (NamingException e) {
            System.err.println("Failed to lookup HelloWorld EJB: " + e.getMessage());
            System.err.println("Make sure Liberty server is running on " + host + ":" + port);
            System.err.println("Ensure the server is configured for " + protocol.toUpperCase() + " on port " + port);
            throw e;
        } finally {
            ctx.close();
        }
    }
    
    /**
     * Call the sayHello() method on the remote EJB
     * @return the greeting message from the EJB
     */
    public String callSayHello() {
        if (helloWorldRemote == null) {
            throw new IllegalStateException("EJB not initialized. Call initialize() first.");
        }
        
        try {
            return helloWorldRemote.sayHello();
        } catch (Exception e) {
            System.err.println("Error calling sayHello(): " + e.getMessage());
            throw new RuntimeException("Failed to call remote EJB method", e);
        }
    }
    
    /**
     * Call the sayHello(String name) method on the remote EJB
     * @param name the name to include in the greeting
     * @return the personalized greeting message from the EJB
     */
    public String callSayHello(String name) {
        if (helloWorldRemote == null) {
            throw new IllegalStateException("EJB not initialized. Call initialize() first.");
        }
        
        try {
            return helloWorldRemote.sayHello(name);
        } catch (Exception e) {
            System.err.println("Error calling sayHello(String): " + e.getMessage());
            throw new RuntimeException("Failed to call remote EJB method", e);
        }
    }
    
    /**
     * Cleanup resources
     */
    public void cleanup() {
        helloWorldRemote = null;
    }
}