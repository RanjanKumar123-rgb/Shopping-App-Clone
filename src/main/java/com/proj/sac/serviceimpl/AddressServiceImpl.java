package com.proj.sac.serviceimpl;

import com.proj.sac.entity.Address;
import com.proj.sac.entity.Store;
import com.proj.sac.exception.AddressNotFoundException;
import com.proj.sac.exception.SellerNotFoundException;
import com.proj.sac.exception.StoreNotFoundException;
import com.proj.sac.mappers.AddressMappers;
import com.proj.sac.repo.AddressRepo;
import com.proj.sac.repo.SellerRepo;
import com.proj.sac.repo.StoreRepo;
import com.proj.sac.requestdto.AddressRequest;
import com.proj.sac.service.AddressService;
import com.proj.sac.util.ResponseStructure;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AddressServiceImpl implements AddressService {
    private final AddressRepo addressRepo;
    private final SellerRepo sellerRepo;
    private final StoreRepo storeRepo;
    private final AddressMappers addressMappers;

    public AddressServiceImpl(AddressRepo addressRepo, SellerRepo sellerRepo, StoreRepo storeRepo, AddressMappers addressMappers) {
        super();
        this.addressRepo = addressRepo;
        this.sellerRepo = sellerRepo;
        this.storeRepo = storeRepo;
        this.addressMappers = addressMappers;
    }

    @Override
    public ResponseEntity<ResponseStructure<Address>> addAddress(AddressRequest addressRequest, int sellerId) {
        return sellerRepo.findById(sellerId).map(seller -> {
            Store store = seller.getStore();
            Address address = addressMappers.mapToAddress(addressRequest, new Address());

            address = addressRepo.save(address);
            store.setAddress(address);
            storeRepo.save(store);

            ResponseStructure<Address> structure = new ResponseStructure<>();
            structure.setMessage("Address Successfully Added !!");
            structure.setStatusCode(HttpStatus.CREATED.value());
            structure.setData(address);
            return new ResponseEntity<>(structure, HttpStatus.OK);
        }).orElseThrow(() -> new SellerNotFoundException("Failed to Add Address"));
    }

    @Override
    public ResponseEntity<ResponseStructure<Address>> updateAddress(AddressRequest addressRequest, int addressId) {
        return addressRepo.findById(addressId).map(exAddress -> {
            addressMappers.mapToAddress(addressRequest, exAddress);
            exAddress = addressRepo.save(exAddress);

            ResponseStructure<Address> structure = new ResponseStructure<>();
            structure.setMessage("Address Successfully Updated !!");
            structure.setStatusCode(HttpStatus.OK.value());
            structure.setData(exAddress);

            return new ResponseEntity<>(structure, HttpStatus.OK);
        }).orElseThrow(() -> new AddressNotFoundException("Failed to locate Address"));
    }

    @Override
    public ResponseEntity<ResponseStructure<Address>> findAddressByAddressId(int addressId) {
        return addressRepo.findById(addressId).map(address -> {
            ResponseStructure<Address> structure = new ResponseStructure<>();
            structure.setData(address);
            structure.setMessage("Address fetched by addressId");
            structure.setStatusCode(HttpStatus.FOUND.value());
            return new ResponseEntity<>(structure, HttpStatus.OK);
        }).orElseThrow(() -> new AddressNotFoundException("Address not added !!"));
    }

    @Override
    public ResponseEntity<ResponseStructure<Address>> findAddressByStoreId(int storeId) {
        return storeRepo.findById(storeId).map(store -> {
            Address address = store.getAddress();
            ResponseStructure<Address> structure = new ResponseStructure<>();
            structure.setData(address);
            structure.setMessage("Address fetched using storeId");
            structure.setStatusCode(HttpStatus.FOUND.value());
            return new ResponseEntity<>(structure, HttpStatus.OK);
        }).orElseThrow(() -> new StoreNotFoundException("Failed to locate Store"));
    }
}
