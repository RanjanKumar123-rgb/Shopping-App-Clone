package com.proj.sac.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proj.sac.entity.Address;
import com.proj.sac.entity.Contact;


public interface AddressRepo extends JpaRepository<Address, Integer>
{
	
}
