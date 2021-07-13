package com.moglix.wms.specifications;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.moglix.wms.constants.Constants;
import com.moglix.wms.constants.InboundStatusType;
import com.moglix.wms.constants.InboundType;
import com.moglix.wms.entities.Inbound;

public class InboundSpecifications {
	
	private InboundSpecifications() {
		
	}

	public static Specification<Inbound> containsTextInAttributes(String text, List<String> attributes) {
	    String finalText = text;
	    return (root, query, builder) -> builder.or(root.getModel().getDeclaredSingularAttributes().stream()
	            .filter(a -> attributes.contains(a.getName()))
	            .map(a -> builder.equal(root.get(a.getName()), finalText))
	            .toArray(Predicate[]::new)
	    );
	}

	public static Specification<Inbound> containsTextInName(String text) {
	    return containsTextInAttributes(text, Constants.getInboundSearchColumns());
	}
	
	public static Specification<Inbound> hasWarehouse(Integer warehouseId) {
	    return new Specification<Inbound>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -8702504757541452499L;

			@Override
			public Predicate toPredicate(Root<Inbound> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				query.orderBy(criteriaBuilder.desc(root.get("created")));
				return criteriaBuilder.equal(root.get("warehouseId"), warehouseId);
			}
		};
	}
	
	public static Specification<Inbound> hasStatus(List<InboundType> status) {
	    return new Specification<Inbound>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -8702504757541452499L;

			@Override
			public Predicate toPredicate(Root<Inbound> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				return root.get("type").in(status);
			}
		};
	}
	
	public static Specification<Inbound> hasAction(List<InboundStatusType> action) {
	    return new Specification<Inbound>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -8702504757541452499L;

			@Override
			public Predicate toPredicate(Root<Inbound> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				return root.get("status").in(action);
			}
		};
	}
}
