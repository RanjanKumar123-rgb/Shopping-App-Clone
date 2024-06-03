package com.proj.sac.requestdto;

import lombok.Data;

@Data
public class ProductRequest 
{
	private String productName;
	private String productDescription;
	private double productPrice;
	private int productQuantity;
}
