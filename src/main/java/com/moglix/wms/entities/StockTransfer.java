package com.moglix.wms.entities;


import com.moglix.wms.constants.HsnSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "stock_transfer")
public class StockTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_transfer_id")
    Long stockTransferId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_transfer_note_id", nullable = false)
    StockTransferNote stockTransferNote;

    @Column(nullable = false, name = "inbound_id")
    int inboundId;

    @Column(nullable = false, name = "supplier_id")
    int supplierId;
    @Column(nullable = false, name = "supplier_po_id")
    int supplierPoId;
    @Column(nullable = false, name = "supplier_po_item_id")
    int supplierPoItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false, name = "purchase_price")
    private Double purchasePrice;

    @Column(nullable = false, name = "tax_percentage")
    private Double taxPercentage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storage_location_id")
    private StorageLocation storageLocation; // inboundstorage table.  bin


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inbound_storage_id")
    private InboundStorage inboundStorage;

    @Column(nullable = false)
    private Double quantity;

    @Column(name = "hsn_code")
    private String hsnCode;

    @Column(name = "hsn_source")
    @Enumerated(EnumType.STRING)
    private HsnSource hsnSource;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, name = "created_date")
    private Date createdDate;
}
