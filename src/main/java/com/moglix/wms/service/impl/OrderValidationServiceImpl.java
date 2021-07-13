package com.moglix.wms.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.google.gson.Gson;
import com.moglix.wms.api.response.GetProductDetailsByCatalogResponse;
import com.moglix.wms.constants.Constants;
import com.moglix.wms.dto.OrderValidateDTO;
import com.moglix.wms.dto.SalesOpsItemDetailsDTO;
import com.moglix.wms.dto.SalesOpsOrderDTO;
import com.moglix.wms.dto.SalesOpsOrderDetailsDTO;
import com.moglix.wms.entities.Product;
import com.moglix.wms.repository.ProductsRepository;
import com.moglix.wms.service.ICatalogService;
import com.moglix.wms.service.IOrderValidationService;
import com.moglix.wms.util.EmailBean;
import com.moglix.wms.util.MailUtil;

/**
 * @author sparsh saxena on 10/5/21
 */

@Service("orderValidationService")
public class OrderValidationServiceImpl implements IOrderValidationService {
	
	private static final Logger logger = LogManager.getLogger(OrderValidationServiceImpl.class);
	
    @Autowired
    private ProductsRepository productRepository;
    
    @Value("${wms.invalid.order.emailto}")
    private String emailTO_ForInvalidOrder;
    
    @Value("${wms.invalid.productmsn.emailto}")
    private String emailTO_ForInvalidProductMSN;

    @Value("${wms.invalid.order.email.cc}")
    private String ccEmailsForInvalidOrder;
    
    @Value("${wms.invalid.order.email.cc}")
    private String ccEmailsForInvalidProductMSN;
    
    @Value("${spring.profiles.active}")
    private String activeProfile;
    
	@Autowired
	private MailUtil mailUtil;
	
	@Autowired
	@Qualifier("catalogService")
	ICatalogService catalogService;
	
	public boolean validateOrder(SalesOpsOrderDTO salesOpsOrderDTO) {

		logger.info("Validating Order!!!");
		
		List<String> orderValidations         = new ArrayList<>();
		SalesOpsOrderDetailsDTO orderDetails  = salesOpsOrderDTO.getOrderDetails();
		List<SalesOpsItemDetailsDTO> items    = salesOpsOrderDTO.getItemDetails();

		// validate top level objects
        if (items == null || items.isEmpty())
            orderValidations.add("Order items");
       
        if (ObjectUtils.isEmpty(orderDetails.getCountryISONumber()))
            orderValidations.add("Order country");
                
        // send email if objects are null, and restrict order saving in WMS.
        if (orderValidations.size() > 0) {
            sendValidationEmail(new OrderValidateDTO(orderValidations), salesOpsOrderDTO, true);
            return false;
        }
		
		return true;
	}
	
	public Product validateProductMSN(String productMSN, String itemRef) {
		
		Product product = productRepository.getUniqueByProductMsn(productMSN);
		
		if (ObjectUtils.isEmpty(product) || product == null) {
			
            logger.warn(String.format("Order for MSN :[ %s ] and itemRef : [ %s ] failed to persist in wms system. [Invalid ProductMSN] ", productMSN, itemRef));
            mailUtil.sendOrderValidationEmail(constructValidationEmail(productMSN, getProductMsnValidationSubject(), emailTO_ForInvalidProductMSN, this.getOrderValidationEmailCC(ccEmailsForInvalidProductMSN), Constants.getProductNotSyncEmailContent(productMSN, itemRef)));
            product = syncProductMSN_FromCatalog(productMSN);
		}
		
		return product;
	}
		
	public Product syncProductMSN_FromCatalog(String productMSN) {
		
		logger.info(String.format("Creating productMSN :: %s in WMS!!!",productMSN));
		Product product = new Product();
		
		ResponseEntity<GetProductDetailsByCatalogResponse> response = catalogService.syncProductMSN(productMSN);
		
		logger.info("Inside syncProductMSN() :: Response : " + new Gson().toJson(response));
		
		if(response.getBody().getStatusCode().equals(HttpStatus.OK.value())) {
			
			logger.info("Inside syncProductMSN() :: Response : " + response.getBody().getStatusCode());
			
			product.setProductMsn(response.getBody().getProductDetails().get(0).getProductMsn());
			product.setProductName(response.getBody().getProductDetails().get(0).getProductName());
			product.setUom(response.getBody().getProductDetails().get(0).getUom());
			if(StringUtils.isNotBlank(response.getBody().getProductDetails().get(0).getBrand())) {
				product.setProductBrand(response.getBody().getProductDetails().get(0).getBrand());
			}
			product.setSerializedProduct(true);
			product.setExpiryDateManagementEnabled(response.getBody().getProductDetails().get(0).getIsExpiryEnabled());
			product.setLotManagementEnabled(response.getBody().getProductDetails().get(0).getIsLotEnabled());
			//if(request.isExpiryDateManagementEnabled() && request.getShelfLife() != null) {
				product.setShelfLife(0);
			//}
				
			product = productRepository.saveAndFlush(product);
			logger.info(String.format("Created productMSN :: %s in WMS. Product ID :: %s",productMSN, product.getId()));
		}
		
		return product;
    }
	
	private void sendValidationEmail(OrderValidateDTO orderValidateDTO, SalesOpsOrderDTO salesOpsOrderDTO, boolean isMailRequired) {
       
		String orderRef = salesOpsOrderDTO.getOrderDetails().getOrderRef();
        
		if (orderValidateDTO.getOrderValidations() != null && orderValidateDTO.getOrderValidations().size() > 0) {
            String message = "Invalid " + String.join(",", orderValidateDTO.getOrderValidations());
            logger.info(String.format("Order %s failed to persist in WMS system : %s ", orderRef, message));
            message += "\r\n";
            message += new Gson().toJson(salesOpsOrderDTO);
            logger.warn(message);
            if (isMailRequired)
            	mailUtil.sendOrderValidationEmail(constructValidationEmail(orderRef, getOrderValidationSubject(), emailTO_ForInvalidOrder, this.getOrderValidationEmailCC(ccEmailsForInvalidOrder), message));
        }
    }
	
	private String[] getOrderValidationEmailCC(String ccEmails) {
		
		List<String> cc = Stream.of(ccEmails.split(",")).collect(Collectors.toList());

        return cc.stream().toArray(String[]::new);
    }

	private EmailBean constructValidationEmail(String value, String subject, String mailTo, String[] cc, String message) {
        EmailBean emailBean = new EmailBean();
        emailBean.setSubject(String.format(subject, value));
        emailBean.setTo(mailTo);
        emailBean.setCc(cc);
        emailBean.setBody(message);
        return emailBean;
    }
	
	private String getOrderValidationSubject() {
        return activeProfile.equals("PROD") ? Constants.SUBJECT_ORDER_VALIDATION_EMAIL_PROD : Constants.SUBJECT_ORDER_VALIDATION_EMAIL_QA;
    }
	
	private String getProductMsnValidationSubject() {
        return activeProfile.equals("PROD") ? Constants.SUBJECT_PRODUCT_MSN_VALIDATION_EMAIL_PROD : Constants.SUBJECT_PRODUCT_MSN_VALIDATION_EMAIL_QA;
    }
}
