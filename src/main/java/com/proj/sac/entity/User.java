package com.proj.sac.entity;

import com.proj.sac.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "users")
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User 
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int userId;

	@Column(unique = true)
	private String username;
	private String email;
	private String password;
	private UserRole userRole;
	private boolean isEmailVerified;
	private String isDeleted;
}
