package com.moglix.wms.entities;


import com.moglix.wms.constants.StockTransferType;
import com.moglix.wms.constants.StockTransferNoteStatus;
import com.moglix.wms.constants.StockTransferNoteType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
@Table(name = "stock_transfer_note")
public class StockTransferNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_transfer_note_id")
    Long stockTransferNoteId;

    @Column(nullable = false, name = "warehouse_from")
    int warehouseFrom;

    @Column(nullable = false, name = "warehouse_to")
    int warehouseTo;

    @Column(nullable = false, name = "pickup_date")
    @Temporal(TemporalType.DATE)
    private Date pickupDate = new Date();

    @Column(nullable = false, name = "stock_transfer_note_type")
    @Enumerated(EnumType.STRING)
    private StockTransferNoteType stnType;

    @Column(nullable = false, name = "stock_transfer_note_status")
    @Enumerated(EnumType.STRING)
    private StockTransferNoteStatus stnStatus;

    @Column(name = "item_ref")
    private String itemRef;

    private Double quantity;

    @Column(name = "parent_stn_id")
    private Long parentStnId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "stockTransferNote", cascade =
            CascadeType.ALL)
    private List<StockTransfer> stockTransferList;

    @Column(nullable = false, name = "stock_transfer_type")
    @Enumerated(EnumType.STRING)
    private StockTransferType stockTransferType;

    private String remarks;

    @Column(name = "freight_charge")
    private Double freightCharge = 0d;

    @Column(name = "misc_charges")
    private Double miscCharges = 0d;
    @Column(name = "failure_reason")
    private String failureReason;
    @Column(name = "invoice_or_challan_url")
    String invoiceOrChallanUrl;

    @Column(name = "invoice_or_challan_number")
    String invoiceOrChallanNumber;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, name = "created_date")
    private Date createdDate;
}
