package com.proj.sac.serviceimpl;

import com.proj.sac.entity.Address;
import com.proj.sac.entity.Seller;
import com.proj.sac.entity.Store;
import com.proj.sac.exception.AddressNotFoundException;
import com.proj.sac.exception.SellerNotFoundException;
import com.proj.sac.exception.StoreNotFoundException;
import com.proj.sac.repo.AddressRepo;
import com.proj.sac.repo.SellerRepo;
import com.proj.sac.repo.StoreRepo;
import com.proj.sac.requestdto.AddressRequest;
import com.proj.sac.service.AddressService;
import com.proj.sac.util.ResponseStructure;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AddressServiceImpl implements AddressService {
    private final AddressRepo addressRepo;
    private final SellerRepo sellerRepo;
    private final ResponseStructure<Address> addressStructure;
    private final StoreRepo storeRepo;

    public AddressServiceImpl(AddressRepo addressRepo,
                              ResponseStructure<Address> addressStructure,
                              SellerRepo sellerRepo,
                              StoreRepo storeRepo) {
        super();
        this.addressRepo = addressRepo;
        this.addressStructure = addressStructure;
        this.sellerRepo = sellerRepo;
        this.storeRepo = storeRepo;
    }

    @Override
    public ResponseEntity<ResponseStructure<Address>> addAddress(AddressRequest addressRequest, int sellerId) {
        Optional<Seller> sellerOptional = sellerRepo.findById(sellerId);
        sellerOptional.ifPresentOrElse(
                seller -> {
                    Store store = seller.getStore();
                    Address address = mapToAddress(addressRequest, seller);

                    addressRepo.save(address);
                    store.setAddress(address);
                    storeRepo.save(store);

                    addressStructure.setMessage("Address Successfully Added !!");
                    addressStructure.setStatusCode(HttpStatus.CREATED.value());
                    addressStructure.setData(address);
                },
                () -> {
                    throw new SellerNotFoundException("Failed to locate Seller");
                }
        );

        return new ResponseEntity<>(addressStructure, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseStructure<Address>> updateAddress(AddressRequest addressRequest, int addressId) {
        Address address = addressRepo.findById(addressId).get();
        address.setStreetAddress(addressRequest.getStreetAddress());
        address.setStreetAddressAdditional(addressRequest.getStreetAddressAdditional());
        address.setCity(addressRequest.getCity());
        address.setState(addressRequest.getState());
        address.setCountry(addressRequest.getCountry());
        address.setPinCode(addressRequest.getPinCode());
        address.setAddressType(addressRequest.getAddressType());

        addressRepo.save(address);

        addressStructure.setMessage("Address Successfully Updated !!");
        addressStructure.setStatusCode(HttpStatus.OK.value());
        addressStructure.setData(address);

        return new ResponseEntity<>(addressStructure, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseStructure<Address>> findAddressByAddressId(int addressId) {
        Optional<Address> addresses = addressRepo.findById(addressId);
        addresses.ifPresentOrElse(
                address -> {
                    addressStructure.setData(address);
                    addressStructure.setMessage("Address fetched by addressId");
                    addressStructure.setStatusCode(HttpStatus.FOUND.value());
                },
                () -> {
                    throw new AddressNotFoundException("Address not added !!");
                }
        );
        return new ResponseEntity<>(addressStructure, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseStructure<Address>> findAddressByStoreId(int storeId) {
        Optional<Store> storeOptional = storeRepo.findById(storeId);
        storeOptional.ifPresentOrElse(
                store -> {
                    Address address = store.getAddress();
                    addressStructure.setData(address);
                    addressStructure.setMessage("Address fetched using storeId");
                    addressStructure.setStatusCode(HttpStatus.FOUND.value());
                },
                () -> {
                    throw new StoreNotFoundException("Failed to locate Store");
                }
        );


        return new ResponseEntity<>(addressStructure, HttpStatus.OK);
    }

//	===============================================================================================================================================	

    private Address mapToAddress(AddressRequest addressRequest, Seller seller) {
        return Address.builder()
                .streetAddress(addressRequest.getStreetAddress())
                .streetAddressAdditional(addressRequest.getStreetAddressAdditional())
                .city(addressRequest.getCity())
                .state(addressRequest.getState())
                .country(addressRequest.getCountry())
                .pinCode(addressRequest.getPinCode())
                .addressType(addressRequest.getAddressType())
                .seller(seller)
                .build();
    }
}
