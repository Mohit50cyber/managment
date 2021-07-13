package com.moglix.wms.dto;

import com.moglix.wms.entities.Zone;

import java.util.Date;

/**
 * @author pankaj on 1/5/19
 */
public class ZoneDto {

    private Integer id;
    private String name;
    private String warehouseName;
    private Date created;
    private Date modified;

    public ZoneDto() {
    }

    public ZoneDto(Zone obj) {
        this.id = obj.getId();
        this.name = obj.getName();
        this.warehouseName = obj.getWarehouse().getName();
        this.created = obj.getCreated();
        this.modified = obj.getModified();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
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
