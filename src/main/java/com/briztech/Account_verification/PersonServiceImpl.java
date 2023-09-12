package com.briztech.Account_verification;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;

@Service
public class PersonServiceImpl implements PersonService
{
	@Autowired
	PersonRepository prepo;                                                          

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Override
	public Person saveUsers(Person person,String url) 
	{
		String password=passwordEncoder.encode(person.getPassword());
		person.setPassword(password);
		person.setRole("ROLE_ADMIN");
	
		person.setEnable(false);
		person.setVerificationCode(UUID.randomUUID().toString());
		
		
		Person p= prepo.save(person);
		
		if(p!=null)
		{
			 sendEmail(p,url);
		}

		return p;
	}

	@Override
	public void removeSessionMessage()
	{
	  HttpSession session =	((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes())).getRequest().getSession();
		
	  session.removeAttribute("msg");
	}

	@Override
	public void sendEmail(Person person, String url)
	{
		String from="vikashkr5405@gmail.com";
		String to=person.getEmail();
		String subject="Account verification";
		String content="Dear [[name]],<br>" + "please click the link below to verify your registration:<br>"
		+ "<h3><a href=\"[[url]]\" target=\"_self\">VERIFY</a></h3>" + "Thank you,<br>" + "vikash";
		
		try {
			
			MimeMessage message=mailSender.createMimeMessage();
			MimeMessageHelper helper=new MimeMessageHelper(message);
			
			helper.setFrom(from);
			helper.setTo(to);
			helper.setSubject(subject);
			
			content=content.replace("[[name]]", person.getName());
			
			String siteUrl=url + "/verify?code=" +person.getVerificationCode();
			
			content=content.replace("[[url]]",siteUrl );
			
			helper.setText(content, true);
			
			mailSender.send(message);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean verifyAccount(String verificationCode) 
	{
	
		Person person=prepo.findByVerificationCode(verificationCode);
		
		if(person==null)
		{
			return false;
		}
		else 
		{
			person.setEnable(true);
			person.setVerificationCode(null);
			
			prepo.save(person);
			
			return true;
		}
	}
	
	
}

