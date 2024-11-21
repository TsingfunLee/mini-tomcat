package com.example.tomcat.engine.mapping;

import jakarta.servlet.Filter;

public class FilterMapping extends AbstractMapping{
    public final Filter filter;
    public FilterMapping(String url, Filter filter){
        super(url);
        this.filter = filter;
    }
}
