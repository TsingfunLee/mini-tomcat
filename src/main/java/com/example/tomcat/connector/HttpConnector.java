package com.example.tomcat.connector;

import com.example.tomcat.engine.HttpServletRequestImpl;
import com.example.tomcat.engine.HttpServletResponseImpl;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import jakarta.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;

public class HttpConnector implements HttpHandler, AutoCloseable {

    final Logger logger = LoggerFactory.getLogger(getClass());

    final HttpServer httpServer;

    public HttpConnector() throws IOException {
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
            process(request, response);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void process(HttpServletRequestImpl request, HttpServletResponseImpl response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String html = "<h1>Hello, " + (name == null ? "world" : name) + ".</h1>";
        response.setContentType("text/html");
        PrintWriter pw = response.getWriter();
        pw.write(html);
        pw.close();
    }

    @Override
    public void close() throws Exception {
        this.httpServer.stop(3);
    }
}
