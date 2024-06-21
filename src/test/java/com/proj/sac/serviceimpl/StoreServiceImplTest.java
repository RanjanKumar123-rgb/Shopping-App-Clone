package com.proj.sac.serviceimpl;

import com.proj.sac.entity.Seller;
import com.proj.sac.entity.Store;
import com.proj.sac.exception.ConstraintViolationException;
import com.proj.sac.exception.SellerNotFoundException;
import com.proj.sac.exception.StoreNotFoundException;
import com.proj.sac.mappers.StoreMappers;
import com.proj.sac.repo.SellerRepo;
import com.proj.sac.repo.StoreRepo;
import com.proj.sac.requestdto.StoreRequest;
import com.proj.sac.util.ResponseStructure;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class StoreServiceImplTest {

    @MockBean
    private StoreRepo storeRepo;

    @MockBean
    private SellerRepo sellerRepo;

    @MockBean
    private StoreMappers storeMappers;

    @MockBean
    private ResponseStructure<Store> storeStructure;

    @Autowired
    private StoreServiceImpl storeService;

    @Test
    void testCreateStore_Success() {
        StoreRequest storeRequest = new StoreRequest();
        storeRequest.setStoreName("Test Store");
        storeRequest.setAbout("About Test Store");

        Seller seller = new Seller();
        seller.setUserId(1);
        seller.setStore(null);

        Store store = new Store();
        store.setStoreName("Test Store");
        store.setAbout("About Test Store");

        when(sellerRepo.findById(1)).thenReturn(Optional.of(seller));
        when(storeMappers.mapToStore(storeRequest)).thenReturn(store);
        when(storeRepo.save(any(Store.class))).thenReturn(store);
        when(sellerRepo.save(any(Seller.class))).thenReturn(seller);

        ResponseEntity<ResponseStructure<Store>> response = storeService.createStore(storeRequest, mock(HttpServletResponse.class), 1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(storeStructure).setMessage("Store Created");
        verify(storeStructure).setStatusCode(HttpStatus.OK.value());
        verify(storeStructure).setData(store);
        verify(storeRepo, times(1)).save(store);
        verify(sellerRepo, times(1)).save(seller);
    }

    @Test
    void testCreateStore_SellerNotFound() {
        StoreRequest storeRequest = new StoreRequest();

        when(sellerRepo.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(SellerNotFoundException.class, () -> {
            storeService.createStore(storeRequest, mock(HttpServletResponse.class), 1);
        });

        assertEquals("Failed to locate Seller", exception.getMessage());
    }

    @Test
    void testCreateStore_ConstraintViolation() {
        StoreRequest storeRequest = new StoreRequest();

        Seller seller = new Seller();
        seller.setUserId(1);
        seller.setStore(new Store());

        when(sellerRepo.findById(1)).thenReturn(Optional.of(seller));

        Exception exception = assertThrows(ConstraintViolationException.class, () -> {
            storeService.createStore(storeRequest, mock(HttpServletResponse.class), 1);
        });

        assertEquals("1 Seller can create only 1 Store", exception.getMessage());
    }

    @Test
    void testUpdateStore_Success() {
        StoreRequest storeRequest = new StoreRequest();
        storeRequest.setStoreName("Updated Store");
        storeRequest.setAbout("Updated About Store");

        Store store = new Store();
        store.setStoreName("Test Store");
        store.setAbout("About Test Store");

        when(storeRepo.findById(1)).thenReturn(Optional.of(store));
        when(storeRepo.save(any(Store.class))).thenReturn(store);

        ResponseEntity<ResponseStructure<Store>> response = storeService.updateStore(storeRequest, mock(HttpServletResponse.class), 1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(storeStructure).setMessage("Updated");
        verify(storeStructure).setStatusCode(HttpStatus.OK.value());
        verify(storeStructure).setData(store);
        verify(storeRepo, times(1)).save(store);
    }

    @Test
    void testUpdateStore_StoreNotFound() {
        StoreRequest storeRequest = new StoreRequest();

        when(storeRepo.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(StoreNotFoundException.class, () -> {
            storeService.updateStore(storeRequest, mock(HttpServletResponse.class), 1);
        });

        assertEquals("Store not found", exception.getMessage());
    }

    @Test
    void testFindStoreByStoreId_Success() {
        Store store = new Store();
        store.setStoreName("Test Store");

        when(storeRepo.findById(1)).thenReturn(Optional.of(store));

        ResponseEntity<ResponseStructure<Store>> response = storeService.findStoreByStoreId(mock(HttpServletResponse.class), 1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(storeStructure).setData(store);
        verify(storeStructure).setStatusCode(HttpStatus.FOUND.value());
        verify(storeStructure).setMessage("Fetched Store Data using Store ID");
    }

    @Test
    void testFindStoreByStoreId_StoreNotFound() {
        when(storeRepo.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(StoreNotFoundException.class, () -> {
            storeService.findStoreByStoreId(mock(HttpServletResponse.class), 1);
        });

        assertEquals("Store doesn't exist !!!", exception.getMessage());
    }

    @Test
    void testFindStoreBySellerId_Success() {
        Seller seller = new Seller();
        Store store = new Store();
        seller.setStore(store);

        when(sellerRepo.findById(1)).thenReturn(Optional.of(seller));

        ResponseEntity<ResponseStructure<Store>> response = storeService.findStoreBySellerId(mock(HttpServletResponse.class), 1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(storeStructure).setData(store);
        verify(storeStructure).setStatusCode(HttpStatus.FOUND.value());
        verify(storeStructure).setMessage("Fetched Store Data using Seller ID");
    }

    @Test
    void testFindStoreBySellerId_SellerNotFound() {
        when(sellerRepo.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(SellerNotFoundException.class, () -> {
            storeService.findStoreBySellerId(mock(HttpServletResponse.class), 1);
        });

        assertEquals("Seller not found !!!", exception.getMessage());
    }
}
