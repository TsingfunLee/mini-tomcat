package com.example.tomcat.connector;

import java.net.URI;

public interface HttpExchangeRequest {
    String getRequestMethod();
    URI getRequestURI();
}
