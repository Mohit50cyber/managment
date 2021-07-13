package com.moglix.wms.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public  class Address {
	private String lastName;
	private String country;
	private String pincode;
	private String address2;
	private String city;
	private String address1;
	private String gstin;
	private String firstName;
	private String phone;
	private String bussinessName;
	private String stateCode;
	private String state;
	private String email;
}