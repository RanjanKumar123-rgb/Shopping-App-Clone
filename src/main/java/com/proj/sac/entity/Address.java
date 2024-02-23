package com.proj.sac.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proj.sac.enums.AddressType;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "addresses")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address 
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int addressId;
	private String streetAddress;
	@Nullable
	private String streetAddressAdditional;
	private String city;
	private String state;
	private String country;
	private int pincode;
	@Enumerated(EnumType.STRING)
	private AddressType addressType;
	
	@OneToOne
	@JsonIgnore
	private Seller seller;
	
	@OneToMany(mappedBy = "address")
	private List<Contact> contactList;
}
