package com.proj.sac.requestdto;

import com.proj.sac.enums.ProductAvailability;
import com.proj.sac.enums.ProductCategory;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class ProductRequestDto 
{
	private String productName;
	private int minPrice;
	private int maxPrice;
	@Enumerated(EnumType.STRING)
	private ProductCategory productCategory;
	private ProductAvailability productAvailability;
}
