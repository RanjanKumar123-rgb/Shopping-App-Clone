package com.proj.sac.repo;

import com.proj.sac.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AddressRepo extends JpaRepository<Address, Integer>
{
	
}
