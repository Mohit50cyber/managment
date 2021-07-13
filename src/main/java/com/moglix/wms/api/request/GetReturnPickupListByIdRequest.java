package com.moglix.wms.api.request;

import javax.validation.constraints.NotNull;

/**
 * @author pankaj on 20/5/19
 */
public class GetReturnPickupListByIdRequest extends BaseRequest {
    private static final long serialVersionUID = 5221039080485685342L;

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
        return "GetReturnPickupListByIdRequest{" +
                "id=" + id +
                '}';
    }
}
