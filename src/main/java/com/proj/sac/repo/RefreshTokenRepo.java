package com.proj.sac.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proj.sac.entity.RefreshToken;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Integer>
{

}
