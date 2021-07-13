package com.moglix.wms.api.request;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.google.gson.Gson;
import com.moglix.wms.constants.DangerType;
import com.moglix.wms.constants.InboundType;
import com.moglix.wms.constants.ProductType;
import com.moglix.wms.constants.StorageType;
import com.moglix.wms.validator.CheckValidTaxRate;

public class ProductInput extends BaseRequest {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -6229948506422124293L;
	
	@NotNull
	private Integer supplierPoId;
	
	@NotNull
	private Integer supplierPoItemId;

	private ProductType type;
	
	private InboundType inboundType;
	
	@NotBlank
	private String productMsn;
	
	private Double inventrisableQuantity = 0.0d;
	
	private Boolean inventorize = true;
	
	private Boolean isJunkInventory = false;
	
	@NotBlank
	private String uom;
	
	private StorageType storageType;
	
	private DangerType dangerType;
	
	@NotNull
	private Boolean isSerializedProduct = false;
	
	@NotNull
	private Double quantity;
	
	@NotNull
	private Double purchasePrice;
	
	@NotBlank
	private String productName;
	
	private Date mfgDate; 
	private Date expDate;
	
	private String supplierName;
	
	private Integer supplierId;
	
	private Double returnedQuantity;
			
	@NotNull
	@CheckValidTaxRate
	private Double tax;
	
	private List<LotInfo> lotInfo;
	
	private List<ItemRefDetail> itemRefDetails;
	
	
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
	
	public ProductType getType() {
		return type;
	}
	
	public void setType(ProductType type) {
		this.type = type;
	}
	
	public InboundType getInboundType() {
		return inboundType;
	}
	public void setInboundType(InboundType inboundType) {
		this.inboundType = inboundType;
	}
	
	public String getProductMsn() {
		return productMsn;
	}
	
	public void setProductMsn(String productMsn) {
		this.productMsn = productMsn;
	}
	
	public Double getInventrisableQuantity() {
		return inventrisableQuantity;
	}
	
	public void setInventrisableQuantity(Double inventrisableQuantity) {
		this.inventrisableQuantity = inventrisableQuantity;
	}
	
	public Boolean getInventorize() {
		return inventorize;
	}
	
	public void setInventorize(Boolean inventorize) {
		this.inventorize = inventorize;
	}
	
	public Boolean getIsJunkInventory() {
		return isJunkInventory;
	}
	
	public void setIsJunkInventory(Boolean isJunkInventory) {
		this.isJunkInventory = isJunkInventory;
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
	
	public void setDangerType(DangerType dangerType) {
		this.dangerType = dangerType;
	}
	
	public Boolean getIsSerializedProduct() {
		return isSerializedProduct;
	}
	
	public void setIsSerializedProduct(Boolean isSerializedProduct) {
		this.isSerializedProduct = isSerializedProduct;
	}
	
	public Double getQuantity() {
		return quantity;
	}
	
	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}
	
	public Double getPurchasePrice() {
		return purchasePrice;
	}
	
	public void setPurchasePrice(Double purchasePrice) {
		this.purchasePrice = purchasePrice;
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
	
	public Double getReturnedQuantity() {
		return returnedQuantity;
	}
	
	public void setReturnedQuantity(Double returnedQuantity) {
		this.returnedQuantity = returnedQuantity;
	}
		
	public Double getTax() {
		return tax;
	}
	
	public void setTax(Double tax) {
		this.tax = tax;
	}
		
	public List<LotInfo> getLotInfo() {
		return lotInfo;
	}
	public void setLotInfo(List<LotInfo> lotInfo) {
		this.lotInfo = lotInfo;
	}
	
	public List<ItemRefDetail> getItemRefDetails() {
		return itemRefDetails;
	}

	public void setItemRefDetails(List<ItemRefDetail> itemRefDetails) {
		this.itemRefDetails = itemRefDetails;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	
	
	public static class LotInfo implements Serializable {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 2375190520633530356L;
		private String lotNumber;
		private Double  quantity;
		
		public String getLotNumber() {
			return lotNumber;
		}
		
		public void setLotNumber(String lotNumber) {
			this.lotNumber = lotNumber;
		}
		
		public Double getQuantity() {
			return quantity;
		}
		
		public void setQuantity(Double quantity) {
			this.quantity = quantity;
		}
	}
	
	public static class ItemRefDetail implements Serializable {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -1936670162473476092L;
		
		private String itemRef;
		private Double quantity;
		
		public String getItemRef() {
			return itemRef;
		}
		
		public void setItemRef(String itemRef) {
			this.itemRef = itemRef;
		}
		
		public Double getQuantity() {
			return quantity;
		}
		
		public void setQuantity(Double quantity) {
			this.quantity = quantity;
		}
	}
	
	
	//A ProductInput object is equal if its productMsn and supplierId is same 
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
		
		if(obj instanceof ProductInput) {
			ProductInput pi = (ProductInput) obj;
			return (pi.productMsn.equals(this.productMsn) && Double.compare(pi.supplierId, this.supplierId) == 0);
		}else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		int hashcode = 0;
		hashcode = supplierId*20;
		hashcode += productMsn.hashCode();
		return hashcode;
	}
}
