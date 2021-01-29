package com.telecom.ecloudframework.sys.rest.ofd.custom.agent;

public interface TransferCallback {

    void onPackStart();

    void onPackEnd();

    void onUploadStart();

    void onUploadEnd();

    void onTicket(String ticket);

    void onDownloadStart();

    void onDownloadEnd();

}
