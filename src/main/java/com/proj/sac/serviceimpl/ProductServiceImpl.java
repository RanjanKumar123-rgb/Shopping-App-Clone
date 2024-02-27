package com.proj.sac.serviceimpl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.proj.sac.entity.Product;
import com.proj.sac.entity.Seller;
import com.proj.sac.enums.ProductAvailability;
import com.proj.sac.exception.SellerNotFoundException;
import com.proj.sac.repo.ProductRepo;
import com.proj.sac.repo.SellerRepo;
import com.proj.sac.requestdto.ProductRequest;
import com.proj.sac.service.ProductService;
import com.proj.sac.util.ResponseStructure;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService
{
	ProductRepo productRepo;
	SellerRepo sellerRepo;
	ResponseStructure<Product> productStructure;
	
	@Override
	public ResponseEntity<ResponseStructure<Product>> createProduct(ProductRequest productRequest, int sellerId) 
	{
		Seller seller = sellerRepo.findById(sellerId).orElseThrow(() -> new SellerNotFoundException("Seller not present !!!"));
		ProductAvailability availability;
		if(productRequest.getProductQuantity()>10)
			availability = ProductAvailability.ACTIVE;
		else if (productRequest.getProductQuantity()<10 && productRequest.getProductQuantity()>0)
			availability = ProductAvailability.LOW_STOCK;
		else
			availability = ProductAvailability.OUT_OF_STOCK;
		Product product = mapToProduct(productRequest, seller, availability);
		
		productRepo.save(product);
		
		productStructure.setData(product);
		productStructure.setMessage("Product Added !!!");
		productStructure.setStatusCode(HttpStatus.CREATED.value());
		
		return new ResponseEntity<ResponseStructure<Product>>(productStructure, HttpStatus.OK);
	}

	private Product mapToProduct(ProductRequest productRequest, Seller seller, ProductAvailability availability) {
		return Product.builder()
				.productName(productRequest.getProductName())
				.productDescription(productRequest.getProductDescription())
				.productPrice(productRequest.getProductPrice())
				.productQuantity(productRequest.getProductQuantity())
				.productAvailability(availability)
				.seller(seller)
				.build();
	}
	
}
