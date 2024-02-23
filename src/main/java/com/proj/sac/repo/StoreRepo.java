package com.proj.sac.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proj.sac.entity.Store;
import java.util.List;


public interface StoreRepo extends JpaRepository<Store, Integer> 
{
	
}
