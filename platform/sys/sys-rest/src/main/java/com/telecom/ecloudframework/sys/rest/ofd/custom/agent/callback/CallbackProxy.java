package com.telecom.ecloudframework.sys.rest.ofd.custom.agent.callback;

import com.telecom.ecloudframework.sys.rest.ofd.custom.agent.ConvertCallback;
import com.telecom.ecloudframework.sys.rest.ofd.custom.agent.TransferCallback;

import java.io.IOException;
import java.io.OutputStream;

public class CallbackProxy implements ConvertCallback, TransferCallback {

    private ConvertCallback cc;
    private TransferCallback tc;

    public CallbackProxy(OutputStream out) {
        this(new SimpleCallback(out));
    }

    public CallbackProxy(ConvertCallback cc) {
        this.cc = cc;
        if (cc instanceof TransferCallback) {
            this.tc = (TransferCallback) cc;
        }
        if (this.cc == null) {
            this.cc = new EmptyConvertCallback();
        }
        if (this.tc == null) {
            this.tc = new EmptyTransferCallback();
        }
    }

    @Override
    public OutputStream openOutput() throws IOException {
        return this.cc.openOutput();
    }

    @Override
    public void onStart() {
        this.cc.onStart();
    }

    @Override
    public void onSuccess() {
        try {
            this.cc.onSuccess();
        } finally {
            this.onFinally();
        }
    }

    @Override
    public void onFailed(String code, String message) {
        try {
            this.cc.onFailed(code, message);
        } finally {
            this.onFinally();
        }
    }

    @Override
    public void onException(Exception ex) {
        try {
            this.cc.onException(ex);
        } finally {
            this.onFinally();
        }
    }

    @Override
    public void onFinally() {
        this.cc.onFinally();
    }

    @Override
    public void onPackStart() {
        this.tc.onPackStart();
    }

    @Override
    public void onPackEnd() {
        this.tc.onPackEnd();
    }

    @Override
    public void onUploadStart() {
        this.tc.onUploadStart();
    }

    @Override
    public void onUploadEnd() {
        this.tc.onUploadEnd();
    }

    @Override
    public void onTicket(String ticket) {
        this.tc.onTicket(ticket);
    }

    @Override
    public void onDownloadStart() {
        this.tc.onDownloadStart();
    }

    @Override
    public void onDownloadEnd() {
        this.tc.onDownloadEnd();
    }
}
