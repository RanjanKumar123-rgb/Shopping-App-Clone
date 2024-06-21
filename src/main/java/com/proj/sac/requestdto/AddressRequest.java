package com.proj.sac.requestdto;

import com.proj.sac.enums.AddressType;

import jakarta.annotation.Nullable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressRequest 
{
	private String streetAddress;
	@Nullable
	private String streetAddressAdditional;
	private String city;
	private String state;
	private String country;
	private int pinCode;
	@Enumerated(EnumType.STRING)
	private AddressType addressType;
}
