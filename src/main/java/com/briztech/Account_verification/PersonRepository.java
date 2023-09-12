package com.briztech.Account_verification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person,Integer>
{

	public Person findByEmail(String email);
	
	public Person findByVerificationCode(String code);
}
