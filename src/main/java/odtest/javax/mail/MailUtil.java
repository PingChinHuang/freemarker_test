package odtest.javax.mail;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateNotFoundException;

public class MailUtil {
	
	private static Properties QuerySMTPAccountConfig() {
		Properties props = new Properties();
		props.put("mail.smtp.host", "mail.audy.com");
		props.put("mail.smtp.sender", "audy@mail.audy.com");
		props.put("mail.smtp.username", "");
		props.put("mail.smtp.password", "");
		return props;
	}

	private static Properties QuerySMTPSecureConfig() {
		Properties props = new Properties();
		//TLS
		//props.put("mail.smtp.port", "587");
		//props.put("mail.smtp.starttls.enable", "true");
		//props.put("mail.smtp.auth", "true");
		
		//SSL
		//props.put("mail.smtp.port", "465");
		//props.put("mail.smtp.socketFactory.port", "465");
		//props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		//props.put("mail.smtp.auth", "true");
		
		//None
		props.put("mail.smtp.port", "25");	
		props.put("mail.smtp.auth", "false");
		return props;
	} 
	
	private static Session CreateMailSession() {
		final Properties props = QuerySMTPSecureConfig();
		return Session.getDefaultInstance(props, null);
	}
	
	private static void SendMail(Message message, Session session) throws AddressException, MessagingException {
		Properties props = QuerySMTPAccountConfig();
		message.setFrom(new InternetAddress(props.getProperty("mail.smtp.sender")));
		Transport transport = session.getTransport("smtp");
		transport.connect(props.getProperty("mail.smtp.host"),
						props.getProperty("mail.smtp.username"),
						props.getProperty("mail.smtp.password"));
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();			
	}
	
	public static String GenerateMailBodyWithTemplate(String templateName, Map<String, Object> map) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException {
		Configuration templateConfig = new Configuration(Configuration.VERSION_2_3_23);
		templateConfig.setClassForTemplateLoading(MailUtil.class, "/templates/");
		templateConfig.setDefaultEncoding("UTF-8");
		templateConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		Template template = templateConfig.getTemplate(templateName + ".ftl");
		return FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
	}
	
	
	public static void SendMail(String subject, String body, List<String> recipients)  throws Exception {
		Session session = CreateMailSession();
		Message message = new MimeMessage(session);
		message.setSubject(subject);
		message.setContent(body, "text/html;charset=UTF-8");
		Iterator<String> iter = recipients.iterator();
		while (iter.hasNext()) {
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(iter.next()));
		}
		
		SendMail(message, session);	
	}
	
	public static void SendMail(String subject, String body, List<String> recipients, String attachmentPath) throws Exception {
		Session session = CreateMailSession();
		Message message = new MimeMessage(session);
		message.setSubject(subject);
		Iterator<String> iter = recipients.iterator();
		while (iter.hasNext()) {
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(iter.next()));
		}
		
		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(body, "text/html;charset=UTF-8");
		
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);
		
		File attachment = new File(attachmentPath);
		if (attachment.isFile()) {
			messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(attachmentPath);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(attachment.getName());
			multipart.addBodyPart(messageBodyPart);
		}
		
		message.setContent(multipart);
		
		SendMail(message, session);		
	}
}
