package com.proj.sac.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proj.sac.enums.ContactPriority;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "contacts")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contact 
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int contactId;
	private String contactName;
	private long contactNumber;
	@Enumerated(EnumType.STRING)
	private ContactPriority contactPriority;
	
	@JsonIgnore
	@ManyToOne
	private Address address;
}