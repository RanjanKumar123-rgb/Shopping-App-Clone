package com.proj.sac.requestdto;

import com.proj.sac.enums.ContactPriority;

import lombok.Data;

@Data
public class ContactRequest 
{
	private String contactName;
	private long contactNumber;
	private ContactPriority contactPriority;
}
