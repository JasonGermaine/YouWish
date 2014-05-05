package com.example.youwish.util;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.example.youwish.model.User;

public class RecoveryManager
{

	

	public static User sendMail(User user) throws AddressException, MessagingException, UnsupportedEncodingException
	{

		Properties emailProperties;
		Session mailSession;
		MimeMessage emailMessage;
		
		String emailPort = "587";// gmail's smtp port

		// Assigning properties to the email
		emailProperties = System.getProperties();
		emailProperties.put("mail.smtp.port", emailPort); // using gmail
		emailProperties.put("mail.smtp.auth", "true"); 
		emailProperties.put("mail.smtp.starttls.enable", "true"); // using tls

		String toEmail = user.getEmail() ; // Get the user's email
		String emailSubject = "YouWish - Password Reset"; // Assigning subject to email
		
		// Generate User email with newly generated password
		String emailBody = "Hi " + user.getFName() + ", \n\n Your new generated password is: " + user.getPassword() 
							+ ".\n\n Please do not reply to this email.\n\n\nYouWish Support Team";

		mailSession = Session.getDefaultInstance(emailProperties, null);
		emailMessage = new MimeMessage(mailSession);

		emailMessage.setFrom(new InternetAddress("recovery.youwish@gmail.com", "YouWish Recovery"));



			emailMessage.addRecipient(Message.RecipientType.TO,
					new InternetAddress(toEmail));

		emailMessage.setSubject(emailSubject);
		//emailMessage.setContent(emailBody, "text/html");// for a html email
		emailMessage.setText(emailBody);// for a text email

		// Specify email login details
		String emailHost = "smtp.gmail.com";
		String fromUser = "recovery.youwish";// just the id alone without @gmail.com
		String fromUserEmailPassword = "X00090307";

		// Using smpt protocol and send the email
		Transport transport = mailSession.getTransport("smtp");
		transport.connect(emailHost, fromUser, fromUserEmailPassword);
		transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
		transport.close();
		
		// Assigning the hash to the newly generated password
		user.setPassword(user.getPassword());
		
		return user;

	}
}