package com.proj.sac.serviceimpl;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.proj.sac.entity.Address;
import com.proj.sac.entity.Seller;
import com.proj.sac.entity.Store;
import com.proj.sac.exception.AddressNotFoundException;
import com.proj.sac.repo.AddressRepo;
import com.proj.sac.repo.SellerRepo;
import com.proj.sac.repo.StoreRepo;
import com.proj.sac.requestdto.AddressRequest;
import com.proj.sac.service.AddressService;
import com.proj.sac.util.ResponseStructure;
import com.proj.sac.util.SimpleResponseStructure;

@Service
public class AddressServiceImpl implements AddressService
{
	private AddressRepo addressRepo;
	private SimpleResponseStructure simpleStructure;
	private SellerRepo sellerRepo;
	private ResponseStructure<Address> addressStructure;
	private StoreRepo storeRepo;
	
	public AddressServiceImpl(AddressRepo addressRepo, 
			SimpleResponseStructure simpleStructure,
			ResponseStructure<Address> addressStructure,
			SellerRepo sellerRepo,
			StoreRepo storeRepo)
	{
		super();
		this.addressRepo = addressRepo;
		this.simpleStructure = simpleStructure;
		this.addressStructure = addressStructure;
		this.sellerRepo = sellerRepo;
		this.storeRepo = storeRepo;
	}

	@Override
	public ResponseEntity<SimpleResponseStructure> addAddress(AddressRequest addressRequest, int sellerId) 
	{
		Seller seller = sellerRepo.findById(sellerId).get();
		Store store = seller.getStore();
		
		Address address = mapToAddress(addressRequest, seller);
		store.setAddress(address);
		storeRepo.save(store);
		
		addressRepo.save(address);
				
		simpleStructure.setMessage("Address Successfully Added !!");
		simpleStructure.setStatusCode(HttpStatus.CREATED.value());
		        
		return new ResponseEntity<>(simpleStructure,HttpStatus.OK);
	}

	@Override
	public ResponseEntity<SimpleResponseStructure> updateAddress(AddressRequest addressRequest, int addressId) 
	{
		Address address = addressRepo.findById(addressId).get();
		address.setStreetAddress(addressRequest.getStreetAddress());
		address.setStreetAddressAdditional(addressRequest.getStreetAddressAdditional());
		address.setCity(addressRequest.getCity());
		address.setState(addressRequest.getState());
		address.setCountry(addressRequest.getCountry());
		address.setPincode(addressRequest.getPincode());
		address.setAddressType(addressRequest.getAddressType());
		
		addressRepo.save(address);
		
		simpleStructure.setMessage("Address Successfully Updated !!");
		simpleStructure.setStatusCode(HttpStatus.OK.value());
		        
		return new ResponseEntity<>(simpleStructure,HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<ResponseStructure<Address>> findAddresssByAddressId(int addressId) 
	{
		Optional<Address> addresses = addressRepo.findById(addressId);
		if(addresses == null)
			throw new AddressNotFoundException("Address nor added !!");
		else
		{
			Address address = addresses.get();
			
			addressStructure.setData(address);
			addressStructure.setMessage("Address fetched by addressId");
			addressStructure.setStatusCode(HttpStatus.FOUND.value());
			
			return new ResponseEntity<ResponseStructure<Address>>(addressStructure, HttpStatus.OK);
		}
		
	}
	
	@Override
	public ResponseEntity<ResponseStructure<Address>> findAddresssByStoreId(int storeId) 
	{
		Store store = storeRepo.findById(storeId).get();
		Address address = store.getAddress();
		
		addressStructure.setData(address);
		addressStructure.setMessage("Address fetched using storeId");
		addressStructure.setStatusCode(HttpStatus.FOUND.value());
		
		return new ResponseEntity<ResponseStructure<Address>>(addressStructure, HttpStatus.OK);
	}
	
//	===============================================================================================================================================	
	
	private Address mapToAddress(AddressRequest addressRequest, Seller seller) 
	{
		return Address.builder()
				.streetAddress(addressRequest.getStreetAddress())
				.streetAddressAdditional(addressRequest.getStreetAddressAdditional())
				.city(addressRequest.getCity())
				.state(addressRequest.getState())
				.country(addressRequest.getCountry())
				.pincode(addressRequest.getPincode())
				.addressType(addressRequest.getAddressType())
				.seller(seller)
				.build();
	}
}
