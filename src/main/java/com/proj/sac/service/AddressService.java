package com.proj.sac.service;

import org.springframework.http.ResponseEntity;

import com.proj.sac.entity.Address;
import com.proj.sac.requestdto.AddressRequest;
import com.proj.sac.util.ResponseStructure;
import com.proj.sac.util.SimpleResponseStructure;

public interface AddressService 
{

	ResponseEntity<ResponseStructure<Address>> addAddress(AddressRequest addressRequest, int sellerId);

	ResponseEntity<ResponseStructure<Address>> updateAddress(AddressRequest addressRequest, int addressId);

	ResponseEntity<ResponseStructure<Address>> findAddresssByAddressId(int addressId);

	ResponseEntity<ResponseStructure<Address>> findAddresssByStoreId(int storeId);
	
}
