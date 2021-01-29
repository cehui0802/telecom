package com.telecom.ecloudframework.sys.rest.ofd.custom.agent;

import java.io.IOException;
import java.io.OutputStream;

public interface ConvertCallback {

    OutputStream openOutput() throws IOException;

    void onStart();

    void onSuccess();

    void onFailed(String code, String message);

    void onException(Exception ex);

    void onFinally();

}
