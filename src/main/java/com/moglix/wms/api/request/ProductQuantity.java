package com.moglix.wms.api.request;

import java.io.Serializable;

public class ProductQuantity implements Serializable{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 8361718390969113836L;

		private String productMsn;
		
		private Double quantity;

		public String getProductMsn() {
			return productMsn;
		}

		public void setProductMsn(String productMsn) {
			this.productMsn = productMsn;
		}

		public Double getQuantity() {
			return quantity;
		}

		public void setQuantity(Double quantity) {
			this.quantity = quantity;
		}

		@Override
		public String toString() {
			return "ProductQuantity [productMsn=" + productMsn + ", quantity=" + quantity + "]";
		}
	}