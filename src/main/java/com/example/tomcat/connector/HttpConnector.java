package com.example.tomcat.connector;

import com.example.tomcat.engine.HttpServletRequestImpl;
import com.example.tomcat.engine.HttpServletResponseImpl;
import com.example.tomcat.engine.ServletContextImpl;
import com.example.tomcat.engine.filter.HelloFilter;
import com.example.tomcat.engine.servlet.HelloServlet;
import com.example.tomcat.engine.servlet.IndexServlet;
import com.example.tomcat.engine.filter.LogFilter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

public class HttpConnector implements HttpHandler, AutoCloseable {

    final Logger logger = LoggerFactory.getLogger(getClass());

    final HttpServer httpServer;

    final ServletContextImpl servletContext;

    public HttpConnector() throws IOException {
        this.servletContext = new ServletContextImpl();
        this.servletContext.initServlets(List.of(IndexServlet.class, HelloServlet.class));
        this.servletContext.initFilters(List.of(LogFilter.class, HelloFilter.class));
        String host = "0.0.0.0";
        int port = 5050;
        this.httpServer = HttpServer.create(new InetSocketAddress(host, port), 0, "/", this);
        this.httpServer.start();
        logger.info("http server started at {}:{}...", host, port);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        logger.info("{}: {}?{}", exchange.getRequestMethod(), exchange.getRequestURI().getPath(), exchange.getRequestURI().getRawQuery());
        var adapter = new HttpExchangeAdapter(exchange);
        var request = new HttpServletRequestImpl(adapter);
        var response = new HttpServletResponseImpl(adapter);
        // process:
        try {
            this.servletContext.process(request, response);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void close() throws Exception {
        this.httpServer.stop(3);
    }
}
