package com.proj.sac.serviceimpl;

import com.proj.sac.entity.Address;
import com.proj.sac.entity.Contact;
import com.proj.sac.exception.AddressNotFoundException;
import com.proj.sac.exception.ContactExceedException;
import com.proj.sac.exception.ContactNotFoundException;
import com.proj.sac.repo.AddressRepo;
import com.proj.sac.repo.ContactRepo;
import com.proj.sac.requestdto.ContactRequest;
import com.proj.sac.service.ContactService;
import com.proj.sac.util.ResponseStructure;
import com.proj.sac.util.SimpleResponseStructure;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContactServiceImpl implements ContactService {
    private final AddressRepo addressRepo;
    private final ContactRepo contactRepo;
    private final SimpleResponseStructure simpleStructure;
    private final ResponseStructure<Contact> contactStructure;
    private final ResponseStructure<List<Contact>> contactListStructure;


    public ContactServiceImpl(AddressRepo addressRepo, ContactRepo contactRepo, ResponseStructure<Contact> contactStructure, SimpleResponseStructure simpleStructure, ResponseStructure<List<Contact>> contactListStructure) {
        super();
        this.addressRepo = addressRepo;
        this.contactRepo = contactRepo;
        this.simpleStructure = simpleStructure;
        this.contactStructure = contactStructure;
        this.contactListStructure = contactListStructure;
    }

    @Override
    public ResponseEntity<SimpleResponseStructure> createContact(ContactRequest contactRequest, int addressId) {
        Optional<Address> addressOptional = addressRepo.findById(addressId);
        addressOptional.ifPresentOrElse(address -> {
            Contact contact = mapToContact(contactRequest, address);

            if (address.getContactList().size() >= 2) throw new ContactExceedException("Already 2 contacts added !!!");
            else {
                contactRepo.save(contact);

                address.getContactList().add(contact);
                addressRepo.save(address);

                simpleStructure.setMessage("Contact Added");
                simpleStructure.setStatusCode(HttpStatus.OK.value());
            }
        }, () -> {
            throw new AddressNotFoundException("Address not found");
        });
        return new ResponseEntity<>(simpleStructure, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<SimpleResponseStructure> updateContact(ContactRequest contactRequest, int contactId) {
        Optional<Contact> contactOptional = contactRepo.findById(contactId);
        contactOptional.ifPresentOrElse(contact -> {
            contact.setContactName(contactRequest.getContactName());
            contact.setContactNumber(contactRequest.getContactNumber());
            contact.setContactPriority(contactRequest.getContactPriority());

            contactRepo.save(contact);

            simpleStructure.setMessage("Contact Updated");
            simpleStructure.setStatusCode(HttpStatus.OK.value());
        }, () -> {
            throw new ContactNotFoundException("Contact not found !!!");
        });
        return new ResponseEntity<>(simpleStructure, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseStructure<Contact>> findContactByContactId(int contactId) {
        Optional<Contact> contactOptional = contactRepo.findById(contactId);
        contactOptional.ifPresentOrElse(
                contact -> {
                    contactStructure.setData(contact);
                    contactStructure.setMessage("Fetched using Contact ID");
                    contactStructure.setStatusCode(HttpStatus.OK.value());
                }, () -> {
                    throw new ContactNotFoundException("Contact doesn't exist !!");
                }
        );
        return new ResponseEntity<>(contactStructure, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseStructure<List<Contact>>> findContactByAddressId(int addressId) {
        addressRepo.findById(addressId).ifPresentOrElse(
                address -> {
                    List<Contact> contactList = address.getContactList();

                    contactListStructure.setData(contactList);
                    contactListStructure.setMessage("Fetched using Contact ID");
                    contactListStructure.setStatusCode(HttpStatus.FOUND.value());
                }, () -> {
                    throw new AddressNotFoundException("Failed to find address !!!");
                }
        );
        return new ResponseEntity<>(contactListStructure, HttpStatus.OK);
    }

    private Contact mapToContact(ContactRequest contactRequest, Address address) {
        return Contact.builder().contactName(contactRequest.getContactName()).contactNumber(contactRequest.getContactNumber()).contactPriority(contactRequest.getContactPriority()).address(address).build();
    }
}

//	==============================================================================================================================





