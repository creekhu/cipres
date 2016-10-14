package org.cipres.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.support.ClassPathXmlApplicationContext;


/*
 * Copyright 2009 CIPRES project. http://www.phylo.org/ 
 * All Rights Reserved. 
 *
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for educational, research and non-profit purposes, without
 * fee, and without a written agreement is hereby granted, provided that the
 * above copyright notice, this paragraph and the following two paragraphs
 * appear in all copies. 
 *
 * Permission to incorporate this software into commercial products may be
 * obtained by contacting us:
 *              http://www.phylo.org/contactUs.html
 * 
 * The software program and documentation are supplied "as is". In no event
 * shall the CIPRES project be liable to any party for direct, indirect,
 * special, incidental, or consequential damages, including lost profits, 
 * arising out of the use of this software and its documentation, even if
 * the CIPRES project has been advised of the possibility of such damage.  
 * The CIPRES project specifically disclaims any warranties, including, but
 * not limited to, the implied warranties of merchantability and fitness for
 * a particular purpose. The CIPRES project has no obligations to provide 
 * maintenance, support, updates, enhancements, or modifications. 
 */

/**
 * Test.java
 * 
 * @author <a href="mailto:lcchan@sdsc.edu">Lucie Chan </a>
 *
 */
public class Test {

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
	
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("spring-mail.xml");
		MailService mailservice = (MailService) appContext.getBean("mailService");
		mailservice.sendSimpleMessage("lcchan@sdsc.edu", "info@ngbw.org", "[Your Subject Here]", "just a test message");
		
		
		Map<String, String> model = new HashMap<String, String>();
		model.put("name", "lcchan");
		try
		{
			mailservice.sendMimeMessage("lcchan@sdsc.edu", "info@ngbw.org", "[Subject Here]", "job", model);
		} catch (Exception ex) {
			System.out.println("Error while send MIME message: " + ex.getMessage());
		}
	}
}
