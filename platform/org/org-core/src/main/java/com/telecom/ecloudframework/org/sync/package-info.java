/**
 *  组用户同步，orm 包下为映射bean，请根据差异配置 @JSONField(name = "xxx"),达到适配其他系统组织的需求， <br/>
 *  请务必保证 userId,orgId 每次同步的唯一与不变性，同步会同步【用户】、【组织】、【用户组织关系】。
 *  同步后新增岗位、用户、用户岗位,再次同步不受影响
 *  如果新增组织、用户组织关系，下次同步会被重置
 * date: 2018年8月24日18:11:58 <br/>
 *
 * @author jeff
 * @version 
 */
package com.telecom.ecloudframework.org.sync;

