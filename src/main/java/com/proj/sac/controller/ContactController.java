package com.proj.sac.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proj.sac.entity.Contact;
import com.proj.sac.requestdto.AddressRequest;
import com.proj.sac.requestdto.ContactRequest;
import com.proj.sac.service.ContactService;
import com.proj.sac.util.ResponseStructure;
import com.proj.sac.util.SimpleResponseStructure;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
@CrossOrigin(allowCredentials = "true", origins = "http://localhost:5173/")
public class ContactController 
{
	ContactService service;
	
	@PreAuthorize(value = "hasAuthority('SELLER') or hasAuthority('CUSTOMER')")
	@PostMapping(path = "/contacts/{addressId}")
	public ResponseEntity<SimpleResponseStructure> createContact(@RequestBody ContactRequest contactRequest, @PathVariable int addressId)
	{
		return service.createContact(contactRequest, addressId);
	}
	
	@PreAuthorize(value = "hasAuthority('SELLER')")
	@PutMapping(path = "/contacts/{contactId}")
	public ResponseEntity<SimpleResponseStructure> updateContact(@RequestBody ContactRequest contactRequest, @PathVariable int contactId)
	{
		return service.updateContact(contactRequest, contactId);
	}
	
	@PreAuthorize(value = "hasAuthority('SELLER')")
	@GetMapping(path = "/contacts/{contactId}")
	public ResponseEntity<ResponseStructure<Contact>> findContactByContactId(@PathVariable int contactId)
	{
		return service.findContactByContactId(contactId);
	}
	
	@PreAuthorize(value = "hasAuthority('SELLER')")
	@GetMapping(path = "/addresses/{addressId}/contacts")
	public ResponseEntity<ResponseStructure<List<Contact>>> findContractByAddressId(@PathVariable int addressId)
	{
		return service.findContactByAddressId(addressId);
	}
}