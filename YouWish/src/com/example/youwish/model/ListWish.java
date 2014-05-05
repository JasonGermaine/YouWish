package com.example.youwish.model;

public class ListWish
{
	private Product p;
	private BucketList b;
	
	public ListWish()
	{
	}

	public Product getProduct()
	{
		return p;
	}

	public void setProduct(Product p)
	{
		this.p = p;
	}

	public BucketList getBucketList()
	{
		return b;
	}

	public void setBucketList(BucketList b)
	{
		this.b = b;
	}
	
}
