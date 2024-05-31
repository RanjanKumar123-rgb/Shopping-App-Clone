package com.proj.sac.service;

import com.proj.sac.entity.Address;
import com.proj.sac.requestdto.AddressRequest;
import com.proj.sac.util.ResponseStructure;
import org.springframework.http.ResponseEntity;

public interface AddressService 
{

	ResponseEntity<ResponseStructure<Address>> addAddress(AddressRequest addressRequest, int sellerId);

	ResponseEntity<ResponseStructure<Address>> updateAddress(AddressRequest addressRequest, int addressId);

	ResponseEntity<ResponseStructure<Address>> findAddressByAddressId(int addressId);

	ResponseEntity<ResponseStructure<Address>> findAddressByStoreId(int storeId);
	
}
