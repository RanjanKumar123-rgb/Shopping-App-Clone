package com.proj.sac.service;

import org.springframework.http.ResponseEntity;

import com.proj.sac.entity.Product;
import com.proj.sac.requestdto.ProductRequest;
import com.proj.sac.util.ResponseStructure;

public interface ProductService 
{
	ResponseEntity<ResponseStructure<Product>> createProduct(ProductRequest productRequest, int sellerId);	
}
