package com.proj.sac.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "sellers")
public class Seller extends User
{
	
}
