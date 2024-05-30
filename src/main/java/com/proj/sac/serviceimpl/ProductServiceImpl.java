package com.proj.sac.serviceimpl;

import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.proj.sac.entity.Product;
import com.proj.sac.enums.ProductAvailability;
import com.proj.sac.exception.ProductNotFoundException;
import com.proj.sac.exception.SellerNotFoundException;
import com.proj.sac.repo.ProductRepo;
import com.proj.sac.repo.SellerRepo;
import com.proj.sac.requestdto.ProductRequest;
import com.proj.sac.requestdto.ProductRequestDto;
import com.proj.sac.service.ProductService;
import com.proj.sac.util.ResponseStructure;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService
{
	private ProductRepo productRepo;
	private SellerRepo sellerRepo;
	private ResponseStructure<Product> productStructure;
	private ResponseStructure<List<Product>> productListStructure;
	private MongoTemplate mongoTemplate;
	
	@Override
	public ResponseEntity<ResponseStructure<Product>> createProduct(ProductRequest productRequest, int sellerId) 
	{
		sellerRepo.findById(sellerId).orElseThrow(() -> new SellerNotFoundException("Seller not present !!!"));
		ProductAvailability availability;
		if(productRequest.getProductQuantity()>=100)
			availability = ProductAvailability.ACTIVE;
		else if (productRequest.getProductQuantity()<100 && productRequest.getProductQuantity()>0)
			availability = ProductAvailability.LOW_STOCK;
		else
			availability = ProductAvailability.OUT_OF_STOCK;
		Product product = mapToProduct(productRequest, sellerId, availability);
		
		productRepo.save(product);
		
		productStructure.setData(product);
		productStructure.setMessage("Product Added !!!");
		productStructure.setStatusCode(HttpStatus.CREATED.value());
		
		return new ResponseEntity<ResponseStructure<Product>>(productStructure, HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<ResponseStructure<List<Product>>> getAllProducts(int sellerId) 
	{
		sellerRepo.findById(sellerId).orElseThrow(() -> new SellerNotFoundException("Seller not found"));
		
		List<Product> products = productRepo.findBySellerId(sellerId);
		if(products == null)
			throw new ProductNotFoundException("Products not found");
		
		productListStructure.setData(products);
		productListStructure.setMessage("List of products fetched");
		productListStructure.setStatusCode(HttpStatus.FOUND.value());
		
		return new ResponseEntity<ResponseStructure<List<Product>>>(productListStructure, HttpStatus.OK);	
	}

	@Override
	public ResponseEntity<ResponseStructure<List<Product>>> findAllProducts(ProductRequestDto productRequest) {
		Criteria criteria = new Criteria();
		
		if(productRequest.getProductName()!=null)
			criteria.and("productName").regex(productRequest.getProductName(),"i");
		if(productRequest.getProductAvailability()!=null)
			criteria.and("productAvailability").is(productRequest.getProductAvailability());
		if(productRequest.getProductCategory()!=null)
			criteria.and("productCategory").is(productRequest.getProductCategory());
		if(productRequest.getMaxPrice()!=0) 
			criteria.and("maxPrice").lte(productRequest.getMaxPrice());
		if(productRequest.getMinPrice()!=0)
			criteria.and("minPrice").lte(productRequest.getMinPrice());
		
		Query query = new Query(criteria);
		System.out.println(query.getQueryObject());
		List<Product> products = mongoTemplate.find(query, Product.class);
		System.out.println("Product Data "+products);
		
		return null;
	}
	
	
	
//	=====================================================================================================================================
	
	private Product mapToProduct(ProductRequest productRequest, int sellerId, ProductAvailability availability) {
		return Product.builder()
				.productName(productRequest.getProductName())
				.productDescription(productRequest.getProductDescription())
				.productPrice(productRequest.getProductPrice())
				.productQuantity(productRequest.getProductQuantity())
				.productAvailability(availability)
				.sellerId(sellerId)
				.build();
	}	
}
