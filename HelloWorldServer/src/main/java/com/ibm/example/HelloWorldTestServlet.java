package com.ibm.example;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet to test the HelloWorld EJB using remote interfaces
 * 
 * This should have been a JSP and bean, but it is an example
 */
@WebServlet(name = "HelloWorldTestServlet", urlPatterns = {"/test", "/hello"})
public class HelloWorldTestServlet extends HttpServlet {
    
    // Direct EJB injection (modern approach)
    @EJB
    private HelloWorldRemote helloWorldEJB;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>HelloWorld EJB Test</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; margin: 40px; }");
            out.println("h1 { color: #333; }");
            out.println("h2 { color: #666; margin-top: 30px; }");
            out.println(".result { background-color: #f0f0f0; padding: 10px; margin: 10px 0; border-radius: 5px; }");
            out.println(".error { background-color: #ffe6e6; color: #cc0000; padding: 10px; margin: 10px 0; border-radius: 5px; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>HelloWorld EJB Test Results</h1>");
            
            // Test 1: Direct EJB injection (modern approach)
            out.println("<h2>Test 1: Direct EJB Injection (@EJB)</h2>");
            testDirectInjection(out);
            
            // Test 2: JNDI lookup using home interface (traditional approach)
            out.println("<h2>Test 2: JNDI Lookup with Home Interface</h2>");
            testJNDILookup(out);
            
            // Test 3: Test with parameter
            String name = request.getParameter("name");
            if (name != null && !name.trim().isEmpty()) {
                out.println("<h2>Test 3: Personalized Greeting</h2>");
                testPersonalizedGreeting(out, name);
            } else {
                out.println("<h2>Test 3: Personalized Greeting</h2>");
                out.println("<p>Add ?name=YourName to the URL to test personalized greeting</p>");
            }
            
            out.println("<hr>");
            out.println("<p><strong>Usage Examples:</strong></p>");
            out.println("<ul>");
            out.println("<li><a href=\"/HelloWorldServer/test\">Basic Test</a></li>");
            out.println("<li><a href=\"/HelloWorldServer/test?name=Jakarta\">Personalized Test</a></li>");
            out.println("<li><a href=\"/HelloWorldServer/hello\">Alternative URL</a></li>");
            out.println("</ul>");
            
            out.println("</body>");
            out.println("</html>");
            
        } catch (Exception e) {
            out.println("<div class=\"error\">Error: " + e.getMessage() + "</div>");
            e.printStackTrace();
        } finally {
            out.close();
        }
    }
    
    /**
     * Test direct EJB injection (modern Jakarta EE approach)
     */
    private void testDirectInjection(PrintWriter out) {
        try {
            if (helloWorldEJB != null) {
                String result = helloWorldEJB.sayHello();
                out.println("<div class=\"result\"><strong>Success:</strong> " + result + "</div>");
            } else {
                out.println("<div class=\"error\">EJB injection failed - helloWorldEJB is null</div>");
            }
        } catch (Exception e) {
            out.println("<div class=\"error\">Error with direct injection: " + e.getMessage() + "</div>");
        }
    }
    
    /**
     * Test JNDI lookup using home interface (traditional approach)
     */
    private void testJNDILookup(PrintWriter out) {
        try {
            Context context = new InitialContext();
            
            // Try different JNDI names for remote interfaces
            String[] remoteHomeJndiNames = {
                "java:global/HelloWorldServer/HelloWorldBean!com.ibm.example.HelloWorldRemote",
                "ejb/HelloWorldServer/HelloWorldServer.war/HelloWorldBean#com.ibm.example.HelloWorldRemote",
                "com.ibm.example.HelloWorldRemote"          
            };
            
            // Test Remote  Interface
            out.println("<h3>Remote Interface Test:</h3>");
            HelloWorldRemote remote = null;
            String successfulRemoteJndiName = null;
            
            for (String jndiName : remoteHomeJndiNames) {
                try {
                    remote = (HelloWorldRemote) context.lookup(jndiName);
                    if (remote != null) {
                        String result = remote.sayHello();
                        out.println("<div class=\"result\"><strong>Success with Remote:</strong> " + result + "</div>");
                        out.println("<div class=\"result\"><strong>JNDI Name used:</strong> " + jndiName + "</div>");
                    } else {
                        out.println("<div class=\"error\">Could not find Remote interface</div>");
                    }
                } catch (Exception e) {
                    e.printStackTrace(out);
                    continue;
                }
            }
        } catch (Exception e) {
            out.println("<div class=\"error\">Error with JNDI lookup: " + e.getMessage() + "</div>");
            e.printStackTrace(System.err);
        }
    }
    
    /**
     * Test personalized greeting
     */
    private void testPersonalizedGreeting(PrintWriter out, String name) {
        try {
            if (helloWorldEJB != null) {
                String result = helloWorldEJB.sayHello(name);
                out.println("<div class=\"result\"><strong>Personalized Greeting:</strong> " + result + "</div>");
            } else {
                out.println("<div class=\"error\">EJB not available for personalized greeting</div>");
            }
        } catch (Exception e) {
            out.println("<div class=\"error\">Error with personalized greeting: " + e.getMessage() + "</div>");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
    
    @Override
    public String getServletInfo() {
        return "HelloWorld EJB Test Servlet";
    }
}