package com.moglix.ems.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name="enterprise_order_item")
@Data
@NoArgsConstructor
public class EnterpriseOrderItem {
    @Id
    private Integer id;
    @Column(name = "item_ref")
    private String itemRef;
    @Column(name = "hsn_code")
    private String hsnCode;
    @Column(name = "is_pushed_to_wms")
    private byte isPushedToWms;

}
