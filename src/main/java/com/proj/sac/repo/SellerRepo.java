package com.proj.sac.repo;

import com.proj.sac.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SellerRepo extends JpaRepository<Seller, Integer>
{
	
}
