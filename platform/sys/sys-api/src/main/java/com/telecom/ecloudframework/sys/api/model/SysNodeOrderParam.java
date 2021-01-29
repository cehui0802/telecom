package com.telecom.ecloudframework.sys.api.model;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author lxy
 */
public class SysNodeOrderParam implements Serializable {

    @NotNull(message = "ID不能为空!")
    private String id;

    private int sn;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }
}
