package com.proj.sac.serviceimpl;

import com.proj.sac.exception.ConstraintViolationException;
import com.proj.sac.exception.SellerNotFoundException;
import com.proj.sac.exception.StoreNotFoundException;
import com.proj.sac.repo.SellerRepo;
import com.proj.sac.repo.StoreRepo;
import com.proj.sac.requestdto.StoreRequest;
import com.proj.sac.entity.Store;
import com.proj.sac.mappers.StoreMappers;
import com.proj.sac.service.StoreService;
import com.proj.sac.util.ResponseStructure;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StoreServiceImpl implements StoreService {
    private final StoreRepo storeRepo;
    private final SellerRepo sellerRepo;
    private final ResponseStructure<Store> storeStructure;
    private final StoreMappers storeMappers;


    public StoreServiceImpl(StoreRepo storeRepo, ResponseStructure<Store> storeStructure, SellerRepo sellerRepo, StoreMappers storeMappers) {
        super();
        this.storeRepo = storeRepo;
        this.storeStructure = storeStructure;
        this.sellerRepo = sellerRepo;
        this.storeMappers = storeMappers;
    }

    @Override
    public ResponseEntity<ResponseStructure<Store>> createStore(StoreRequest storeRequest, HttpServletResponse response, int sellerId) {
        return sellerRepo.findById(sellerId).map(seller -> {
            if (seller.getStore() != null)
                throw new ConstraintViolationException("1 Seller can create only 1 Store");
            else {
                Store store = storeMappers.mapToStore(storeRequest);
                storeRepo.save(store);
                seller.setStore(store);
                sellerRepo.save(seller);

                storeStructure.setMessage("Store Created");
                storeStructure.setStatusCode(HttpStatus.OK.value());
                storeStructure.setData(store);
                return new ResponseEntity<>(storeStructure, HttpStatus.OK);
            }
        }).orElseThrow(() -> new SellerNotFoundException("Failed to locate Seller"));

    }

    @Override
    public ResponseEntity<ResponseStructure<Store>> updateStore(StoreRequest storeRequest, HttpServletResponse response, int storeId) {
        return storeRepo.findById(storeId).map(store -> {
            store.setStoreName(storeRequest.getStoreName());
            store.setAbout(storeRequest.getAbout());
            storeRepo.save(store);

            storeStructure.setMessage("Updated");
            storeStructure.setStatusCode(HttpStatus.OK.value());
            storeStructure.setData(store);
            return new ResponseEntity<>(storeStructure, HttpStatus.OK);
        }).orElseThrow(() -> new StoreNotFoundException("Store not found"));

    }

    @Override
    public ResponseEntity<ResponseStructure<Store>> findStoreByStoreId(HttpServletResponse response, int storeId) {
        return storeRepo.findById(storeId).map(store -> {
            storeStructure.setData(store);
            storeStructure.setStatusCode(HttpStatus.FOUND.value());
            storeStructure.setMessage("Fetched Store Data using Store ID");

            return new ResponseEntity<>(storeStructure, HttpStatus.OK);
        }).orElseThrow(() -> new StoreNotFoundException("Store doesn't exist !!!"));

    }

    @Override
    public ResponseEntity<ResponseStructure<Store>> findStoreBySellerId(HttpServletResponse response, int sellerId) {
        return sellerRepo.findById(sellerId).map(seller -> {
            Store store = seller.getStore();

            storeStructure.setData(store);
            storeStructure.setStatusCode(HttpStatus.FOUND.value());
            storeStructure.setMessage("Fetched Store Data using Seller ID");

            return new ResponseEntity<>(storeStructure, HttpStatus.OK);
        }).orElseThrow(() -> new SellerNotFoundException("Seller not found !!!"));

    }
}
