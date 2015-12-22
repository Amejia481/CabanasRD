package com.cabanasrd.ui.menu;

public class MenuItem {

	/** The title1. */
	private String title1;

	/** The title2. */
	private String title2;

	/** The description. */
	private String desc;

	/** The image resource id. */
	private int imageNormal;
	
	/** The image2. */
	private int imageSelected;

	/**
	 * Instantiates a new data.
	 *
	 * @param title1 the title1
	 * @param title2 the title2
	 * @param desc the desc
	 * @param image1 the image1
	 */
	public MenuItem(String title1, String title2, String desc, int image1)
	{
		this.title1 = title1;
		this.title2 = title2;
		this.desc = desc;
		this.imageNormal = image1;
	}

	/**
	 * Instantiates a new data.
	 *
	 * @param title1 the title1
	 * @param image1 the image1
	 * @param image2 the image2
	 */
	public MenuItem(String title1, int image1, int image2)
	{
		this.title1 = title1;
		this.imageNormal = image1;
		this.imageSelected = image2;
	}

	/**
	 * Gets the title1.
	 * 
	 * @return the title1
	 */
	public String getTitle1()
	{
		return title1;
	}

	/**
	 * Sets the title1.
	 * 
	 * @param title1
	 *            the new title1
	 */
	public void setTitle1(String title1)
	{
		this.title1 = title1;
	}

	/**
	 * Gets the title2.
	 * 
	 * @return the title2
	 */
	public String getTitle2()
	{
		return title2;
	}

	/**
	 * Sets the title2.
	 * 
	 * @param title2
	 *            the new title2
	 */
	public void setTitle2(String title2)
	{
		this.title2 = title2;
	}

	/**
	 * Gets the desc.
	 * 
	 * @return the desc
	 */
	public String getDesc()
	{
		return desc;
	}

	/**
	 * Sets the desc.
	 * 
	 * @param desc
	 *            the new desc
	 */
	public void setDesc(String desc)
	{
		this.desc = desc;
	}

	/**
	 * Gets the image1.
	 * 
	 * @return the image1
	 */
	public int getImage1()
	{
		return imageNormal;
	}

	/**
	 * Sets the image1.
	 * 
	 * @param image1
	 *            the new image1
	 */
	public void setImage1(int image1)
	{
		this.imageNormal = image1;
	}

	/**
	 * Gets the image2.
	 * 
	 * @return the image2
	 */
	public int getImage2()
	{
		return imageSelected;
	}

	/**
	 * Sets the image2.
	 * 
	 * @param image2
	 *            the new image2
	 */
	public void setImage2(int image2)
	{
		this.imageSelected = image2;
	}

}
