package com.telecom.ecloudframework.sys.rest.ofd.custom.agent.callback;

import com.telecom.ecloudframework.sys.rest.ofd.custom.agent.ConvertCallback;

import java.io.IOException;
import java.io.OutputStream;

public class EmptyConvertCallback implements ConvertCallback {
    @Override
    public OutputStream openOutput() throws IOException {
        return null;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onFailed(String code, String message) {

    }

    @Override
    public void onException(Exception ex) {

    }

    @Override
    public void onFinally() {

    }
}
