package com.proj.sac.entity;

import com.proj.sac.enums.ImageType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "image")
@Data
public class Image
{
	@Id	//@MongoId	
	private String imageId;
	private ImageType imageType;
	private byte[] imageByte;
	private String contentType;
}