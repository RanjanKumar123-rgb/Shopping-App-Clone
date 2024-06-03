package com.proj.sac.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proj.sac.entity.Contact;

public interface ContactRepo extends JpaRepository<Contact, Integer>
{

}
