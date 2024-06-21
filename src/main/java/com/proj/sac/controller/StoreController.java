package com.proj.sac.controller;

import com.proj.sac.requestdto.StoreRequest;
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

import com.proj.sac.entity.Store;
import com.proj.sac.service.StoreService;
import com.proj.sac.util.ResponseStructure;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("${app.base_url}")
@AllArgsConstructor
@CrossOrigin(allowCredentials = "true", origins = "http://localhost:5173/")
public class StoreController 
{
	StoreService service;
	
	@PreAuthorize(value = "hasAuthority('SELLER')")
	@PostMapping(path = "/stores/{sellerId}")
	public ResponseEntity<ResponseStructure<Store>> createStore(@RequestBody StoreRequest storeRequest, HttpServletResponse response, @PathVariable int sellerId)
	{
		return service.createStore(storeRequest, response, sellerId);
	}
	
	@PreAuthorize(value = "hasAuthority('SELLER')")
	@PutMapping(path = "/stores/{storeId}")
	public ResponseEntity<ResponseStructure<Store>> updateStore(@RequestBody StoreRequest storeRequest, HttpServletResponse response, @PathVariable int storeId)
	{
		return service.updateStore(storeRequest, response, storeId);
	}
	
	@PreAuthorize(value = "hasAuthority('SELLER')")
	@GetMapping(path = "/stores/{storeId}")
	public ResponseEntity<ResponseStructure<Store>> findStoreByStoreId(HttpServletResponse response, @PathVariable int storeId)
	{
		return service.findStoreByStoreId(response, storeId);
	}
	
	@PreAuthorize(value = "hasAuthority('SELLER')")
	@GetMapping(path = "/sellerId/{sellerId}/stores")
	public ResponseEntity<ResponseStructure<Store>> findStoreBySellerId(HttpServletResponse response, @PathVariable int sellerId)
	{
		return service.findStoreBySellerId(response, sellerId);
	}
}
