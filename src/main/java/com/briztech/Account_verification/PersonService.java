package com.briztech.Account_verification;



public interface PersonService 
{

	Person saveUsers(Person person,String url);
	
	public void removeSessionMessage();

	public void sendEmail(Person person,String path);
	
	public boolean verifyAccount(String verificationCode);
	
}
