package com.proj.sac.serviceimpl;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService
{
	private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);
	private ProductRepo productRepo;
	private SellerRepo sellerRepo;
	private ResponseStructure<Product> productStructure;
	private ResponseStructure<List<Product>> productListStructure;
	private MongoTemplate mongoTemplate;
	
	@Override
	public ResponseEntity<ResponseStructure<Product>> createProduct(ProductRequest productRequest, int sellerId) 
	{
		sellerRepo.findById(sellerId).ifPresentOrElse(
				seller -> {
					ProductAvailability availability;
					if(productRequest.getProductQuantity()>=100)
						availability = ProductAvailability.ACTIVE;
					else if (productRequest.getProductQuantity()>0)
						availability = ProductAvailability.LOW_STOCK;
					else
						availability = ProductAvailability.OUT_OF_STOCK;
					Product product = mapToProduct(productRequest, sellerId, availability);

					productRepo.save(product);

					productStructure.setData(product);
					productStructure.setMessage("Product Added !!!");
					productStructure.setStatusCode(HttpStatus.CREATED.value());
				}, () -> {
					throw new SellerNotFoundException("Seller not present !!!");
				}
		);
		return new ResponseEntity<>(productStructure, HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<ResponseStructure<List<Product>>> getAllProducts(int sellerId) 
	{
		sellerRepo.findById(sellerId).ifPresentOrElse(
				seller -> {
					List<Product> products = productRepo.findBySellerId(sellerId);
					if(products == null)
						throw new ProductNotFoundException("Products not found");

					productListStructure.setData(products);
					productListStructure.setMessage("List of products fetched");
					productListStructure.setStatusCode(HttpStatus.FOUND.value());
				}, () -> {
					throw new SellerNotFoundException("Seller not found");
				}
		);
		return new ResponseEntity<>(productListStructure, HttpStatus.OK);
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
		List<Product> products = mongoTemplate.find(query, Product.class);

		productListStructure.setData(products);
		productListStructure.setMessage("All Products Fetched");
		productListStructure.setStatusCode(HttpStatus.FOUND.value());
		
		return new ResponseEntity<> (productListStructure, HttpStatus.OK);
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
