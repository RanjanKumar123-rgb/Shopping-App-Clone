package com.proj.sac.serviceimpl;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.proj.sac.entity.Address;
import com.proj.sac.entity.Contact;
import com.proj.sac.exception.ContactExceedException;
import com.proj.sac.exception.ContactNotFoundException;
import com.proj.sac.repo.AddressRepo;
import com.proj.sac.repo.ContactRepo;
import com.proj.sac.requestdto.AddressRequest;
import com.proj.sac.requestdto.ContactRequest;
import com.proj.sac.service.ContactService;
import com.proj.sac.util.ResponseStructure;
import com.proj.sac.util.SimpleResponseStructure;

@Service
public class ContactServiceImpl implements ContactService
{
	private AddressRepo addressRepo;
	private ContactRepo contactRepo;
	private SimpleResponseStructure simpleStructure;
	private ResponseStructure<Contact> contactStructure;
	ResponseStructure<List<Contact>> contactListStructure;
	
	
	public ContactServiceImpl(AddressRepo addressRepo, 
			ContactRepo contactRepo, 
			ResponseStructure<Contact> contactStructure,
			SimpleResponseStructure simpleStructure,
			ResponseStructure<List<Contact>> contactListStructure) 
	{
		super();
		this.addressRepo = addressRepo;
		this.contactRepo = contactRepo;
		this.simpleStructure = simpleStructure;
		this.contactStructure = contactStructure;
		this.contactListStructure = contactListStructure;
	}

	@Override
	public ResponseEntity<SimpleResponseStructure> createContact(ContactRequest contactRequest, int addressId) 
	{
		Address address = addressRepo.findById(addressId).get();
		Contact contact = mapToContact(contactRequest, address);
		
		if(address.getContactList().size()>=2)
			throw new ContactExceedException("Already 2 contacts added !!!");
		else
		{
			contactRepo.save(contact);
			
			address.getContactList().add(contact);
			addressRepo.save(address);
			
			simpleStructure.setMessage("Contact Added");
			simpleStructure.setStatusCode(HttpStatus.OK.value());
			        
			return new ResponseEntity<SimpleResponseStructure>(simpleStructure, HttpStatus.OK);
		}
	}

	@Override
	public ResponseEntity<SimpleResponseStructure> updateContact(ContactRequest contactRequest, int contactId) 
	{
		Contact contact = contactRepo.findById(contactId).get();
		
		contact.setContactName(contactRequest.getContactName());
		contact.setContactNumber(contactRequest.getContactNumber());
		contact.setContactPriority(contactRequest.getContactPriority());
		
		contactRepo.save(contact);
		
		simpleStructure.setMessage("Contact Updated");
		simpleStructure.setStatusCode(HttpStatus.OK.value());
		        
		return new ResponseEntity<SimpleResponseStructure>(simpleStructure, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<ResponseStructure<Contact>> findContactByContactId(int contactId) 
	{
		Optional<Contact> contacts = contactRepo.findById(contactId);
		if(contacts == null)
			throw new ContactNotFoundException("Contact doesnt exist !!");
		else
		{
			Contact contact = contacts.get();
			
			contactStructure.setData(contact);
			contactStructure.setMessage("Fetchedd using Contact ID");
			contactStructure.setStatusCode(HttpStatus.OK.value());
			
			return new ResponseEntity<ResponseStructure<Contact>>(contactStructure, HttpStatus.OK);
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<List<Contact>>> findContactByAddressId(int addressId) 
	{
		Address address = addressRepo.findById(addressId).get();
		List<Contact> contactList = address.getContactList();
		
		contactListStructure.setData(contactList);
		contactListStructure.setMessage("Fetchedd using Contact ID");
		contactListStructure.setStatusCode(HttpStatus.FOUND.value());
		
		return new ResponseEntity<ResponseStructure<List<Contact>>>(contactListStructure, HttpStatus.OK);
		
	}
	
//	==============================================================================================================================	
	

	private Contact mapToContact(ContactRequest contactRequest, Address address) {
		return Contact.builder()
				.contactName(contactRequest.getContactName())
				.contactNumber(contactRequest.getContactNumber())
				.contactPriority(contactRequest.getContactPriority())
				.address(address)
				.build();
	}
}
