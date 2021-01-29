package com.telecom.ecloudframework.base.core.ext;

import com.telecom.ecloudframework.base.core.model.EnumExtraData;

import java.util.List;

/**
 * 枚举扩展数据加载
 *
 * @author wacxhs
 */
public interface IEnumExtraDataLoader {

    /**
     * 标记，用于区分具体枚举下的扩展
     *
     * @return 扩展枚举类
     */
    Class<?> tag();

    /**
     * 加载扩展枚举数据
     *
     * @return 扩展枚举数据
     */
    List<EnumExtraData> load();
}
