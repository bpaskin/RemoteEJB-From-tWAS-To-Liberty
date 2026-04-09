package com.ibm.example;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * JAX-RS Application configuration
 * Defines the base path for REST endpoints
 * All REST resources will be accessible under /api/*
 */
@ApplicationPath("/api")
public class RestApplication extends Application {
    // No additional configuration needed
    // JAX-RS will automatically discover and register all @Path annotated classes
}

// Made with Bob
