package com.moglix.wms.api.response;

import java.util.ArrayList;
import java.util.List;

import com.moglix.wms.dto.CountryDTO;

/**
 * @author sparsh saxena on 22/4/21
 */

public class CountryListResponse extends BaseResponse {

	private static final long serialVersionUID = 8605944932746473750L;
	
	private List<CountryDTO> countries = new ArrayList<>();

	public List<CountryDTO> getCountries() {
		return countries;
	}

	public void setCountries(List<CountryDTO> countries) {
		this.countries = countries;
	}
}
