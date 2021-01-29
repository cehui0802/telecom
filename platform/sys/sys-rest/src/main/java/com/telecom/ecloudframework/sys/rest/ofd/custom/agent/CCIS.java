package com.telecom.ecloudframework.sys.rest.ofd.custom.agent;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CCIS extends FilterInputStream {

    private boolean closed;

    protected CCIS(InputStream in) {
        super(in);
    }

    @Override
    public final void close() throws IOException {
        if (this.closed) {
            return;
        }
        this.closed = true;
        try {
            super.close();
        } finally {
            this.onClose();
        }
    }

    public void onClose() {
    }
}
