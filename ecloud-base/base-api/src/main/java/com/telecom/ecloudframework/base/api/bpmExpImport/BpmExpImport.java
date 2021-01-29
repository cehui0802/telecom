package com.telecom.ecloudframework.base.api.bpmExpImport;

public interface BpmExpImport {
    void bpmExport(String defIds, String filePath) throws Exception;

    void bpmImport(String filePath) throws Exception;

    String checkImport(String filePath)throws Exception;
}
