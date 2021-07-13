package com.moglix.wms.api.request;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author pankaj on 3/5/19
 */
public class CreateInboundStorageRequest extends BaseRequest {
    private static final long serialVersionUID = -1875396629629811013L;

    @NotNull
    private Integer inboundId;
    
    private String binAssignedBy;

    @NotNull
    @Size(min = 1)
    @Valid
    private List<LocationToQuantity> items = new ArrayList<>();

    public Integer getInboundId() {
        return inboundId;
    }

    public void setInboundId(Integer inboundId) {
        this.inboundId = inboundId;
    }

    public List<LocationToQuantity> getItems() {
        return items;
    }

    public void setItems(List<LocationToQuantity> items) {
        this.items = items;
    }

    public String getBinAssignedBy() {
		return binAssignedBy;
	}

	public void setBinAssignedBy(String binAssignedBy) {
		this.binAssignedBy = binAssignedBy;
	}

	public static class LocationToQuantity {
        @NotNull
        private Integer storageLocationId;
        @NotNull
        private Double quantity;
        
        private Integer lotId;
        
        private String lotNumber;
        
        private Date expDate;
        
        public Integer getStorageLocationId() {
            return storageLocationId;
        }

        public void setStorageLocationId(Integer storageLocationId) {
            this.storageLocationId = storageLocationId;
        }

        public Double getQuantity() {
            return quantity;
        }

        public void setQuantity(Double quantity) {
            this.quantity = quantity;
        }
        
        

        public Integer getLotId() {
			return lotId;
		}

		public void setLotId(Integer lotId) {
			this.lotId = lotId;
		}

		public String getLotNumber() {
			return lotNumber;
		}

		public void setLotNumber(String lotNumber) {
			this.lotNumber = lotNumber;
		}
		
		

		public Date getExpDate() {
			return expDate;
		}

		public void setExpDate(Date expDate) {
			this.expDate = expDate;
		}

		@Override
		public String toString() {
			return "LocationToQuantity [storageLocationId=" + storageLocationId + ", quantity=" + quantity + ", lotId="
					+ lotId + ", lotNumber=" + lotNumber + ", expDate=" + expDate + "]";
		}
		
		
		

		
    }

	@Override
	public String toString() {
		return "CreateInboundStorageRequest [inboundId=" + inboundId + ", binAssignedBy=" + binAssignedBy + ", items="
				+ items + "]";
	}
}
