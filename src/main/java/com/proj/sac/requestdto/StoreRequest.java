package com.proj.sac.requestdto;

import jakarta.annotation.Nullable;
import lombok.Data;

@Data
public class StoreRequest 
{
	private String storeName;
	@Nullable
	private String about;
}
