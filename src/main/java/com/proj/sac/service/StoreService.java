package com.proj.sac.service;

import org.springframework.http.ResponseEntity;

import com.proj.sac.entity.Store;
import com.proj.sac.requestdto.StoreRequest;
import com.proj.sac.util.ResponseStructure;
import com.proj.sac.util.SimpleResponseStructure;

import jakarta.servlet.http.HttpServletResponse;

public interface StoreService
{
	ResponseEntity<ResponseStructure<Store>> createStore(StoreRequest storeRequest, HttpServletResponse response, int sellerId);

	ResponseEntity<ResponseStructure<Store>> updateStore(StoreRequest storeRequest, HttpServletResponse response, int storeId);

	ResponseEntity<ResponseStructure<Store>> findStoreByStoreId(HttpServletResponse response, int storeId);

	ResponseEntity<ResponseStructure<Store>> findStoreBySellerId(HttpServletResponse response, int sellerId);

}
