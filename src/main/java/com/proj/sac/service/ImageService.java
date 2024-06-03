package com.proj.sac.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.proj.sac.util.ResponseStructure;

public interface ImageService 
{
	ResponseEntity<ResponseStructure<String>> addStoreImage(int storeId, MultipartFile image);
	ResponseEntity<byte[]> getStoreImage(String imageId);
}
