package com.proj.sac.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proj.sac.entity.Address;
import com.proj.sac.requestdto.AddressRequest;
import com.proj.sac.service.AddressService;
import com.proj.sac.util.ResponseStructure;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("${app.base_url}")
@AllArgsConstructor
@CrossOrigin(allowCredentials = "true", origins = "http://localhost:5173/")
public class AddressController 
{
	AddressService service;
	
	@PreAuthorize(value = "hasAuthority('SELLER')")
	@PostMapping(path = "/addresses/{sellerId}")
	public ResponseEntity<ResponseStructure<Address>> addAddress(@RequestBody AddressRequest addressRequest, @PathVariable int sellerId)
	{
		return service.addAddress(addressRequest, sellerId);
	}
	
	@PreAuthorize(value = "hasAuthority('SELLER')")
	@PutMapping(path = "/addresses/{addressId}")
	public ResponseEntity<ResponseStructure<Address>> updateAddress(@RequestBody AddressRequest addressRequest, @PathVariable int addressId)
	{
		return service.updateAddress(addressRequest, addressId);
	}
	
	@PreAuthorize(value = "hasAuthority('SELLER')")
	@GetMapping(path = "/addresses/{addressId}")
	public ResponseEntity<ResponseStructure<Address>> findAddressByAddressId(@PathVariable int addressId)
	{
		return service.findAddressByAddressId(addressId);
	}
	
	@PreAuthorize(value = "hasAuthority('SELLER')")
	@GetMapping(path = "/storeId/{storeId}/addresses")
	public ResponseEntity<ResponseStructure<Address>> findAddressByStoreId(@PathVariable int storeId)
	{
		return service.findAddressByStoreId(storeId);
	}
	
}
