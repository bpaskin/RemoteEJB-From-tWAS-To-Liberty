package com.ibm.example;

import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Resource for HelloWorld EJB Client
 * Provides RESTful endpoints to invoke remote EJB methods
 * Accessible at /api/hello endpoint
 */
@Path("/hello")
public class HelloWorldRestResource {
    
    /**
     * Call the remote HelloWorld EJB via REST
     * 
     * @param name Optional name for personalized greeting
     * @param host WebSphere server hostname (default: localhost)
     * @param port WebSphere server port (default: 2809)
     * @param schema Provider URL schema (default: corbaloc)
     * @param protocol Transport protocol - iiop (default: iiop)
     * @return JSON response with the EJB result or error
     * 
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response sayHello(
            @QueryParam("name") String name,
            @QueryParam("host") String host,
            @QueryParam("port") String port,
            @QueryParam("schema") String schema,
            @QueryParam("protocol") String protocol) {
        
        Map<String, Object> response = new HashMap<>();
        HelloWorldClient client = null;
        
        try {
            // Create and initialize the EJB client
            client = new HelloWorldClient();
            
            // Build connection info for response
            String connectionInfo;
            
            // Initialize the client with custom or default settings
            if (schema != null && protocol != null && host != null && port != null) {
                connectionInfo = String.format("%s:%s:%s:%s", schema, protocol, host, port);
                client.initialize(schema, protocol, host, port);
            } else if (host != null && port != null) {
                connectionInfo = String.format("corbaloc:iiop:%s:%s", host, port);
                client.initialize(host, port);
            } else {
                connectionInfo = "corbaloc:iiop:localhost:2809";
                client.initialize();
            }
            
            // Call the remote EJB method
            String result;
            if (name != null && !name.trim().isEmpty()) {
                result = client.callSayHello(name);
            } else {
                result = client.callSayHello();
            }
            
            // Build success response
            response.put("success", true);
            response.put("message", result);
            response.put("connection", connectionInfo);
            
            if (name != null && !name.trim().isEmpty()) {
                response.put("name", name);
            }
            
            return Response.ok(response).build();
            
        } catch (NamingException e) {
            // Handle EJB lookup failures
            response.put("success", false);
            response.put("error", "Failed to lookup or call remote EJB");
            response.put("message", e.getMessage());
            response.put("possibleCauses", new String[]{
                "WebSphere Application Server is not running",
                "HelloWorld EJB is not deployed on WebSphere",
                "Incorrect host or port configuration",
                "Server not configured for IIOP/IIOPS on the specified port",
                "Network connectivity issues",
                "JNDI name mismatch"
            });
            
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(response)
                    .build();
            
        } catch (Exception e) {
            // Handle other exceptions
            response.put("success", false);
            response.put("error", "Unexpected error");
            response.put("message", e.getMessage());
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(response)
                    .build();
            
        } finally {
            // Cleanup
            if (client != null) {
                client.cleanup();
            }
        }
    }
    
    /**
     * Get API information and usage examples
     * 
     * @return JSON response with API documentation
     */
    @GET
    @Path("/info")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInfo() {
        Map<String, Object> info = new HashMap<>();
        
        info.put("name", "HelloWorld EJB REST Client");
        info.put("version", "1.0");
        info.put("description", "RESTful API to invoke remote HelloWorld EJB on WebSphere");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("GET /api/hello", "Call the remote EJB");
        endpoints.put("GET /api/hello/info", "Get API information");
        info.put("endpoints", endpoints);
        
        Map<String, String> parameters = new HashMap<>();
        parameters.put("name", "Optional name for personalized greeting");
        parameters.put("host", "WebSphere server hostname (default: localhost)");
        parameters.put("port", "WebSphere IIOP/IIOPS port (default: 2809 for IIOP)");
        parameters.put("schema", "Provider URL schema (default: corbaloc)");
        parameters.put("protocol", "Transport protocol - iiop (default: iiop)");
        info.put("parameters", parameters);
        
        String[] examples = {
            "GET /api/hello",
            "GET /api/hello?name=John",
            "GET /api/hello?host=server.example.com&port=2809",
            "GET /api/hello?schema=corbaname&protocol=iiop&host=server.example.com&port=2809",
            "GET /api/hello?name=John&schema=corbaname&protocol=iiop&host=localhost&port=2809"
        };
        info.put("examples", examples);
        
        return Response.ok(info).build();
    }
}

