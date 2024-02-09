package com.proj.sac.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proj.sac.entity.AccessToken;

public interface AccessTokenRepo extends JpaRepository<AccessToken, Integer>
{

}
