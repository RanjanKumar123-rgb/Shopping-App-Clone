package com.proj.sac.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.proj.sac.service.ImageService;
import com.proj.sac.util.ResponseStructure;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("${app.base_url}")
@AllArgsConstructor
@CrossOrigin(allowCredentials = "true", origins = "http://localhost:5173/")
public class ImageController 
{
	private ImageService imageService;
	
	@PostMapping("/stores/{storeId}/images")
	public ResponseEntity<ResponseStructure<String>> addStoreImage(@PathVariable int storeId, MultipartFile image)
	{
		return imageService.addStoreImage(storeId, image);
	}
	
	@GetMapping("/images/{imageId}")
	public ResponseEntity<byte[]> getStoreImage(@PathVariable String imageId)
	{
		return imageService.getStoreImage(imageId);
	}
}