package com.support.objects;

import java.util.ArrayList;

/**
 * Created by Jeffery on 11/23/2015.
 */
public class ErrorParent {

        private String ClientName;
        private String Object;
        private String ObjectID;
        private String ObjectNumber;
        private String DateCreated;
        public boolean HasResolution;

        private ArrayList<ErrorChild> children = new ArrayList<ErrorChild>();

        public ErrorParent(String mClientName, String mObject, String mObjectID, String mObjectNumber, String mDateCreated, boolean mHasResolution, ArrayList<ErrorChild> childList) {
            super();
            this.ClientName = mClientName;
            this.Object = mObject;
            this.ObjectID = mObjectID;
            this.ObjectNumber = mObjectNumber;
            this.DateCreated = mDateCreated;
            this.HasResolution = mHasResolution;
            this.children = childList;


        }

        public ErrorParent() {
            super();
        }

        public String getClientName() {
            return ClientName;
        }

        public void setClientName(String clientName) {
            ClientName = clientName;
        }

        public String getObject() {
            return Object;
        }

        public void setObject(String object) {
            Object = object;
        }

        public String getObjectID() {
            return ObjectID;
        }

        public void setObjectID(String objectID) {
            ObjectID = objectID;
        }

        public String getObjectNumber() {
            return ObjectNumber;
        }

        public void setObjectNumber(String objectNumber) {
            ObjectNumber = objectNumber;
        }

        public String getDateCreated() {
            return DateCreated;
        }

        public void setDateCreated(String dateCreated) {
            DateCreated = dateCreated;
        }

        public boolean isHasResolution() {
            return HasResolution;
        }

        public void setHasResolution(boolean hasResolution) {
            HasResolution = hasResolution;
        }

        public ArrayList<ErrorChild> getChildList() {
                return children;
            }
            public void setChildList(ArrayList<ErrorChild> childList) {
                this.children = childList;
            };


        }

