package com.telecom.ecloudframework.sys.rest.ofd.custom.agent.callback;

import java.io.IOException;
import java.io.OutputStream;

import com.telecom.ecloudframework.sys.rest.ofd.custom.agent.ConvertCallback;

@Deprecated
public class BeaconCallback implements ConvertCallback {

    private ConvertCallback orgin;

    public BeaconCallback(ConvertCallback callback) {
        this.orgin = callback;
    }

    public BeaconCallback(OutputStream out) {
        this.orgin = new SimpleCallback(out);
    }

    @Override
    public final OutputStream openOutput() throws IOException {
        if (this.orgin != null) {
            return this.orgin.openOutput();
        }
        return null;
    }

    @Override
    public final void onStart() {
        if (this.orgin != null) {
            this.orgin.onStart();
        }
    }

    @Override
    public final void onSuccess() {
        try {
            if (this.orgin != null) {
                this.orgin.onSuccess();
            }
        } finally {
            this.whenFinish();
        }
    }

    @Override
    public final void onFailed(String code, String message) {
        try {
            if (this.orgin != null) {
                this.orgin.onFailed(code, message);
            }
        } finally {
            this.whenFinish();
        }
    }

    @Override
    public final void onException(Exception ex) {
        try {
            if (this.orgin != null) {
                this.orgin.onException(ex);
            }
        } finally {
            this.whenFinish();
        }
    }

    protected final void whenFinish() {
        if (this.orgin != null) {
            this.orgin.onFinally();
        }
        this.onFinally();
    }

    @Override
    public void onFinally() {
    }

}
