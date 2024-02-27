package com.proj.sac.serviceimpl;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.proj.sac.entity.Seller;
import com.proj.sac.entity.Store;
import com.proj.sac.exception.ConstraintViolationException;
import com.proj.sac.exception.StoreNotFoundException;
import com.proj.sac.repo.SellerRepo;
import com.proj.sac.repo.StoreRepo;
import com.proj.sac.requestdto.StoreRequest;
import com.proj.sac.service.StoreService;
import com.proj.sac.util.ResponseStructure;
import com.proj.sac.util.SimpleResponseStructure;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StoreServiceImpl implements StoreService
{
	private StoreRepo storeRepo;
	private SellerRepo sellerRepo;
	private ResponseStructure<Store> storeStructure;
	

	public StoreServiceImpl(StoreRepo storeRepo,  
			ResponseStructure<Store> storeStructure,
			SellerRepo sellerRepo) {
		super();
		this.storeRepo = storeRepo;
		this.storeStructure = storeStructure;
		this.sellerRepo = sellerRepo;
	}

	@Override
	public ResponseEntity<ResponseStructure<Store>> createStore(StoreRequest storeRequest, HttpServletResponse response, int sellerId) 
	{
		Seller seller = sellerRepo.findById(sellerId).get();
		if(seller.getStore()!=null)
			throw new ConstraintViolationException("1 Seller can create only 1 Store");
		else
		{
			Store store = mapToStore(storeRequest);
			storeRepo.save(store);
			seller.setStore(store);
			sellerRepo.save(seller);
					
			storeStructure.setMessage("Store Created");
			storeStructure.setStatusCode(HttpStatus.OK.value());
			storeStructure.setData(store);
			        
			return new ResponseEntity<ResponseStructure<Store>>(storeStructure,HttpStatus.OK);
		}
	}
	
	@Override
	public ResponseEntity<ResponseStructure<Store>> updateStore(StoreRequest storeRequest, HttpServletResponse response, int storeId) 
	{
		Store store = storeRepo.findById(storeId).get();
		store.setStoreName(storeRequest.getStoreName());
		store.setAbout(storeRequest.getAbout());
		storeRepo.save(store);
		
		storeStructure.setMessage("Updated");
		storeStructure.setStatusCode(HttpStatus.OK.value());
		storeStructure.setData(store);
        
        return new ResponseEntity<ResponseStructure<Store>>(storeStructure,HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<ResponseStructure<Store>> findStoreByStoreId(HttpServletResponse response, int storeId) 
	{
		Optional<Store> stores = storeRepo.findById(storeId);
		if(stores == null)
			throw new StoreNotFoundException("Store doesnt exist !!!");
		else
		{
			Store store = stores.get();
			
			storeStructure.setData(store);
			storeStructure.setStatusCode(HttpStatus.FOUND.value());
			storeStructure.setMessage("Fetched Store Data using Store ID");
			
			return new ResponseEntity<ResponseStructure<Store>>(storeStructure, HttpStatus.OK);
		}
	}
	
	@Override
	public ResponseEntity<ResponseStructure<Store>> findStoreBySellerId(HttpServletResponse response, int sellerId) 
	{
		Seller seller = sellerRepo.findById(sellerId).get();
		Store store = seller.getStore();
		
		storeStructure.setData(store);
		storeStructure.setStatusCode(HttpStatus.FOUND.value());
		storeStructure.setMessage("Fetched Store Data using Seller ID");
		
		return new ResponseEntity<ResponseStructure<Store>>(storeStructure, HttpStatus.OK);
	}
	
	
	
// =====================================================================================================================================================
	
	private Store mapToStore(StoreRequest storeRequest) 
	{
		return Store.builder()
				.storeName(storeRequest.getStoreName())
				.about(storeRequest.getAbout())
				.build();
	}

}
