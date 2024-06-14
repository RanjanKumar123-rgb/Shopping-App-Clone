package com.proj.sac.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proj.sac.entity.Product;
import com.proj.sac.requestdto.ProductRequest;
import com.proj.sac.requestdto.ProductRequestDto;
import com.proj.sac.service.ProductService;
import com.proj.sac.util.ResponseStructure;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("${app.base_url}")
@AllArgsConstructor
@CrossOrigin(allowCredentials = "true", origins = "http://localhost:5173/")
public class ProductController 
{
	ProductService service;
	
	@PreAuthorize(value = "hasAuthority('SELLER')")
	@PostMapping(path = "/products/{sellerId}")
	public ResponseEntity<ResponseStructure<Product>> createProduct(@RequestBody ProductRequest productRequest, @PathVariable int sellerId)
	{
		return service.createProduct(productRequest, sellerId);
	}
	
	@PreAuthorize(value = "hasAuthority('SELLER')")
	@GetMapping(path = "/products/{sellerId}")
	public ResponseEntity<ResponseStructure<List<Product>>> getAllProducts(@PathVariable int sellerId)
	{
		return service.getAllProducts(sellerId);
	}
	
	@PreAuthorize(value = "hasAuthority('SELLER')")
	@PostMapping(path = "/products-search")
	public ResponseEntity<ResponseStructure<List<Product>>> findAllProducts(@RequestBody ProductRequestDto productRequest)
	{
		return service.findAllProducts(productRequest);
	}
}
