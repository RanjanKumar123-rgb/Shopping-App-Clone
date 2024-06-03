package com.proj.sac.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.proj.sac.entity.Product;
import java.util.List;



public interface ProductRepo extends MongoRepository<Product, String>
{
	List<Product> findBySellerId(int sellerId);
}
