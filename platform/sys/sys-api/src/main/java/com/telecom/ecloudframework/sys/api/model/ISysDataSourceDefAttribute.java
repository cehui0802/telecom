package com.telecom.ecloudframework.sys.api.model;

import java.io.Serializable;

public interface ISysDataSourceDefAttribute extends Serializable {

    String getName();

    String getComment();

    String getType();

    boolean isRequired();

    String getDefaultValue();

    String getValue();
}
