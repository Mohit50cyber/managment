package com.moglix.wms.api.request;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.moglix.wms.constants.OrderType;

/**
 * @author pankaj on 6/5/19
 */
public class CreateSaleOrderRequest extends BaseRequest {
   
	private static final long serialVersionUID = 8343089097599234526L;

	@NotNull
    private Integer emsOrderId;

    @NotNull
    private Integer fulfillmentWarehouseId;
    
    // private boolean isCloned = false;
    
    // private String cloneOf;
    
    private String bulkInvoiceId;
    
    private String batchRef;
    
    private Integer plantId;
    
    private String inventory_id;
    
    @NotNull
    private int orderType = OrderType.NEW.getCode();
    
    private String orderRef;
    
    @NotNull
    private Integer countryId;
    
    @NotNull
    @Size(min=1)
    @Valid
    private List<EmsOrderItem> items = new ArrayList<>();
    
    
    public Integer getEmsOrderId() {
        return emsOrderId;
    }

    public void setEmsOrderId(Integer emsOrderId) {
        this.emsOrderId = emsOrderId;
    }

    public Integer getFulfillmentWarehouseId() {
        return fulfillmentWarehouseId;
    }

    public void setFulfillmentWarehouseId(Integer fulfillmentWarehouseId) {
        this.fulfillmentWarehouseId = fulfillmentWarehouseId;
    }
    
//    public boolean isCloned() {
//		return isCloned;
//	}

//	public void setCloned(boolean isCloned) {
//		this.isCloned = isCloned;
//	}
	
//	public String getCloneOf() {
//		return cloneOf;
//	}

//	public void setCloneOf(String cloneOf) {
//		this.cloneOf = cloneOf;
//	}

	public String getBulkInvoiceId() {
		return bulkInvoiceId;
	}

	public void setBulkInvoiceId(String bulkInvoiceId) {
		this.bulkInvoiceId = bulkInvoiceId;
	}

	public String getBatchRef() {
		return batchRef;
	}

	public void setBatchRef(String batchRef) {
		this.batchRef = batchRef;
	}
	
	public Integer getPlantId() {
		return plantId;
	}

	public void setPlantId(Integer plantId) {
		this.plantId = plantId;
	}

	public String getInventory_id() {
		return inventory_id;
	}

	public void setInventory_id(String inventory_id) {
		this.inventory_id = inventory_id;
	}

	public int getOrderType() {
		return orderType;
	}

	public void setOrderType(int orderType) {
		this.orderType = orderType;
	}

    public String getOrderRef() {
		return orderRef;
	}

	public void setOrderRef(String orderRef) {
		this.orderRef = orderRef;
	}

	public Integer getCountryId() {
		return countryId;
	}

	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}

	public List<EmsOrderItem> getItems() {
        return items;
    }

    public void setItems(List<EmsOrderItem> items) {
        this.items = items;
    }

	@Override
	public String toString() {
		return "CreateSaleOrderRequest [emsOrderId=" + emsOrderId + ", fulfillmentWarehouseId=" + fulfillmentWarehouseId
				+ ", bulkInvoiceId=" + bulkInvoiceId + ", batchRef=" + batchRef + ", plantId=" + plantId
				+ ", inventory_id=" + inventory_id + ", orderType=" + orderType + ", orderRef=" + orderRef
				+ ", countryId=" + countryId + ", items=" + items + "]";
	}
    
	// Nested Class

	public static class EmsOrderItem {

		private Integer emsOrderItemId;

        private String itemRef;

        @NotBlank
        private String productMsn;

        @NotNull
        private Double orderedQuantity;

        private String remark;
        
        private boolean isCloned = false;
        
        private String cloneOf;
        
		public Integer getEmsOrderItemId() {
            return emsOrderItemId;
        }

        public void setEmsOrderItemId(Integer emsOrderItemId) {
            this.emsOrderItemId = emsOrderItemId;
        }

        public String getProductMsn() {
            return productMsn;
        }

        public void setProductMsn(String productMsn) {
            this.productMsn = productMsn;
        }

        public Double getOrderedQuantity() {
            return orderedQuantity;
        }

        public void setOrderedQuantity(Double orderedQuantity) {
            this.orderedQuantity = orderedQuantity;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getItemRef() {
            return itemRef;
        }

        public void setItemRef(String itemRef) {
            this.itemRef = itemRef;
        }
        
        public boolean isCloned() {
    		return isCloned;
    	}

    	public void setCloned(boolean isCloned) {
    		this.isCloned = isCloned;
    	}
    	
    	public String getCloneOf() {
    		return cloneOf;
    	}

    	public void setCloneOf(String cloneOf) {
    		this.cloneOf = cloneOf;
    	}
    	
		@Override
		public String toString() {
			return "EmsOrderItem [emsOrderItemId=" + emsOrderItemId + ", itemRef=" + itemRef + ", productMsn="
					+ productMsn + ", orderedQuantity=" + orderedQuantity + ", remark=" + remark + ", isCloned="
					+ isCloned + ", cloneOf=" + cloneOf + "]";
		}
    }
}