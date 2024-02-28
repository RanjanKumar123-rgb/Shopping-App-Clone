package com.proj.sac.serviceimpl;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.proj.sac.entity.StoreImage;
import com.proj.sac.enums.ImageType;
import com.proj.sac.exception.ImageNotFoundException;
import com.proj.sac.exception.StoreNotFoundException;
import com.proj.sac.repo.ImageRepo;
import com.proj.sac.repo.StoreRepo;
import com.proj.sac.service.ImageService;
import com.proj.sac.util.ResponseStructure;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ImageServiceImpl implements ImageService
{
	private StoreRepo storeRepo;
	private ImageRepo imageRepo;
	private ResponseStructure<String> responseStructure;
	ResponseStructure<MultipartFile> imageStructure;
	
	@Override
	public ResponseEntity<ResponseStructure<String>> addStoreImage(int storeId, MultipartFile image) 
	{
		StoreImage storedImage = storeRepo.findById(storeId).map(store -> {
			StoreImage storeImage = new StoreImage();
			storeImage.setStoreId(storeId);
			storeImage.setImageType(ImageType.LOGO);
			storeImage.setContentType(image.getContentType());
			try {
				storeImage.setImageByte(image.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			imageRepo.save(storeImage);
			store.setLogoLink(storeImage.getImageId());
			storeRepo.save(store);
			return storeImage;
		}).orElseThrow(() -> new StoreNotFoundException("Store object not found !!!"));
		
		responseStructure.setData("/api/v1/images/"+storedImage.getImageId());
		responseStructure.setMessage("Image uploaded");
		responseStructure.setStatusCode(HttpStatus.OK.value());
		
		return new ResponseEntity<ResponseStructure<String>>(responseStructure ,HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<byte[]> getStoreImage(String imageId) 
	{
	    return imageRepo.findById(imageId).map(image -> {
	        return ResponseEntity.ok()
	        		.contentType(MediaType.valueOf(image.getContentType()))
	        		.contentLength(image.getImageByte().length)
	        		.body(image.getImageByte());
	    }).orElseThrow(() -> new ImageNotFoundException("Image not found !!!"));
	}

}
