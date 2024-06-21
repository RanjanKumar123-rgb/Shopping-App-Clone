package com.proj.sac.serviceimpl;

import com.proj.sac.entity.Address;
import com.proj.sac.entity.Contact;
import com.proj.sac.enums.ContactPriority;
import com.proj.sac.exception.AddressNotFoundException;
import com.proj.sac.exception.ContactExceedException;
import com.proj.sac.exception.ContactNotFoundException;
import com.proj.sac.mappers.ContactMappers;
import com.proj.sac.repo.AddressRepo;
import com.proj.sac.repo.ContactRepo;
import com.proj.sac.requestdto.ContactRequest;
import com.proj.sac.util.ResponseStructure;
import com.proj.sac.util.SimpleResponseStructure;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

//Test Case done using JUnit
@SpringBootTest
@ActiveProfiles("test")
class ContactServiceImplTest {

    @MockBean
    private AddressRepo addressRepo;

    @MockBean
    private ContactRepo contactRepo;

    @MockBean
    private SimpleResponseStructure simpleStructure;

    @MockBean
    private ResponseStructure<Contact> contactStructure;

    @MockBean
    private ResponseStructure<List<Contact>> contactListStructure;

    @MockBean
    private ContactMappers contactMappers;

    @Autowired
    private ContactServiceImpl contactService;

    @Test
    void testCreateContact_Success() {
        ContactRequest contactRequest = new ContactRequest();
        contactRequest.setContactName("John Doe");
        contactRequest.setContactNumber(Long.parseLong("1234567890"));
        contactRequest.setContactPriority(ContactPriority.LOW);

        Address address = new Address();
        address.setAddressId(1);
        address.setContactList(new ArrayList<>());

        Contact contact = new Contact();
        contact.setContactId(1);
        contact.setContactName("John Doe");
        contact.setContactNumber(Long.parseLong("1234567890"));
        contact.setContactPriority(ContactPriority.LOW);
        contact.setAddress(address);

        when(addressRepo.findById(1)).thenReturn(Optional.of(address));
        when(contactMappers.mapToContact(any(ContactRequest.class), any(Address.class))).thenReturn(contact);
        when(contactRepo.save(any(Contact.class))).thenReturn(contact);

        ResponseEntity<SimpleResponseStructure> response = contactService.createContact(contactRequest, 1);

        verify(contactRepo, times(1)).save(contact);
        verify(addressRepo, times(1)).save(address);
        verify(simpleStructure, times(1)).setMessage("Contact Added");
        verify(simpleStructure, times(1)).setStatusCode(HttpStatus.OK.value());

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testCreateContact_AddressNotFound() {
        ContactRequest contactRequest = new ContactRequest();
        when(addressRepo.findById(1)).thenReturn(Optional.empty());

        AddressNotFoundException exception = org.junit.jupiter.api.Assertions.assertThrows(AddressNotFoundException.class, () -> {
            contactService.createContact(contactRequest, 1);
        });

        assertEquals("Address not found", exception.getMessage());
    }

    @Test
    void testCreateContact_ContactExceedException() {
        ContactRequest contactRequest = new ContactRequest();
        Address address = new Address();
        List<Contact> contactList = new ArrayList<>();
        contactList.add(new Contact());
        contactList.add(new Contact());
        address.setContactList(contactList);

        when(addressRepo.findById(1)).thenReturn(Optional.of(address));

        ContactExceedException exception = org.junit.jupiter.api.Assertions.assertThrows(ContactExceedException.class, () -> {
            contactService.createContact(contactRequest, 1);
        });

        assertEquals("Already 2 contacts added !!!", exception.getMessage());
    }

    @Test
    void testUpdateContact_Success() {
        ContactRequest contactRequest = new ContactRequest();
        contactRequest.setContactName("John Doe Updated");
        contactRequest.setContactNumber(Long.parseLong("0987654321"));
        contactRequest.setContactPriority(ContactPriority.LOW);

        Contact contact = new Contact();
        contact.setContactId(1);
        contact.setContactName("John Doe");
        contact.setContactNumber(Long.parseLong("1234567890"));
        contact.setContactPriority(ContactPriority.LOW);

        when(contactRepo.findById(1)).thenReturn(Optional.of(contact));

        ResponseEntity<SimpleResponseStructure> response = contactService.updateContact(contactRequest, 1);

        verify(contactRepo, times(1)).save(contact);
        verify(simpleStructure, times(1)).setMessage("Contact Updated");
        verify(simpleStructure, times(1)).setStatusCode(HttpStatus.OK.value());

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUpdateContact_ContactNotFound() {
        ContactRequest contactRequest = new ContactRequest();
        when(contactRepo.findById(1)).thenReturn(Optional.empty());

        ContactNotFoundException exception = org.junit.jupiter.api.Assertions.assertThrows(ContactNotFoundException.class, () -> {
            contactService.updateContact(contactRequest, 1);
        });

        assertEquals("Contact not found !!!", exception.getMessage());
    }

    @Test
    void testFindContactByContactId_Success() {
        Contact contact = new Contact();
        contact.setContactId(1);
        contact.setContactName("John Doe");

        when(contactRepo.findById(1)).thenReturn(Optional.of(contact));

        ResponseEntity<ResponseStructure<Contact>> response = contactService.findContactByContactId(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testFindContactByContactId_ContactNotFound() {
        when(contactRepo.findById(1)).thenReturn(Optional.empty());

        ContactNotFoundException exception = org.junit.jupiter.api.Assertions.assertThrows(ContactNotFoundException.class, () -> {
            contactService.findContactByContactId(1);
        });

        assertEquals("Contact doesn't exist !!", exception.getMessage());
    }

    @Test
    void testFindContactByAddressId_Success() {
        Address address = new Address();
        List<Contact> contactList = new ArrayList<>();
        contactList.add(new Contact());
        contactList.add(new Contact());
        address.setContactList(contactList);

        when(addressRepo.findById(1)).thenReturn(Optional.of(address));

        ResponseEntity<ResponseStructure<List<Contact>>> response = contactService.findContactByAddressId(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testFindContactByAddressId_AddressNotFound() {
        when(addressRepo.findById(1)).thenReturn(Optional.empty());

        AddressNotFoundException exception = org.junit.jupiter.api.Assertions.assertThrows(AddressNotFoundException.class, () -> {
            contactService.findContactByAddressId(1);
        });

        assertEquals("Failed to find address !!!", exception.getMessage());
    }
}
