package com.proj.sac.mappers;

import com.proj.sac.entity.Address;
import com.proj.sac.entity.Seller;
import com.proj.sac.requestdto.AddressRequest;

public class AddressMappers
{
    public Address mapToAddress(AddressRequest addressRequest, Address address) {
        address.setStreetAddress(addressRequest.getStreetAddress());
        address.setStreetAddressAdditional(addressRequest.getStreetAddressAdditional());
        address.setCity(addressRequest.getCity());
        address.setState(addressRequest.getState());
        address.setCountry(addressRequest.getCountry());
        address.setPinCode(addressRequest.getPinCode());
        address.setAddressType(addressRequest.getAddressType());
        return address;
    }
}
