package com.proj.sac.serviceimpl;

import com.proj.sac.exception.AddressNotFoundException;
import com.proj.sac.exception.ContactExceedException;
import com.proj.sac.exception.ContactNotFoundException;
import com.proj.sac.mappers.ContactMappers;
import com.proj.sac.repo.AddressRepo;
import com.proj.sac.repo.ContactRepo;
import com.proj.sac.requestdto.ContactRequest;
import com.proj.sac.entity.Address;
import com.proj.sac.entity.Contact;
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
    private final ContactMappers contactMappers;


    public ContactServiceImpl(AddressRepo addressRepo, ContactRepo contactRepo, SimpleResponseStructure simpleStructure, ContactMappers contactMappers) {
        super();
        this.addressRepo = addressRepo;
        this.contactRepo = contactRepo;
        this.simpleStructure = simpleStructure;
        this.contactMappers = contactMappers;
    }

    @Override
    public ResponseEntity<SimpleResponseStructure> createContact(ContactRequest contactRequest, int addressId) {
        Optional<Address> addressOptional = addressRepo.findById(addressId);
        addressOptional.ifPresentOrElse(address -> {
            Contact contact = contactMappers.mapToContact(contactRequest, address);

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
        return contactRepo.findById(contactId).map(contact -> {
            ResponseStructure<Contact> structure = new ResponseStructure<>();

            structure.setData(contact);
            structure.setMessage("Fetched using Contact ID");
            structure.setStatusCode(HttpStatus.OK.value());

            return new ResponseEntity<>(structure, HttpStatus.OK);
        }).orElseThrow(() -> new ContactNotFoundException("Contact doesn't exist !!"));
    }

    @Override
    public ResponseEntity<ResponseStructure<List<Contact>>> findContactByAddressId(int addressId) {
        return addressRepo.findById(addressId).map(address -> {
            ResponseStructure<List<Contact>> structure = new ResponseStructure<>();
            List<Contact> contactList = address.getContactList();

            structure.setData(contactList);
            structure.setMessage("Fetched using Contact ID");
            structure.setStatusCode(HttpStatus.FOUND.value());

            return new ResponseEntity<>(structure, HttpStatus.OK);
        }).orElseThrow(() -> new AddressNotFoundException("Failed to find address !!!"));
    }
}