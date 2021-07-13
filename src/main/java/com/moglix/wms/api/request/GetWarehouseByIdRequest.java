package com.moglix.wms.api.request;

import javax.validation.constraints.NotNull;

/**
 * @author pankaj on 30/4/19
 */
public class GetWarehouseByIdRequest extends BaseRequest {
    private static final long serialVersionUID = 8337639935435387716L;

    @NotNull
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "GetWarehouseByIdRequest{" +
                "id=" + id +
                '}';
    }
}
