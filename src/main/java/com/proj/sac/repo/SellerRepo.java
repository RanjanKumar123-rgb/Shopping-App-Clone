package com.proj.sac.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proj.sac.entity.Seller;

@Repository
public interface SellerRepo extends JpaRepository<Seller, Integer>
{

}
