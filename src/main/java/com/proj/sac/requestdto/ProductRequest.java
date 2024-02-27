package com.proj.sac.requestdto;

import com.proj.sac.enums.ProductAvailability;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class ProductRequest 
{
	private int productId;
	private String productName;
	private String productDescription;
	private double productPrice;
	private int productQuantity;
	@Enumerated(EnumType.STRING)
	private ProductAvailability productAvailability;
	private double averageRating;
	private int totalOrders;
}
