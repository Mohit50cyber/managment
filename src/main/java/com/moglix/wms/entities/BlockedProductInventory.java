package com.moglix.wms.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.moglix.wms.constants.BlockedProductInventoryStatus;

/**
 * 
 * @author Vaibhav Thapliyal
 *
 */
@Entity
@Table(name = "blocked_product_inventory", uniqueConstraints=@UniqueConstraint(columnNames={"product_msn", "warehouse_id","unique_block_transaction_id"}))
public class BlockedProductInventory implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5082094671432947479L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column (name = "blocked_quantity")
    private Double blockedQuantity = 0.0d;
	
	@Enumerated(EnumType.STRING)
	@Column (name = "status")
    private BlockedProductInventoryStatus status = BlockedProductInventoryStatus.BLOCKED;
	
	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
	private Date created = new Date();

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@UpdateTimestamp
	private Date modified = new Date();
	
	@Column (name = "warehouse_id")
    private Integer warehouseId;
	
	@Column (name = "product_msn")
    private String productMsn;
	
	@Column (name = "unique_block_transaction_id")
    private String uniqueblockid;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Double getBlockedQuantity() {
		return blockedQuantity;
	}

	public void setBlockedQuantity(Double blockedQuantity) {
		this.blockedQuantity = blockedQuantity;
	}

	public BlockedProductInventoryStatus getStatus() {
		return status;
	}

	public void setStatus(BlockedProductInventoryStatus status) {
		this.status = status;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getProductMsn() {
		return productMsn;
	}

	public void setProductMsn(String productMsn) {
		this.productMsn = productMsn;
	}
	
	public String getUniqueblockid() {
		return uniqueblockid;
	}

	public void setUniqueblockid(String uniqueblockid) {
		this.uniqueblockid = uniqueblockid;
	}

}
