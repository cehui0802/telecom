package com.telecom.ecloudframework.sys.core.manager.impl;

import com.telecom.ecloudframework.base.api.exception.BusinessMessage;
import com.telecom.ecloudframework.base.core.id.IdUtil;
import com.telecom.ecloudframework.base.manager.impl.BaseManager;
import com.telecom.ecloudframework.sys.api.model.calendar.Schedule;
import com.telecom.ecloudframework.sys.api.model.calendar.ScheduleHistory;
import com.telecom.ecloudframework.sys.api.model.calendar.ScheduleParticipant;
import com.telecom.ecloudframework.sys.core.dao.ScheduleDao;
import com.telecom.ecloudframework.sys.core.dao.ScheduleHistoryDao;
import com.telecom.ecloudframework.sys.core.dao.ScheduleParticipantDao;
import com.telecom.ecloudframework.sys.util.ContextUtil;
import com.telecom.ecloudframework.sys.core.manager.ScheduleManager;
import com.telecom.ecloudframework.sys.core.model.ParticipantScheduleDO;
import cn.hutool.core.lang.Assert;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * <pre> 
 * 描述：日程 处理实现类
 * 作者:linkai
 * 邮箱:linkai@ddjf.com.cn
 * 日期:2018-02-01 17:45:09
 * </pre>
 */
@Service("scheduleManager")
public class ScheduleManagerImpl extends BaseManager<String, Schedule> implements ScheduleManager{
	@Resource
    ScheduleDao scheduleDao;
	@Resource
    ScheduleParticipantDao scheduleParticipantDao;

	@Resource
	private ScheduleHistoryDao scheduleHistoryDao;

//	@Autowired
//	private ScheduleMQ scheduleMQ;
	 
	/**
	 * 创建实体包含子表实体
	 */
	@Override
    public void create(Schedule schedule){
    	super.create(schedule);
    	String pk=schedule.getId();
    	
    	//--添加参与者--
//    	if((ISchedule.TYPE_SHARE.equals(schedule.getType()) || ISchedule.TYPE_ILKA.equals(schedule.getType())) && schedule.getParticipantNames() != null && !schedule.getParticipantNames().isEmpty()) {
//    		String[] participants = schedule.getParticipantNames().split(",");
//    		for(int i = 0; i < participants.length; i++) {
//    			ScheduleParticipant scheduleParticipant = new ScheduleParticipant();
//    			//??????????????id设置？？？？？？？？？？？？
//    			scheduleParticipant.setScheduleId(pk);
//    			scheduleParticipant.setParticipantorName(participants[i]);
//    			scheduleParticipant.setRateProgress(0);
//    			scheduleParticipant.setCreateTime(schedule.getCreateTime());
//    			scheduleParticipant.setActualStartTime(schedule.getCreateTime());
//    			scheduleParticipant.setUpdateTime(schedule.getCreateTime());
//    			scheduleParticipantDao.create(scheduleParticipant);
//    		}
//    	}
    	if((Schedule.TYPE_SHARE.equals(schedule.getType()) || Schedule.TYPE_ILKA.equals(schedule.getType()))){
    		List<ScheduleParticipant> scheduleParticipantList= schedule.getScheduleParticipantList();
			//判断是否包含创建者
			boolean flag = false;
			for(ScheduleParticipant p : scheduleParticipantList){
				if(p.getParticipantor().equals(schedule.getOwner())){
					flag = true;
					break;
				}
			}
			if(!flag){
				//共享类型和合作类型设置创建者就是参与者
				ScheduleParticipant participant = new ScheduleParticipant();
				participant.setParticipantor(schedule.getOwner());
				participant.setParticipantorName(schedule.getOwnerName());
				scheduleParticipantList.add(participant);
			}
        	for(ScheduleParticipant scheduleParticipant:scheduleParticipantList){
        		scheduleParticipant.setId(IdUtil.getSuid());
        		scheduleParticipant.setScheduleId(pk);
        		scheduleParticipant.setRateProgress(0);
    			scheduleParticipant.setCreateTime(schedule.getCreateTime());
    			scheduleParticipant.setActualStartTime(schedule.getActualStartTime());
    			scheduleParticipant.setUpdateTime(schedule.getUpdateTime());
        		scheduleParticipantDao.create(scheduleParticipant);
        	}
    	}
    	//发送预约日程消息
		//scheduleMQ.scheduleNotice(schedule.getId(),ScheduleMQ.SCHEDULEORDERTAG);
    	
    }
	
	/**
	 * 删除记录包含子表记录
	 */
	@Override
	public void remove(String entityId){
		super.remove(entityId);
    	scheduleParticipantDao.delByMainId(entityId);
	}
	
	/**
	 * 批量删除包含子表记录
	 */
	@Override
	public void removeByIds(String[] entityIds){
		for(String id:entityIds){
			this.remove(id);
		}
	}
    
	/**
	 * 获取实体
	 */
    @Override
	public Schedule get(String entityId){
    	Assert.notBlank("日程 ID 不能为空！");
    	Schedule schedule=super.get(entityId);
    	List<ScheduleParticipant> scheduleParticipantList=scheduleParticipantDao.getScheduleParticipantList(entityId);
    	if(schedule != null){
			schedule.setScheduleParticipantList(scheduleParticipantList);
		}
    	return schedule;
    }
    
    /**
     * 更新实体同时更新子表记录
     */
    @Override
	public void update(Schedule schedule){
    	//1.判断是否有权限更新主日程，若有权限更新则即可以更新主日程也可以更新参与日程
		String userId = ContextUtil.getCurrentUserId();
		String userName = ContextUtil.getCurrentUser().getFullname();
		int mainRate = 0;
		//处理主日程rate_progress
		if(schedule.getRateProgress() >= 100){
			schedule.setRateProgress(100);
			schedule.setCompleteTime(new Date());
		}
		List<ScheduleParticipant> participants= schedule.getScheduleParticipantList();
		if(userId.equals(schedule.getOwner())){
			if(Schedule.TYPE_ILKA.equals(schedule.getType())){
				//flag标记是否全部完成
				boolean flag = true;
				for(ScheduleParticipant participant:participants){
					if(participant.getRateProgress() == 100){
						participant.setCompleteTime(new Date());
					}else{
						flag = false;
					}
					scheduleParticipantDao.update(participant);
					mainRate += (int)Math.round((participant.getRateProgress()>100?100:participant.getRateProgress()) * (1.0 / participants.size()));
				}
				if(flag){
					mainRate = 100;
					schedule.setCompleteTime(new Date());
				}
				schedule.setRateProgress(mainRate);
			}
    	}else {
			//2.否则只能更新自己和主日程的比例和时间
			if(Schedule.TYPE_ILKA.equals(schedule.getType())){
				for(ScheduleParticipant participant:participants){
					if(participant.getParticipantor().equals(userId)){
						if(participant.getRateProgress() > 100){
							participant.setRateProgress(100);
						}
						if(participant.getRateProgress() == 100){
							participant.setCompleteTime(new Date());
						}
						scheduleParticipantDao.update(participant);
						break;
					}
				}
				List<ScheduleParticipant> participantList = scheduleParticipantDao.getScheduleParticipantList(schedule.getId());
				for(ScheduleParticipant participant:participantList){
					mainRate += (int)Math.round(participant.getRateProgress() * (1.0 / participantList.size()));
				}
				//设置主日程
				if(mainRate == 100){
					schedule.setCompleteTime(new Date());
				}
				schedule.setRateProgress(mainRate);
			}
		}
		if(Schedule.TYPE_SHARE.equals(schedule.getType())){
			//共享模式，所有人的比例都和主日程一致
			for(ScheduleParticipant participant:participants){
				if(schedule.getRateProgress() == 100){
					participant.setCompleteTime(new Date());
				}
				participant.setRateProgress(schedule.getRateProgress());
				scheduleParticipantDao.update(participant);
			}
		}
		schedule.setUpdateTime(new Date());
		schedule.setSubmitter(userId);
		schedule.setSubmitNamer(userName);
		scheduleDao.update(schedule);

		//更新日程历史列表
		updateScheduleHistory(schedule);
		//发送变更消息
		if(schedule.getRateProgress() == 100){
			//如果主日程100，发送完成消息
			//scheduleMQ.scheduleNotice(schedule.getId(),ScheduleMQ.SCHEDULECOMPLETETAG);
		}
    }
    
    /**
     * 获取一段时间内的日程
     */
	@Override
	public List<Schedule> getByPeriodAndOwner(Date start, Date end, String ownerName, String owner) {
		return scheduleDao.getByPeriodAndOwner(start, end, ownerName, owner);
	}
	
	@Override
	public void saveSchedule(Schedule schedule) {
		if(schedule.getStartTime().compareTo(schedule.getEndTime()) > 0) {
			throw new BusinessMessage("日程开始时间不能大于结束时间");
		}
		create(schedule);
	}
	@Override
	public List<Schedule> getByPeriodAndSource(Date start, Date end, String source) {
		return scheduleDao.getByPeriodAndSource(start, end, source);
	}
	@Override
	public void deleteByBizId(String bizId) {
		scheduleDao.deleteByBizId(bizId);
	}
	@Override
	public void dragUpdate(Schedule schedule) {
		scheduleDao.dragUpdate(schedule);
	}
	@Override
	public void updateSchedule(String biz_id, Date start, Date end, String ownerAccount) {
		scheduleDao.updateSchedule(biz_id, start, end, ownerAccount);
	}
	@Override
	public List<Schedule> getByBizId(String biz_id) {
		return scheduleDao.getByBizId(biz_id);
	}
	@Override
	public void updateOnlySchedule(Schedule schedule) {
		scheduleDao.updateOnlySchedule(schedule);
	}

	@Override
	public List<ParticipantScheduleDO> getParticipantEvents(Date startDate, Date endDate, String name, String id) {
		Map<String,Object> map = new HashMap<>();
		map.put("startTime", startDate);
		map.put("endTime", endDate);
		map.put("participantName", name);
		map.put("participant", id);
		
		return scheduleDao.getParticipantEvents(map);
	}


	public List<ScheduleHistory> queryHistory(String scheduleId){
		return scheduleHistoryDao.queryHistory(scheduleId);
	}

	/**
	 * 创建日程历史列表
	 * @param schedule
	 */
	public void updateScheduleHistory(Schedule schedule){
		String userId = ContextUtil.getCurrentUserId();
		String userName = ContextUtil.getCurrentUser().getFullname();
		Date currentTime = new Date();
		ScheduleHistory scheduleHistory = new ScheduleHistory();
		scheduleHistory.setId(IdUtil.getSuid());
		scheduleHistory.setScheduleId(schedule.getId());
		scheduleHistory.setRateProgress(schedule.getRateProgress());
		scheduleHistory.setSubmit(userId);
		scheduleHistory.setSubmitName(userName);
		scheduleHistory.setSubmitTime(currentTime);
		scheduleHistoryDao.create(scheduleHistory);
	}
    
	
}
