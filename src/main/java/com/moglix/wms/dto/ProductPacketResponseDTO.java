package com.moglix.wms.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.moglix.wms.constants.DangerType;
import com.moglix.wms.constants.ProductType;
import com.moglix.wms.constants.StorageType;

public final class ProductPacketResponseDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7451039840195943558L;

	private ProductType type;
	
	@NotBlank
	private String productMsn;
	
	private Boolean inventorize = true;
	
	@NotBlank
	private String uom;
	
	private StorageType storageType;
	
	private DangerType dangerType;
	
	@NotNull
	private Boolean isSerializedProduct = false;
	
	@NotBlank
	private String productName;
	
	private Date mfgDate; 
	private Date expDate;
	
	private Double returnedQuantity;
	
	private Set<SupplierInfo> supplierDetails = new HashSet<>();
	
	public final static class SupplierInfo implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 5426600742739800460L;

		@NotNull
		private Double quantity;
		
		//adding extra column
		
		transient private String mrnId;
		
		private String supplierName;
		
		private Integer supplierId;
		
		private Integer supplierPoId;
		
		private Integer supplierPoItemId;
		
		private Double purchasePrice;
		
		private Double tax;
	
		public Double getPurchasePrice() {
			return purchasePrice;
		}

		public void setPurchasePrice(Double purchasePrice) {
			this.purchasePrice = purchasePrice;
		}

		public Double getQuantity() {
			return quantity;
		}

		public void setQuantity(Double quantity) {
			this.quantity = quantity;
		}

		public String getSupplierName() {
			return supplierName;
		}

		public void setSupplierName(String supplierName) {
			this.supplierName = supplierName;
		}

		public Integer getSupplierId() {
			return supplierId;
		}

		public void setSupplierId(Integer supplierId) {
			this.supplierId = supplierId;
		}
		
		public Integer getSupplierPoId() {
			return supplierPoId;
		}

		public void setSupplierPoId(Integer supplierPoId) {
			this.supplierPoId = supplierPoId;
		}

		public Integer getSupplierPoItemId() {
			return supplierPoItemId;
		}

		public void setSupplierPoItemId(Integer supplierPoItemId) {
			this.supplierPoItemId = supplierPoItemId;
		}

		public Double getTax() {
			return tax;
		}

		public void setTax(Double tax) {
			this.tax = tax;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (obj == this) {
				return true;
			}
			if (obj.getClass() != getClass()) {
				return false;
			}
			
			if(obj instanceof SupplierInfo) {
				SupplierInfo pi = (SupplierInfo) obj;
				//return (Double.compare(pi.quantity, this.quantity) == 0 && Double.compare(pi.supplierId, this.supplierId) == 0 && Double.compare(pi.purchasePrice, this.purchasePrice) == 0);
				return (Double.compare(pi.quantity, this.quantity) == 0 
						&& Double.compare(pi.supplierId, this.supplierId) == 0 
						&& Double.compare(pi.purchasePrice, this.purchasePrice) == 0
						&& Double.compare(pi.supplierPoId, this.supplierPoId)==0
						&& Double.compare(pi.supplierPoItemId,this.supplierPoItemId)==0
						&& pi.mrnId.equals(this.mrnId)
						);
			}else {
				return false;
			}
		}
		
		@Override
		public int hashCode() {
			int hashcode = 0;
			hashcode = supplierId*20;
			hashcode += quantity.hashCode();
			hashcode += purchasePrice.hashCode();
			hashcode += supplierPoId.hashCode();
			hashcode += supplierPoItemId.hashCode();
			hashcode += mrnId.hashCode();
					
			return hashcode;
		}

		public String getMrnId() {
			return mrnId;
		}

		public void setMrnId(String mrnId) {
			this.mrnId = mrnId;
		}
		
	}

	public ProductType getType() {
		return type;
	}

	public void setType(ProductType type) {
		this.type = type;
	}

	public String getProductMsn() {
		return productMsn;
	}

	public void setProductMsn(String productMsn) {
		this.productMsn = productMsn;
	}

	public Boolean getInventorize() {
		return inventorize;
	}

	public void setInventorize(Boolean inventorize) {
		this.inventorize = inventorize;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public StorageType getStorageType() {
		return storageType;
	}

	public void setStorageType(StorageType storageType) {
		this.storageType = storageType;
	}

	public DangerType getDangerType() {
		return dangerType;
	}

	public Double getReturnedQuantity() {
		return returnedQuantity;
	}

	public void setReturnedQuantity(Double returnedQuantity) {
		this.returnedQuantity = returnedQuantity;
	}

	public void setDangerType(DangerType dangerType) {
		this.dangerType = dangerType;
	}

	public Boolean getIsSerializedProduct() {
		return isSerializedProduct;
	}

	public void setIsSerializedProduct(Boolean isSerializedProduct) {
		this.isSerializedProduct = isSerializedProduct;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Date getMfgDate() {
		return mfgDate;
	}

	public void setMfgDate(Date mfgDate) {
		this.mfgDate = mfgDate;
	}

	public Date getExpDate() {
		return expDate;
	}

	public void setExpDate(Date expDate) {
		this.expDate = expDate;
	}

	public Set<SupplierInfo> getSupplierDetails() {
		return supplierDetails;
	}

	public void setSupplierDetails(Set<SupplierInfo> supplierDetails) {
		this.supplierDetails = supplierDetails;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		
		if(obj instanceof ProductPacketResponseDTO) {
			ProductPacketResponseDTO pi = (ProductPacketResponseDTO) obj;
			return (pi.productMsn.equals(this.productMsn));
		}else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		int hashcode = 0;
		hashcode += productMsn.hashCode();
		return hashcode;
	}
}
