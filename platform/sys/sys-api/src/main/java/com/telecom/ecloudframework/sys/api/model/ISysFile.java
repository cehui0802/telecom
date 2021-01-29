package com.telecom.ecloudframework.sys.api.model;

public interface ISysFile {

    String getId();
    /**
     * 附件名
     */
    String getName();

    /**
     * <pre>
     * 这附件用的是上传器
     * 具体类型可以看 IUploader 的实现类
     * </pre>
     */
    String getUploader();

    /**
     * <pre>
     * 路径，这个路径能从上传器中获取到对应的附件内容
     * 所以也不一定是路径，根据不同上传器会有不同值
     * </pre>
     */
    String getPath();

    /**
     * 备注，可以用于关联查询
     */
    String getRemark();

    /**
     * 流程实例ID
     */
    String getInstId();

    /**
     * 创建人名称
     */
    String getCreator();
}
