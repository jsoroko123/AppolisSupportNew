package com.support.objects;

/**
 * Created by Jeffery on 11/5/2015.
 */
public class ClientUser {
    public String string;
    public Object tag;

    public ClientUser(String stringPart, Object tagPart) {
        string = stringPart;
        tag = tagPart;
    }

    @Override
    public String toString() {
        return string;
    }
}
