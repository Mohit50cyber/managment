package com.moglix.wms.util;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author sparsh saxena on 10/5/21
 */

@JsonInclude (JsonInclude.Include.NON_NULL)
public class EmailBean {
	
	@NotNull (message = "email to is mandatory")
    private String to;

    private String[] cc;

    @NotNull (message = "subject is mandatory")
    private String subject;

    @NotNull (message = "remarks is mandatory")
    private String body;
    
    public EmailBean() {
	}

    public EmailBean(String to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
    }

    public EmailBean(String to, String subject, String body, String[] cc) {
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.cc = cc;
    }

	
	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String[] getCc() {
		return cc;
	}

	public void setCc(String[] cc) {
		this.cc = cc;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
}
