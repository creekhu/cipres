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

package org.cipres.utils;

/**
 * MailService.java
 * 
 * @author <a href="mailto:lcchan@sdsc.edu">Lucie Chan </a>
 *
 */
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

public class MailService {

		protected JavaMailSender mailSender;
		private FreeMarkerConfigurer mailTemplateEngine;
		private SimpleMailMessage mailMessage;

		public void setMailTemplateEngine(FreeMarkerConfigurer mailTemplateEngine) {
			this.mailTemplateEngine = mailTemplateEngine;
		}
		public void setMailSender(JavaMailSender mailSender) {
			this.mailSender = mailSender;
		}
		public void setMailMessage(SimpleMailMessage mailMessage)
		{
			this.mailMessage = mailMessage;
		}
		
		/**
		 * send MIME message (both text and htm) using freemarker templates
		 * 
		 * @param to: recipient of message
		 * @param templatePrefix: the name of the templates used
		 * @param model: contains values to be replaced in templates at runtime
		 * @throws Exception: exception throws by this call 
		 */
		public void sendMimeMessage(String to, String from, String subject, String templatePrefix, Map model) throws Exception {
		
			MimeMessage mimeMsg = null;
			
			String textcontent = generateEmailContent(templatePrefix+"-text.ftl", model);
			String htmlcontent = generateEmailContent(templatePrefix+"-html.ftl", model);
		
			mimeMsg = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMsg, true, "utf-8");
			helper.setTo(to);

			if (from != null)
				helper.setFrom(from);
			else
				helper.setFrom(mailMessage.getFrom());
				
			if (subject != null)
				helper.setSubject(subject);
			else
				helper.setSubject(mailMessage.getSubject());

			helper.setText(textcontent, htmlcontent);
			mailSender.send(mimeMsg);
		}
		
		
		/**
		 * 
		 * integrate template with data from client program for text replacement
		 */
		public String generateEmailContent(String template, Map map) throws Exception {
			
			Template t = mailTemplateEngine.getConfiguration().getTemplate(template);
			return FreeMarkerTemplateUtils.processTemplateIntoString(t, map);
		} 

		
		/**
		 * 
		 * 
		 * @param subject: subject of the email
		 * @param from: sender of the email
		 * @param to: user to send email to
		 * @param message: message to send
		 */
		public void sendSimpleMessage(String to,String from, String subject,String message) {
			SimpleMailMessage msg = new SimpleMailMessage();
			// required 
			msg.setTo(to); 
			
			// either passed in the function call or set in the configuration
			if (from != null)
				msg.setFrom(from);
			else
				msg.setFrom(mailMessage.getFrom());

			if (subject != null)
				msg.setSubject(subject);
			else
				msg.setSubject(mailMessage.getSubject());

			msg.setText(message);
			mailSender.send(msg);
		}
	}


