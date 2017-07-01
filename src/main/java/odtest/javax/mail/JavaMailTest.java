package odtest.javax.mail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaMailTest {

	public JavaMailTest() {
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args) throws Exception {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("title", "Freemarker Template Test");
			map.put("bodyTitle",	"Body Title");
			map.put("message", "This is a mail generated by Freemarker,");
			String mailBody = MailUtil.GenerateMailBodyWithTemplate("test_template", map); 
			
			List<String> recipients = new ArrayList<String>();
			recipients.add("");
			recipients.add("");
			MailUtil.SendMail("Test MailUtil", mailBody, recipients, "");
			
			System.out.println("Done");
		} catch (Exception  e) {
			throw new RuntimeException(e);
		} 
	}
}
