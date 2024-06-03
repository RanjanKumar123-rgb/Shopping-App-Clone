package com.proj.sac.serviceimpl;

import com.proj.sac.entity.Store;
import com.proj.sac.exception.ConstraintViolationException;
import com.proj.sac.exception.SellerNotFoundException;
import com.proj.sac.exception.StoreNotFoundException;
import com.proj.sac.repo.SellerRepo;
import com.proj.sac.repo.StoreRepo;
import com.proj.sac.requestdto.StoreRequest;
import com.proj.sac.service.StoreService;
import com.proj.sac.util.ResponseStructure;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
		sellerRepo.findById(sellerId).ifPresentOrElse(
				seller -> {
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
					}
				}, () -> {
					throw new StoreNotFoundException("Failed to locate store");
				}
		);
		return new ResponseEntity<>(storeStructure,HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<ResponseStructure<Store>> updateStore(StoreRequest storeRequest, HttpServletResponse response, int storeId) 
	{
		storeRepo.findById(storeId).ifPresentOrElse(
				store -> {
					store.setStoreName(storeRequest.getStoreName());
					store.setAbout(storeRequest.getAbout());
					storeRepo.save(store);

					storeStructure.setMessage("Updated");
					storeStructure.setStatusCode(HttpStatus.OK.value());
					storeStructure.setData(store);
				}, () -> {
					throw new StoreNotFoundException("Store not found");
				}
		);
        return new ResponseEntity<>(storeStructure,HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<ResponseStructure<Store>> findStoreByStoreId(HttpServletResponse response, int storeId) 
	{
		Optional<Store> storeOptional = storeRepo.findById(storeId);
		storeOptional.ifPresentOrElse(
				store -> {
					storeStructure.setData(store);
					storeStructure.setStatusCode(HttpStatus.FOUND.value());
					storeStructure.setMessage("Fetched Store Data using Store ID");
				}, () -> {
					throw new StoreNotFoundException("Store doesn't exist !!!");
				});
			return new ResponseEntity<>(storeStructure, HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<ResponseStructure<Store>> findStoreBySellerId(HttpServletResponse response, int sellerId) 
	{
		sellerRepo.findById(sellerId).ifPresentOrElse(
				seller -> {
					Store store = seller.getStore();

					storeStructure.setData(store);
					storeStructure.setStatusCode(HttpStatus.FOUND.value());
					storeStructure.setMessage("Fetched Store Data using Seller ID");
				}, () -> {
					throw new SellerNotFoundException("Seller not found !!!");
				}
		);
		return new ResponseEntity<>(storeStructure, HttpStatus.OK);
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
