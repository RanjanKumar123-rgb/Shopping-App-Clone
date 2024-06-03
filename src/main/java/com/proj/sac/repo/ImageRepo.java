package com.proj.sac.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.proj.sac.entity.Image;


public interface ImageRepo extends MongoRepository<Image, String>
{
	
}
