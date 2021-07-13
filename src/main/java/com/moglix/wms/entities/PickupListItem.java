package com.moglix.wms.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * @author pankaj on 14/5/19
 */
@Entity
@Table(name = "pickup_list_item")
public class PickupListItem implements Serializable {

    private static final long serialVersionUID = -5208591017793832492L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pickup_list_id", nullable = false)
    private PickupList pickupList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "packet_item_id", nullable = false)
    private PacketItem packetItem;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date created = new Date();

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date modified = new Date();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PickupList getPickupList() {
        return pickupList;
    }

    public void setPickupList(PickupList pickupList) {
        this.pickupList = pickupList;
    }

    public PacketItem getPacketItem() {
        return packetItem;
    }

    public void setPacketItem(PacketItem packetItem) {
        this.packetItem = packetItem;
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
}
