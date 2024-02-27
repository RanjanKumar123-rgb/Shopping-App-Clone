package com.proj.sac.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proj.sac.entity.Product;

public interface ProductRepo extends JpaRepository<Product, Integer>
{

}
