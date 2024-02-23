package com.proj.sac.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "stores")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Store 
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int storeId;
	private String storeName;
	
	private String logoLink;
	@Nullable
	private String about;
	
	@OneToOne
	private Address address;
}