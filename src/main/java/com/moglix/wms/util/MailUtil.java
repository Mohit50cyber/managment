package com.moglix.wms.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @author sparsh saxena on 10/5/21
 */

@Component
public class MailUtil {
	
	@Value("${spring.application.mode}")
    private String mode;
		
    @Value("${wms.care.mail}")
    private String supportEmail;
	
	@Autowired
	@Qualifier("mailer")
	private Mailer mailer;
	
    private HashMap<Integer, List<String>> warehouseToEmailMapping = new HashMap<>();
	
	private Logger logger = LogManager.getLogger(MailUtil.class);
	
	public void sendMail(String mailContent, String subject, int warehouseId) throws IOException, MessagingException {

		Email email = EmailBuilder.startingBlank()
				.from(new InternetAddress(mode == "sandbox" ? "sparsh.eronmicro@moglix.com" : "care@moglix.com"))
				.toMultiple(addRecipientsForWarehouse(warehouseId)).withPlainText(mailContent).withSubject(subject).buildEmail();
		mailer.sendMail(email, true);
    }
	
	private InternetAddress[] addRecipientsForWarehouse(int warehouseId) throws MessagingException {
	
		logger.debug("Inside addRecipientsForWarehouse()");
		
		List<InternetAddress> recipients = new ArrayList<>();
		
        if (mode.equalsIgnoreCase("sandbox")) {
        	
        	recipients.add(new InternetAddress("naman.jain@moglix.com"));
        	recipients.add(new InternetAddress("alok.kumar@moglix.com"));
        	recipients.add(new InternetAddress("sparsh.eronmicro@moglix.com"));
        	
        } else if (mode.equalsIgnoreCase("live")) {

            List<String> toMailByMap = warehouseToEmailMapping.get(warehouseId);
            
            if (!CollectionUtils.isEmpty(toMailByMap)) {
            	for(String address: toMailByMap) {
            		recipients.add(new InternetAddress(address));
            	}
            } else {
                recipients.add(new InternetAddress("care@moglix.com"));
            }
        }
    	return recipients.toArray(new InternetAddress [recipients.size()]);
    }
	
	public Boolean sendOrderValidationEmail(EmailBean emailBean) {
        try {
			Email email = EmailBuilder.startingBlank()
    				.from(new InternetAddress(supportEmail))
    				.toMultiple(emailBean.getTo())
    				.ccMultiple(emailBean.getCc())
    				.withPlainText(emailBean.getBody())
    				.withSubject(emailBean.getSubject()).buildEmail();
    		mailer.sendMail(email, true);
            
            logger.info(String.format("Email has been sent to %s,cc %s with body : %s", emailBean.getTo(), Arrays.toString(emailBean.getCc()), emailBean.getBody()));
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error(String.format("Error while sending email to %s,cc %s with body : %s", emailBean.getTo(), Arrays.toString(emailBean.getCc()), emailBean.getBody()), e.getMessage());
            return false;
        }
    }

	@PostConstruct
    private void initMap() {
       
		List<String> ccaWarehouse = new ArrayList<>();
        
		ccaWarehouse.add("delhi-warehouse@moglix.com");
        ccaWarehouse.add("haryana-warehouse@moglix.com");
        warehouseToEmailMapping.put(1, ccaWarehouse);
        
        ccaWarehouse = new ArrayList<>();
        ccaWarehouse.add("purchase.west@moglix.com");
        warehouseToEmailMapping.put(2, ccaWarehouse);
      
        ccaWarehouse = new ArrayList<>();
        ccaWarehouse.add("delhi-warehouse@moglix.com");
        ccaWarehouse.add("haryana-warehouse@moglix.com");
        
        warehouseToEmailMapping.put(3, ccaWarehouse);
        ccaWarehouse = new ArrayList<>();
        ccaWarehouse.add("purchase.south@moglix.com");
        warehouseToEmailMapping.put(4, ccaWarehouse);
        
        ccaWarehouse = new ArrayList<>();
        ccaWarehouse.add("delhi-warehouse@moglix.com");
        ccaWarehouse.add("haryana-warehouse@moglix.com");
        warehouseToEmailMapping.put(5, ccaWarehouse);
        
        ccaWarehouse = new ArrayList<>();
        ccaWarehouse.add("purchase.mumbai@moglix.com");
        warehouseToEmailMapping.put(6, ccaWarehouse);

        ccaWarehouse = new ArrayList<>();
        ccaWarehouse.add("ahmedabadteam@moglix.com");
        warehouseToEmailMapping.put(7, ccaWarehouse);

        ccaWarehouse = new ArrayList<>();
        ccaWarehouse.add("aurangabad-team@moglix.com");
        warehouseToEmailMapping.put(14, ccaWarehouse);

        ccaWarehouse = new ArrayList<>();
        ccaWarehouse.add("purchase.west@moglix.com");
        warehouseToEmailMapping.put(16, ccaWarehouse);

        ccaWarehouse = new ArrayList<>();
        ccaWarehouse.add("haridwar-warehouse@moglix.com");
        warehouseToEmailMapping.put(17, ccaWarehouse);

        ccaWarehouse = new ArrayList<>();
        ccaWarehouse.add("team.ludhiana@moglix.com");
        warehouseToEmailMapping.put(11, ccaWarehouse);

        ccaWarehouse = new ArrayList<>();
        ccaWarehouse.add("pantnagar@moglix.com");
        warehouseToEmailMapping.put(12, ccaWarehouse);

        ccaWarehouse = new ArrayList<>();
        ccaWarehouse.add("purchase.bangalore@moglix.com");
        warehouseToEmailMapping.put(13, ccaWarehouse);
        
        ccaWarehouse = new ArrayList<>();
        ccaWarehouse.add("omt-east@moglix.com");
        warehouseToEmailMapping.put(8, ccaWarehouse);
        
        ccaWarehouse = new ArrayList<>();
        ccaWarehouse.add("omt-east@moglix.com");
        warehouseToEmailMapping.put(18, ccaWarehouse);
        
        ccaWarehouse = new ArrayList<>();
        ccaWarehouse.add("omt-east@moglix.com");
        warehouseToEmailMapping.put(20, ccaWarehouse);
        
        ccaWarehouse = new ArrayList<>();
        ccaWarehouse.add("purchase.silvassa@moglix.com");
        warehouseToEmailMapping.put(21, ccaWarehouse);
        
        ccaWarehouse = new ArrayList<>();
        ccaWarehouse.add("care@moglix.com");
        warehouseToEmailMapping.put(9, ccaWarehouse);
        
        ccaWarehouse = new ArrayList<>();
        ccaWarehouse.add("care@moglix.com");
        warehouseToEmailMapping.put(10, ccaWarehouse);
        
        ccaWarehouse = new ArrayList<>();
        ccaWarehouse.add("hyderabad-warehouse@moglix.com");
        warehouseToEmailMapping.put(15, ccaWarehouse);
        
        ccaWarehouse = new ArrayList<>();
        ccaWarehouse.add("team.madurai@moglix.com");
        warehouseToEmailMapping.put(19, ccaWarehouse);
        
        ccaWarehouse = new ArrayList<>();
        ccaWarehouse.add("vadodara-warehouse@moglix.com");
        warehouseToEmailMapping.put(22, ccaWarehouse);
        
        ccaWarehouse = new ArrayList<>();
        ccaWarehouse.add("brijwasan-warehouse@moglix.com");
        warehouseToEmailMapping.put(23, ccaWarehouse);
        
        ccaWarehouse = new ArrayList<>();
        ccaWarehouse.add("noida-warehouse@moglix.com");
        warehouseToEmailMapping.put(24, ccaWarehouse);
	}
}
