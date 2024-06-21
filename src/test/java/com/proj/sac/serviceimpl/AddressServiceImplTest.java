package com.proj.sac.serviceimpl;

import com.proj.sac.entity.Address;
import com.proj.sac.entity.Seller;
import com.proj.sac.entity.Store;
import com.proj.sac.enums.AddressType;
import com.proj.sac.exception.AddressNotFoundException;
import com.proj.sac.exception.SellerNotFoundException;
import com.proj.sac.exception.StoreNotFoundException;
import com.proj.sac.mappers.AddressMappers;
import com.proj.sac.repo.AddressRepo;
import com.proj.sac.repo.SellerRepo;
import com.proj.sac.repo.StoreRepo;
import com.proj.sac.requestdto.AddressRequest;
import com.proj.sac.util.ResponseStructure;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//Test Case done using JUnit
@SpringBootTest
@ActiveProfiles("test")
class AddressServiceImplTest {

    @Autowired
    private AddressServiceImpl addressServiceImpl;

    @MockBean
    private SellerRepo sellerRepo;

    @MockBean
    private AddressRepo addressRepo;

    @MockBean
    private StoreRepo storeRepo;

    @MockBean
    private AddressMappers addressMappers;

    @Test
    void testAddAddress_Success() {

        // Arrangement
        int sellerId = 123;

        Store store = new Store();

        Seller seller = new Seller();
        seller.setUserId(sellerId);
        seller.setStore(store);

        AddressRequest addressRequest = AddressRequest.builder().build();

        Address address = new Address();

        System.out.println(seller);
        when(sellerRepo.findById(sellerId)).thenReturn(Optional.of(seller));
        when(addressMappers.mapToAddress(any(AddressRequest.class), any(Address.class))).thenReturn(address);
        when(addressRepo.save(any(Address.class))).thenReturn(address);
        when(storeRepo.save(any(Store.class))).thenReturn(store);

        System.out.println(seller);
        ResponseStructure<Address> response = addressServiceImpl.addAddress(addressRequest, sellerId).getBody();

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());
        assertEquals("Address Successfully Added !!", response.getMessage());
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());
        assertEquals(address, response.getData());

        verify(sellerRepo, times(1)).findById(anyInt());
        verify(addressRepo, times(1)).save(any(Address.class));
        verify(storeRepo, times(1)).save(any(Store.class));
    }

    @Test
    void testAddAddress_SellerNotFound() {
        when(sellerRepo.findById(anyInt())).thenReturn(Optional.empty());
        AddressRequest addressRequest = AddressRequest.builder().build();

        Exception exception = assertThrows(SellerNotFoundException.class, () -> {
            addressServiceImpl.addAddress(addressRequest, 1);
        });

        assertEquals("Failed to Add Address", exception.getMessage());
        verify(sellerRepo, times(1)).findById(1);
        verify(addressRepo, times(0)).save(any(Address.class));
        verify(storeRepo, times(0)).save(any(Store.class));
    }

    @Test
    void testUpdateAddress_Success() {
        Address existingAddress = new Address();
        Address updatedAddress = new Address();

        when(addressRepo.findById(1)).thenReturn(Optional.of(existingAddress));
        when(addressRepo.save(existingAddress)).thenReturn(updatedAddress);

        AddressRequest addressRequest = AddressRequest.builder().build();
        ResponseStructure<Address> response = addressServiceImpl.updateAddress(addressRequest, 1).getBody();

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("Address Successfully Updated !!", response.getMessage());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals(updatedAddress, response.getData());

        verify(addressRepo, times(1)).findById(1);
        verify(addressMappers, times(1)).mapToAddress(addressRequest, existingAddress);
        verify(addressRepo, times(1)).save(existingAddress);
    }

    @Test
    void testUpdateAddress_AddressNotFound() {
        AddressRequest addressRequest = AddressRequest.builder().build();
        when(addressRepo.findById(1)).thenReturn(Optional.empty());

        AddressNotFoundException thrown = assertThrows(AddressNotFoundException.class, () -> {
            addressServiceImpl.updateAddress(addressRequest, 1);
        });

        assertEquals("Failed to locate Address", thrown.getMessage());

        verify(addressRepo, times(1)).findById(1);
        verify(addressMappers, times(0)).mapToAddress(any(), any());
        verify(addressRepo, times(0)).save(any());
    }

    @Test
    void testFindAddressByAddressId_Success() {
        Address address = new Address();
        address.setStreetAddress("Btm Layout");
        address.setStreetAddressAdditional(null);
        address.setCity("Bangalore");
        address.setState("Karnataka");
        address.setCountry("INDIA");
        address.setPinCode(560029);
        address.setAddressType(AddressType.HOME);

        when(addressRepo.findById(1)).thenReturn(Optional.of(address));

        ResponseStructure<Address> response = addressServiceImpl.findAddressByAddressId(1).getBody();

        assertNotNull(response);
        assertEquals(HttpStatus.FOUND.value(), response.getStatusCode());
        assertEquals("Address fetched by addressId", response.getMessage());
        assertEquals(HttpStatus.FOUND.value(), response.getStatusCode());
        assertEquals(address, response.getData());

        verify(addressRepo, times(1)).findById(1);
    }

    @Test
    void testFindAddressByAddressId_AddressNotFound() {
        when(addressRepo.findById(1)).thenReturn(Optional.empty());

        AddressNotFoundException thrown = assertThrows(AddressNotFoundException.class, () -> {
            addressServiceImpl.findAddressByAddressId(1);
        });

        assertEquals("Address not added !!", thrown.getMessage());

        verify(addressRepo, times(1)).findById(1);
    }

    @Test
    void testFindAddressByStoreId_Success() {
        Address address = new Address();
        Store store = new Store();
        store.setAddress(address);

        when(storeRepo.findById(anyInt())).thenReturn(Optional.of(store));

        ResponseEntity<ResponseStructure<Address>> response = addressServiceImpl.findAddressByStoreId(anyInt());

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Address fetched using storeId", response.getBody().getMessage());
        assertEquals(HttpStatus.FOUND.value(), response.getBody().getStatusCode());
        assertEquals(address, response.getBody().getData());
    }

    @Test
    void testFindAddressByStoreId_StoreNotFound() {
        when(storeRepo.findById(1)).thenReturn(Optional.empty());

        StoreNotFoundException thrown = assertThrows(StoreNotFoundException.class, () -> {
            addressServiceImpl.findAddressByStoreId(1);
        });

        assertEquals("Failed to locate Store", thrown.getMessage());

        verify(storeRepo, times(1)).findById(1);
    }
}