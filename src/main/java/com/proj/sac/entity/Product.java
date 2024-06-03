package com.proj.sac.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proj.sac.enums.ProductAvailability;
import com.proj.sac.enums.ProductCategory;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product 
{
	@MongoId
	private String productId;
	private String productName;
	private String productDescription;
	private double productPrice;
	private int productQuantity;
	@Enumerated(EnumType.STRING)
	private ProductAvailability productAvailability;
	private double averageRating;
	private int totalOrders;
	@Enumerated(EnumType.STRING)
	private ProductCategory productCategory;
	
	@JsonIgnore
	private int sellerId;
}
