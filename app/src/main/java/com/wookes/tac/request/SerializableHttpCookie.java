package com.wookes.tac.request;

import java.io.Serializable;
import java.net.HttpCookie;

class SerializableHttpCookie implements Serializable {
    private static final long serialVersionUID = 6374381323722046732L;
    private String name;
    private String value;
    private String comment;
    private String commentURL;
    private String domain;
    private long maxAge;
    private String path;
    private String portlist;
    private int version;
    private boolean secure;
    private boolean discard;

    SerializableHttpCookie(HttpCookie cookie) {
        this.name = cookie.getName();
        this.value = cookie.getValue();
        this.comment = cookie.getComment();
        this.commentURL = cookie.getCommentURL();
        this.domain = cookie.getDomain();
        this.maxAge = cookie.getMaxAge();
        this.path = cookie.getPath();
        this.portlist = cookie.getPortlist();
        this.version = cookie.getVersion();
        this.secure = cookie.getSecure();
        this.discard = cookie.getDiscard();
    }

    HttpCookie getCookie(){
        HttpCookie clientCookie = new HttpCookie(name, value);
        clientCookie.setComment(comment);
        clientCookie.setCommentURL(commentURL);
        clientCookie.setDomain(domain);
        clientCookie.setMaxAge(maxAge);
        clientCookie.setPath(path);
        clientCookie.setPortlist(portlist);
        clientCookie.setVersion(version);
        clientCookie.setSecure(secure);
        clientCookie.setDiscard(discard);
        return clientCookie;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentURL() {
        return commentURL;
    }

    public void setCommentURL(String commentURL) {
        this.commentURL = commentURL;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public long getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPortlist() {
        return portlist;
    }

    public void setPortlist(String portlist) {
        this.portlist = portlist;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean getSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public boolean getDiscard() {
        return discard;
    }

    public void setDiscard(boolean discard) {
        this.discard = discard;
    }
}
