package com.proj.sac.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.proj.sac.entity.Contact;
import com.proj.sac.requestdto.AddressRequest;
import com.proj.sac.requestdto.ContactRequest;
import com.proj.sac.util.ResponseStructure;
import com.proj.sac.util.SimpleResponseStructure;

public interface ContactService 
{

	ResponseEntity<SimpleResponseStructure> createContact(ContactRequest contactRequest, int addressId);

	ResponseEntity<SimpleResponseStructure> updateContact(ContactRequest contactRequest, int contactId);

	ResponseEntity<ResponseStructure<Contact>> findContactByContactId(int contactId);

	ResponseEntity<ResponseStructure<List<Contact>>> findContactByAddressId(int addressId);
	
}
