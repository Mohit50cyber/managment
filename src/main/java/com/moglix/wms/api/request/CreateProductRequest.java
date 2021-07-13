package com.moglix.wms.api.request;

import javax.validation.constraints.NotBlank;

/**
 * @author pankaj on 24/5/19
 */
public class CreateProductRequest extends BaseRequest {
    private static final long serialVersionUID = -7020958969741661820L;

    @NotBlank
    private String productMsn;
    @NotBlank
    private String productName;
    @NotBlank
    private String uom;
    
    private String productBrand;
    
    private boolean lotManagementEnabled = false;
    
    private boolean expiryDateManagementEnabled = false;
    
    private boolean serializedProduct = false;
    
    private Integer shelfLife;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getProductMsn() {
        return productMsn;
    }

    public void setProductMsn(String productMsn) {
        this.productMsn = productMsn;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

	public boolean isSerializedProduct() {
        return serializedProduct;
    }

    public void setSerializedProduct(boolean serializedProduct) {
        this.serializedProduct = serializedProduct;
    }   
    
    public String getProductBrand() {
		return productBrand;
	}

	public void setProductBrand(String productBrand) {
		this.productBrand = productBrand;
	}
	
	public boolean isLotManagementEnabled() {
		return lotManagementEnabled;
	}

	public void setLotManagementEnabled(boolean lotManagementEnabled) {
		this.lotManagementEnabled = lotManagementEnabled;
	}

	public boolean isExpiryDateManagementEnabled() {
		return expiryDateManagementEnabled;
	}

	public void setExpiryDateManagementEnabled(boolean expiryDateManagementEnabled) {
		this.expiryDateManagementEnabled = expiryDateManagementEnabled;
	}

	public Integer getShelfLife() {
		return shelfLife;
	}

	public void setShelfLife(Integer shelfLife) {
		this.shelfLife = shelfLife;
	}

	@Override
	public String toString() {
		return "CreateProductRequest [productMsn=" + productMsn + ", productName=" + productName + ", uom=" + uom
				+ ", productBrand=" + productBrand + ", lotManagementEnabled=" + lotManagementEnabled
				+ ", expiryDateManagementEnabled=" + expiryDateManagementEnabled + ", serializedProduct="
				+ serializedProduct + "]";
	}
}
