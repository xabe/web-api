package com.xabe.web.api;

import com.xabe.web.api.config.CustomResourceConfig;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.LogManager;

public class App {

    private static final String BIND_IP = "0.0.0.0";
    private static HttpServer server;


    public static void main(String[] args) throws InterruptedException, IOException {
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        java.util.logging.Logger.getLogger("global").setLevel(Level.ALL);
        final ResourceConfig rc = new CustomResourceConfig();
        server = GrizzlyHttpServerFactory.createHttpServer(URI.create(getUriInfo("http" ,8008)), rc,false);
        server.getServerConfiguration().setAllowPayloadForUndefinedHttpMethods(true);
        server.start();
        Runtime.getRuntime().addShutdownHook( new Thread( server::shutdown ));
        LoggerFactory.getLogger(App.class).info( "Stop the application using CTRL+C" );
        Thread.currentThread().join();
    }

    private static String getUriInfo(String protocol,int port) {
        return String.format("%s://%s:%d/api", protocol, BIND_IP, port);
    }

}
