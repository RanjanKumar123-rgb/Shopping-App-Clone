package com.proj.sac.service;

import com.proj.sac.entity.Contact;
import com.proj.sac.requestdto.ContactRequest;
import com.proj.sac.util.ResponseStructure;
import com.proj.sac.util.SimpleResponseStructure;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ContactService 
{

	ResponseEntity<SimpleResponseStructure> createContact(ContactRequest contactRequest, int addressId);

	ResponseEntity<SimpleResponseStructure> updateContact(ContactRequest contactRequest, int contactId);

	ResponseEntity<ResponseStructure<Contact>> findContactByContactId(int contactId);

	ResponseEntity<ResponseStructure<List<Contact>>> findContactByAddressId(int addressId);
	
}
