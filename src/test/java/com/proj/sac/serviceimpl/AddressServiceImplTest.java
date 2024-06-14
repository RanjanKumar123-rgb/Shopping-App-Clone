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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddressServiceImplTest {

    @InjectMocks
    private AddressServiceImpl addressServiceImpl;

    @Mock
    private SellerRepo sellerRepo;

    @Mock
    private AddressRepo addressRepo;

    @Mock
    private StoreRepo storeRepo;

    @Mock
    private AddressMappers addressMappers;

    private AddressRequest addressRequest;
    private Seller seller;
    private Store store;
    private Address address;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        addressRequest = new AddressRequest();
        addressRequest.setAddressType(AddressType.HOME);
        addressRequest.setStreetAddress("BTM Layout");
        addressRequest.setState("Karnataka");
        addressRequest.setCountry("INDIA");
        addressRequest.setCity("Bangalore");
        addressRequest.setPinCode(560029);
        addressRequest.setStreetAddressAdditional("1st stage");


        seller = new Seller();
        store = new Store();
        seller.setStore(store);
        address = new Address();
        address.setAddressType(AddressType.HOME);
        address.setStreetAddress("BTM Layout");
        address.setState("Karnataka");
        address.setCountry("INDIA");
        address.setCity("Bangalore");
        address.setPinCode(560029);
        address.setStreetAddressAdditional("1st stage");
        store.setAddress(address);
    }

    @Test
    void testAddAddress_Success() {
        when(sellerRepo.findById(anyInt())).thenReturn(Optional.of(seller));
        when(addressMappers.mapToAddress(any(AddressRequest.class), any(Address.class))).thenReturn(address);
        when(addressRepo.save(any(Address.class))).thenReturn(address);
        when(storeRepo.save(any(Store.class))).thenReturn(store);

        ResponseStructure<Address> response = addressServiceImpl.addAddress(addressRequest, 1).getBody();

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
        when(storeRepo.findById(1)).thenReturn(Optional.of(store));

        ResponseStructure<Address> response = addressServiceImpl.findAddressByStoreId(1).getBody();

        assertNotNull(response);
        assertEquals(HttpStatus.FOUND.value(), response.getStatusCode());
        assertEquals("Address fetched using storeId", response.getMessage());
        assertEquals(HttpStatus.FOUND.value(), response.getStatusCode());
        assertEquals(address, response.getData());

        verify(storeRepo, times(1)).findById(1);
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