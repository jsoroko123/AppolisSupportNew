package com.support.objects;

/**
 * Created by Jeffery on 11/6/2015.
 */
public class CaseReason {
    public String string;
    public Object tag;

    public CaseReason(String stringPart, Object tagPart) {
        string = stringPart;
        tag = tagPart;
    }

    @Override
    public String toString() {
        return string;
    }
}
