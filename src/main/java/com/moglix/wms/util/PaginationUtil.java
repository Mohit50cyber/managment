package com.moglix.wms.util;

import org.springframework.data.domain.Page;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.moglix.wms.api.response.PaginationResponse;
import com.moglix.wms.constants.Constants;

public class PaginationUtil {
	
	private PaginationUtil() {
		
	}
	public static PaginationResponse setPaginationParams(Page<?> pages, PaginationResponse response) {
		
		String nextUri = null;
		String prevUri = null;
		
		if(pages.hasNext()) {
			ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentRequestUri();
			nextUri = builder.scheme("https").host(Constants.WMS_API_BASE_URL_QA).port(-1).replaceQueryParam("page", pages.getNumber() + 1)
					.replaceQueryParam("size", pages.getSize())
					.toUriString();
		}
		
		if(pages.hasPrevious()) {
			ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentRequestUri();
			if(pages.getNumber() > 1) {
				prevUri = builder.scheme("https").host(Constants.WMS_API_BASE_URL_QA).port(-1).replaceQueryParam("page", pages.getNumber() - 1)
						.replaceQueryParam("size", pages.getSize())
						.toUriString();				
			}
		}
		
		response.setNext(nextUri);
		response.setPrev(prevUri);
		response.setSize(pages.getSize());
		response.setTotalCount((int)pages.getTotalElements());
		response.setPageCount(pages.getTotalPages());
		response.setPageNumber(pages.getNumber());		
		return response;
	}
}
