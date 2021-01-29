package com.telecom.ecloudframework.sys.rest.ofd.custom.agent.callback;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import com.telecom.ecloudframework.sys.rest.ofd.custom.agent.ConvertCallback;
import com.telecom.ecloudframework.sys.rest.ofd.custom.agent.TransferCallback;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleCallback implements ConvertCallback,TransferCallback, Closeable {

    private static Logger log = LoggerFactory.getLogger(SimpleCallback.class);

    private OutputStream out;
    
    private StringBuilder sb = new StringBuilder();
    
    private String path;
    
    public SimpleCallback(File out,String path) throws IOException {
        this(FileUtils.openOutputStream(out));
        this.path=path;
    }

    public SimpleCallback(OutputStream out) {
        this.out = out;
    }

    @Override
    public OutputStream openOutput() throws IOException {
        return this.out;
    }

    @Override
    public void onStart() {
        log.debug("Task start");
    }

    @Override
    public void onSuccess() {
        log.debug("Task success");
        
    }

    @Override
    public void onFailed(String code, String message) {
        log.debug("Task failed, code = {}, message = {}", code, message);
    }

    @Override
    public void onException(Exception ex) {
        log.error("Task exception", ex);
    }

    @Override
    public void onFinally() {
        IOUtils.closeQuietly(this);
        
    }

    @Override
    public void close() throws IOException {
        IOUtils.closeQuietly(this.out);
    }
    
    long l;
    static String format =" %s %sms";
	@Override
	public void onPackStart() {
		l=System.currentTimeMillis();
		
	}

	@Override
	public void onPackEnd() {
		long pt = System.currentTimeMillis()-l;
		sb.append(String.format(format,"Pack",pt));
		
	}

	@Override
	public void onUploadStart() {
		l=System.currentTimeMillis();
	}

	@Override
	public void onUploadEnd() {
		long pt = System.currentTimeMillis()-l;
		sb.append(String.format(format,"Upload",pt));
	}

	@Override
	public void onTicket(String ticket) {
		sb.append(" ticket=".concat(ticket));
	}

	@Override
	public void onDownloadStart() {
		l=System.currentTimeMillis();
	}

	@Override
	public void onDownloadEnd() {
		long pt = System.currentTimeMillis()-l;
		sb.append(String.format(format,"Download",pt));
	}
	
	public String getMag(){
		return this.sb.toString();
	}
	
}
