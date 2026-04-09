package com.ibm.example;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet that calls the remote HelloWorld EJB
 * Accessible at /hello endpoint
 */
@WebServlet(name = "HelloWorldClientServlet", urlPatterns = {"/hello"})
public class HelloWorldClientServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Handle GET requests to invoke the remote EJB
     * Supports optional 'name' parameter for personalized greeting
     * Supports optional 'host', 'port', 'schema', and 'protocol' parameters for custom WebSphere server location
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            // Get optional parameters
            String name = request.getParameter("name");
            String host = request.getParameter("host");
            String port = request.getParameter("port");
            String schema = request.getParameter("schema");
            String protocol = request.getParameter("protocol");
            
            // Create and initialize the EJB client
            HelloWorldClient client = new HelloWorldClient();
            
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>HelloWorld EJB Client</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; margin: 40px; }");
            out.println("h1 { color: #0066cc; }");
            out.println(".success { color: green; font-weight: bold; }");
            out.println(".error { color: red; font-weight: bold; }");
            out.println(".info { background-color: #f0f0f0; padding: 15px; border-radius: 5px; margin: 20px 0; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>HelloWorld Remote EJB Client</h1>");
            
            // Initialize the client with custom or default settings
            if (schema != null && protocol != null && host != null && port != null) {
                out.println("<p>Connecting to WebSphere at " + host + ":" + port + " using " + protocol.toUpperCase() + " with schema " + schema + "...</p>");
                client.initialize(schema, protocol, host, port);
            } else if (host != null && port != null) {
                out.println("<p>Connecting to WebSphere at " + host + ":" + port + " using IIOP...</p>");
                client.initialize(host, port);
            } else {
                out.println("<p>Connecting to WebSphere at localhost using IIOP (port 2809)...</p>");
                client.initialize();
            }
            
            // Call the remote EJB method
            String result;
            if (name != null && !name.trim().isEmpty()) {
                result = client.callSayHello(name);
                out.println("<div class='info'>");
                out.println("<p class='success'>✓ Successfully called remote EJB with parameter</p>");
                out.println("<p><strong>Response:</strong> " + result + "</p>");
                out.println("</div>");
            } else {
                result = client.callSayHello();
                out.println("<div class='info'>");
                out.println("<p class='success'>✓ Successfully called remote EJB</p>");
                out.println("<p><strong>Response:</strong> " + result + "</p>");
                out.println("</div>");
            }
            
            // Cleanup
            client.cleanup();
            
            // Usage instructions
            out.println("<h2>Usage</h2>");
            out.println("<p><strong>Note:</strong> This client uses IIOP/IIOPS for communication.</p>");
            out.println("<ul>");
            out.println("<li>Default call: <code>/hello</code></li>");
            out.println("<li>With name parameter: <code>/hello?name=YourName</code></li>");
            out.println("<li>Custom server: <code>/hello?host=server.example.com&port=2809</code></li>");
            out.println("<li>With SSL: <code>/hello?schema=corbaloc&protocol=iiops&host=server.example.com&port=2814</code></li>");
            out.println("<li>Combined: <code>/hello?name=YourName&schema=corbaloc&protocol=iiop&host=server.example.com&port=2809</code></li>");
            out.println("</ul>");
            out.println("<h3>Parameters</h3>");
            out.println("<ul>");
            out.println("<li><strong>name</strong>: Optional name for personalized greeting</li>");
            out.println("<li><strong>host</strong>: WebSphere server hostname (default: localhost)</li>");
            out.println("<li><strong>port</strong>: WebSphere IIOP/IIOPS port (default: 2809 for IIOP, 2814 for IIOPS)</li>");
            out.println("<li><strong>schema</strong>: Provider URL schema (default: corbaloc)</li>");
            out.println("<li><strong>protocol</strong>: Transport protocol - iiop or iiops (default: iiop)</li>");
            out.println("</ul>");
            
            out.println("</body>");
            out.println("</html>");
            
        } catch (NamingException e) {
            // Handle EJB lookup failures
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Error - HelloWorld EJB Client</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; margin: 40px; }");
            out.println("h1 { color: #cc0000; }");
            out.println(".error { background-color: #ffe6e6; padding: 15px; border-radius: 5px; border-left: 4px solid #cc0000; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Error Calling Remote EJB</h1>");
            out.println("<div class='error'>");
            out.println("<p><strong>Error:</strong> Failed to lookup or call remote EJB</p>");
            out.println("<p><strong>Message:</strong> " + e.getMessage() + "</p>");
            out.println("<p><strong>Possible causes:</strong></p>");
            out.println("<ul>");
            out.println("<li>WebSphere Application Server is not running</li>");
            out.println("<li>HelloWorld EJB is not deployed on WebSphere</li>");
            out.println("<li>Incorrect host or port configuration</li>");
            out.println("<li>Server not configured for IIOP/IIOPS on the specified port</li>");
            out.println("<li>Network connectivity issues</li>");
            out.println("<li>JNDI name mismatch</li>");
            out.println("</ul>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
            
            // Log the error
            log("Error calling remote EJB", e);
            
        } catch (Exception e) {
            // Handle other exceptions
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Error - HelloWorld EJB Client</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Unexpected Error</h1>");
            out.println("<p>Error: " + e.getMessage() + "</p>");
            out.println("</body>");
            out.println("</html>");
            
            // Log the error
            log("Unexpected error in servlet", e);
            
        } finally {
            out.close();
        }
    }
    
    /**
     * Handle POST requests the same way as GET
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
    
    /**
     * Provide servlet information
     */
    @Override
    public String getServletInfo() {
        return "HelloWorld Remote EJB Client Servlet";
    }
}

// Made with Bob
