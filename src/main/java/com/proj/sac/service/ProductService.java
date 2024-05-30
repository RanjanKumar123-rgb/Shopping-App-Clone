package com.proj.sac.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.proj.sac.entity.Product;
import com.proj.sac.requestdto.ProductRequest;
import com.proj.sac.requestdto.ProductRequestDto;
import com.proj.sac.util.ResponseStructure;

public interface ProductService 
{
	ResponseEntity<ResponseStructure<Product>> createProduct(ProductRequest productRequest, int sellerId);

	ResponseEntity<ResponseStructure<List<Product>>> getAllProducts(int sellerId);

	ResponseEntity<ResponseStructure<List<Product>>> findAllProducts(ProductRequestDto productRequest);	
}
