package com.proj.sac.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proj.sac.entity.Customer;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, Integer>
{

}
