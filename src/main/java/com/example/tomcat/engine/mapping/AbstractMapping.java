package com.example.tomcat.engine.mapping;

import java.util.regex.Pattern;

public abstract class AbstractMapping implements Comparable<AbstractMapping> {
    final Pattern pattern;
    final String url;

    public AbstractMapping(String url){
        this.url = url;
        this.pattern = buildPattern(url);
    }

    public Boolean matches(String uri){
        return this.pattern.matcher(uri).matches();
//        return true;
    }

    Pattern buildPattern(String urlPattern){
        StringBuilder sb = new StringBuilder(urlPattern.length() + 16);
        sb.append('^');
        for (int i = 0; i < urlPattern.length(); i++) {
            char ch = urlPattern.charAt(i);
            if (ch == '*') {
                sb.append(".*");
            } else if (ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch >= '0' && ch <= '9') {
                sb.append(ch);
            } else {
                sb.append('\\').append(ch);
            }
        }
        sb.append('$');
        return Pattern.compile(sb.toString());
    }

//    private Pattern buildPattern(String url){
//        String regex = "^" + url.replaceAll("\\*", ".*") + "$";
//        regex = regex.replaceAll("([^a-zA-z0-9.*])", "\\\\$1");
//
//        return Pattern.compile(regex);
//    }

    @Override
    public int compareTo(AbstractMapping other){
        if(this.url.equals("/")) return 2;
        if(this.url.equals("*")) return 1;

        return this.url.length() - other.url.length() == 0 ? this.url.compareTo(other.url) : this.url.length() - other.url.length();
    }
}
