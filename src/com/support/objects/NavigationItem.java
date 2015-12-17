package com.support.objects;


public class NavigationItem {

	 public NavigationItem(int stringTitle, int drawableIcon)
	    {
	        this.stringTitle = stringTitle;
	        this.drawableIcon = drawableIcon;
	    }

	    public int getStringTitle() {
		return stringTitle;
	}

	public void setStringTitle(int stringTitle) {
		this.stringTitle = stringTitle;
	}

	public int getDrawableIcon() {
		return drawableIcon;
	}

	public void setDrawableIcon(int drawableIcon) {
		this.drawableIcon = drawableIcon;
	}

		/**
	     * The id of the string resource of the text of the item.
	     */
	    public int stringTitle;

	    /**
	     * The id of the drawable resource of icon of the item.
	     */
	    public int drawableIcon;
	    
}

	    /**
	     * The Fragment to be loaded upon selecting the item.
	     */
