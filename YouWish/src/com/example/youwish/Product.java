package com.example.youwish;

public class Product extends Wish
{

	@com.google.gson.annotations.SerializedName("price")
	private double mPrice;
	public double getPrice() { return mPrice; }
	public void setPrice(double price) { this.mPrice = price; }

	@com.google.gson.annotations.SerializedName("ean")
	private int mEan;
	public int getEan() { return mEan; }
	public void setEan(int ean) { this.mEan = ean; }
	
	public Product(String image, String title)
	{
		super(image, title);
		this.mPrice = 0.0;
		this.mEan = 0;
		// TODO Auto-generated constructor stub
	}

	public Product(String image, String title, String desc, String location,
			String url, int priority, double price, int ean)
	{
		super(image, title, desc, location, url, priority);
		this.mPrice = price;
		this.mEan = ean;
	}
	
	
	@com.google.gson.annotations.SerializedName("id")
	private String mId;
	public String getId() { return mId;}
	public void setId(String id){this.mId = id;}
	
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Product && ((Product) o).mId == mId;
	}

}
