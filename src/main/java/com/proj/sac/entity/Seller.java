package com.proj.sac.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "sellers")
public class Seller extends User
{
	@OneToOne
	private Store store;
}
