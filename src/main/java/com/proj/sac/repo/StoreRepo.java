package com.proj.sac.repo;

import com.proj.sac.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;


public interface StoreRepo extends JpaRepository<Store, Integer> 
{
	
}
