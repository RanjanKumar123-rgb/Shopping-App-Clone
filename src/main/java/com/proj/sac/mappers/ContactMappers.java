package com.proj.sac.mappers;

import com.proj.sac.entity.Address;
import com.proj.sac.entity.Contact;
import com.proj.sac.requestdto.ContactRequest;
import org.springframework.stereotype.Component;

@Component
public class ContactMappers {
    public Contact mapToContact(ContactRequest contactRequest, Address address) {
        return Contact.builder().contactName(contactRequest.getContactName()).contactNumber(contactRequest.getContactNumber()).contactPriority(contactRequest.getContactPriority()).address(address).build();
    }
}
