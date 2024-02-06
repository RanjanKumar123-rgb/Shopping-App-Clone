package com.proj.sac.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proj.sac.entity.User;

@Repository
public interface UserRepo extends JpaRepository<User, Integer>
{

}
