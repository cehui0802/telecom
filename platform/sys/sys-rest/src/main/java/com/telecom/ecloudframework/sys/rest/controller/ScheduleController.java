package com.telecom.ecloudframework.sys.rest.controller;

import com.telecom.ecloudframework.base.api.aop.annotion.CatchErr;
import com.telecom.ecloudframework.base.api.constant.BaseStatusCode;
import com.telecom.ecloudframework.base.api.exception.BusinessException;
import com.telecom.ecloudframework.base.api.exception.BusinessMessage;
import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.api.response.impl.ResultMsg;
import com.telecom.ecloudframework.base.core.id.IdUtil;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.base.dao.CommonDao;
import com.telecom.ecloudframework.base.db.model.page.PageResult;
import com.telecom.ecloudframework.base.db.model.query.DefaultQueryFilter;
import com.telecom.ecloudframework.base.rest.BaseController;
import com.telecom.ecloudframework.sys.api.model.calendar.Schedule;
import com.telecom.ecloudframework.sys.api.model.calendar.ScheduleHistory;
import com.telecom.ecloudframework.sys.api.model.calendar.ScheduleParticipant;
import com.telecom.ecloudframework.sys.core.dao.ScheduleDao;
import com.telecom.ecloudframework.sys.core.dao.ScheduleParticipantDao;
import com.telecom.ecloudframework.sys.util.ContextUtil;
import com.telecom.ecloudframework.sys.core.manager.ScheduleManager;
import com.telecom.ecloudframework.sys.core.model.ParticipantScheduleDO;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


/**
 * 描述：日程 控制器类
 */
@RestController
@RequestMapping("/calendar/schedule")
public class ScheduleController extends BaseController<Schedule>{
	@Resource
	ScheduleManager scheduleManager;
	@Resource
    ScheduleParticipantDao scheduleParticipantDao;
	@Resource
    ScheduleDao scheduleDao;
	@Autowired
	CommonDao<?> commonDao;
//	@Autowired
//	private ScheduleMQ scheduleMQ;


	@CatchErr("删除日程失败")
	@RequestMapping(value = "remove",method = RequestMethod.POST)
	public ResultMsg<String> remove(@RequestParam(value = "id",required = true) String id) throws Exception{
		String currentUserId = ContextUtil.getCurrentUserId();
		//获取删除日程的所属者
		Schedule schedule = scheduleDao.get(id);
		if(schedule.getOwner().equals(currentUserId)){
//			scheduleMQ.scheduleNotice(id,ScheduleMQ.SCHEDULECANCELTAG);
//			scheduleDao.remove(id);
		}else{
			//抛出没有删除权限
//			return getSuccessResult("删除失败，没有删除权限");
			throw new BusinessMessage("删除失败，没有删除权限");
		}
		return  getSuccessResult("删除成功");
	}

	@CatchErr("获取日程列表失败")
	@RequestMapping(value = "listJson",method = {RequestMethod.POST})
	public PageResult<Schedule> listJson(HttpServletRequest request,HttpServletResponse response){
		//获取所属者为当前用户的日程 //获取参与者为当前用户的日程
		String currentId = ContextUtil.getCurrentUserId();
		String sql = " (owner_ = '"+currentId+"' or id_ in (select schedule_id_ from c_schedule_participant where participantor_ = '"+currentId+"' ))";
		//通过传参来判断用户使用的那个检索，分为使用title_~owner_name_^VLK检索或者其他
		if(request.getParameter("participantors") != null && !"".equals(request.getParameter("participantors")) && (request.getParameter("title_~owner_name_^VLK") == null || "".equals(request.getParameter("title_~owner_name_^VLK")))){
			//高级检索
			//添加多个参与者
			String[] participantors = request.getParameter("participantors").split(",");
			String participantorSql = " ";
			for(String participantor :participantors){
				participantorSql = participantorSql + " participantor_='" +participantor + "' or ";
			}
			if(participantors.length > 0){
				participantorSql = participantorSql.substring(0,participantorSql.length() - 3);
				sql = sql +  " and id_ in (select schedule_id_ from c_schedule_participant where "+participantorSql+" ) ";
			}
		}
		if("true".equals(request.getParameter("isComplete"))){
			sql = sql + " and rate_progress_ = 100 ";
		}else if("false".equals(request.getParameter("isComplete"))){
			sql = sql + " and rate_progress_ != 100 ";
		}
		QueryFilter queryFilter = (DefaultQueryFilter)getQueryFilter(request);
		queryFilter.addParamsFilter("defaultWhere",sql);
		Page<Schedule> pageList = (Page<Schedule>) scheduleManager.query(queryFilter);
		return new PageResult(pageList);
	}

	@CatchErr("获取日程历史列表失败")
	@RequestMapping(value = "listJsonHistory",method = {RequestMethod.POST})
	public PageResult<Schedule> listJsonHistory(@RequestParam(value = "scheduleId",required = true) String scheduleId){

		List<ScheduleHistory> historyList = scheduleManager.queryHistory(scheduleId);
		return new PageResult(historyList);

	}

	
	/**
	 * @return 
	 * 保存日程信息
	 * @param schedule
	 * @throws Exception 
	 * void
	 * @exception 
	 */
	@CatchErr("对日程操作失败")
	@RequestMapping("save")
	@Override
	public ResultMsg<String> save(@RequestBody Schedule schedule) throws Exception{
		String resultMsg=null;
		String id=schedule.getId();
		if(schedule.getEndTime() != null && schedule.getStartTime() != null && schedule.getEndTime().getTime() <= schedule.getStartTime().getTime()) {
			throw new BusinessMessage("结束日期不能小于开始日期");
		}
		if(schedule.getActualStartTime() != null && schedule.getCompleteTime() != null && schedule.getCompleteTime().getTime() <= schedule.getActualStartTime().getTime()) {
			throw new BusinessMessage("完成时间不能小于实际开始日期");
		}
		if(StringUtil.isEmpty(id)){
			String ownerName = ContextUtil.getCurrentUser().getFullname();
			String owner = ContextUtil.getCurrentUserId();
			schedule.setOwner(owner);
			schedule.setOwnerName(ownerName);
			schedule.setId(IdUtil.getSuid());
			schedule.setRateProgress(0);//--创建时进度为0--
			schedule.setCreateTime(new Date());
			schedule.setUpdateTime(new Date());
			schedule.setUpdateBy(owner);
			schedule.setActualStartTime(schedule.getStartTime());
			schedule.setCreateBy(ownerName);
			scheduleManager.saveSchedule(schedule);
			resultMsg="添加日程成功";
		}else{

			scheduleManager.update(schedule);
			resultMsg="更新日程成功";
		}

		return  getSuccessResult(resultMsg);
		 
	}

	/**
	 * 批量更新日程
	 * @param batchSchedule [{"scheduleId":"","completeRate":""},{},{}]
	 * @return
	 */
	@RequestMapping(value = "batchUpdateSchedule",method = {RequestMethod.POST})
	@CatchErr("批量更新日程失败")
	public ResultMsg<String> batchUpdateSchedule(@RequestBody List<Map<String,Object>> batchSchedule){
		String currentId = ContextUtil.getCurrentUserId();
		for(Map<String,Object> updateSchedule : batchSchedule){
			String scheduleId = (String) updateSchedule.get("scheduleId");
			int completeRate = (int)updateSchedule.get("completeRate");
			//1.组装完成日程信息
			Schedule schedule = scheduleManager.get(scheduleId);
			//2.组装参与者
			List<ScheduleParticipant> participantList = scheduleParticipantDao.getScheduleParticipantList(scheduleId);
			//合作类型修改自己完成度
			if(Schedule.TYPE_ILKA.equals(schedule.getType())){
				for(ScheduleParticipant participant : participantList){
					if(currentId.equals(participant.getParticipantor())){
						participant.setRateProgress(completeRate);
					}
				}
			}else{
				//个人类型或共享类型，修改主日程
				schedule.setRateProgress(completeRate);
			}
			schedule.setScheduleParticipantList(participantList);
			scheduleManager.update(schedule);
		}
		return getSuccessResult("批量更新成功！");
	}
	 
	
	
	/**
	 * 从数据库中获取日程数据(所属者)
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getEvents")
	@CatchErr("日程获取失败")
	public List<Schedule> getEvents(HttpServletRequest request,HttpServletResponse response) throws Exception{
		List<Schedule> list = new ArrayList<>();
		String name = ContextUtil.getCurrentUser().getFullname();
		String id = ContextUtil.getCurrentUserId();
		String start = request.getParameter("start");
		String end = request.getParameter("end");
		
		Calendar startDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		startDate.setTimeInMillis(Long.valueOf(start));
		endDate.setTimeInMillis(Long.valueOf(end));
		
		try {
			list = scheduleManager.getByPeriodAndOwner(startDate.getTime(), endDate.getTime(), name, id);
		} catch (Exception e) {
			throw new BusinessException("呵呵",BaseStatusCode.NO_ACCESS,e);
		}
		 
		return list;
	}
	/**
	 * 从数据库中获取日程数据(参与者)
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getParticipantEvents")
	@CatchErr("日程获取失败")
	public List<ParticipantScheduleDO> getParticipantEvents(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String name = ContextUtil.getCurrentUser().getFullname();
		String id = ContextUtil.getCurrentUserId();
		
		String start = request.getParameter("start");
		String end = request.getParameter("end");
		
		Calendar startDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		startDate.setTimeInMillis(Long.valueOf(start));
		endDate.setTimeInMillis(Long.valueOf(end));
		
		List<ParticipantScheduleDO> list = scheduleManager.getParticipantEvents(startDate.getTime(),endDate.getTime(), name, id);

		//添加合作类型（所属者为本人）中本人已经完成但是总进度未完成的日程

		return list;
	}
	
	
	/**
	 * 日程表上选中保存
	 * @return 
	 */
	@RequestMapping("saveOwn")
	@CatchErr("日程保存失败")
	public ResultMsg<String> saveOwn(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String title = request.getParameter("title");
		String start = request.getParameter("start");
		
		String end = request.getParameter("end");
		Calendar startDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		
		startDate.setTimeInMillis(Long.valueOf(start)-8*60*60*1000);//格林乔治时间减8小时调整为中国时间
		endDate.setTimeInMillis(Long.valueOf(end)-8*60*60*1000);
		
		Schedule schedule = new Schedule();
		schedule.setTitle(title);
		schedule.setStartTime(startDate.getTime());
		schedule.setEndTime(endDate.getTime());
		schedule.setRateProgress(0);
		schedule.setCreateTime(new Date());
		scheduleManager.create(schedule);
		//发送保存消息
		//scheduleMQ.scheduleNotice(schedule.getId(),ScheduleMQ.SCHEDULEORDERTAG);
		return getSuccessResult("日程保存成功");
	}
	/**
	 * 日程表拖拽更新
	 * @param request
	 * @param response
	 * @return 
	 * @throws Exception
	 */
	@RequestMapping("dragUpdate")
	@CatchErr("日程拖拽失败")
	public ResultMsg<String> dargUpdate(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String id = request.getParameter("id");
		String start = request.getParameter("start");
		String end = request.getParameter("end");
		String owner = ContextUtil.getCurrentUserId();
		//检测是否具有拖拽权限
		Schedule s1 = scheduleManager.get(id);
		if(s1 == null || !s1.getOwner().equals(owner)){
			return getWarnResult("当前用户没有拖拽权限！");
		}

		Calendar startDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		startDate.setTimeInMillis(Long.valueOf(start)-8*60*60*1000);//格林乔治时间减8小时调整为中国时间
		endDate.setTimeInMillis(Long.valueOf(end)-8*60*60*1000);//格林乔治时间减8小时调整为中国时间

		Schedule schedule = new Schedule();
		schedule.setId(id);
		schedule.setStartTime(startDate.getTime());
		schedule.setEndTime(endDate.getTime());
		scheduleManager.dragUpdate(schedule);
		//发送变更消息
		//scheduleMQ.scheduleNotice(id,ScheduleMQ.SCHEDULEORDERTAG);
		return getSuccessResult("日程拖拽成功");
	}
	
	/**
	 * 完成日程任务  ??? wtf 
	 * @param request
	 * @param response
	 * @return 
	 * @throws Exception
	 */
	@CatchErr("完成日程失败")
	@RequestMapping("completeTask")
	public ResultMsg<String> completeTask(HttpServletRequest request,HttpServletResponse response,@RequestBody Schedule schedule2) throws Exception{
		String participantId = request.getParameter("id");
		//String id = request.getParameter("id");
		String type = schedule2.getType();
		String comment = schedule2.getRemark();
		Date actualStart = schedule2.getActualStartTime();
		String subName = ContextUtil.getCurrentUser().getFullname();
		String subId = ContextUtil.getCurrentUserId();
		Date date = new Date();
		//int rate = schedule2.getRateProgress();
		//--single情况下是所属
		if(Schedule.TYPE_SINGLE.equals(type)) {
			if(schedule2.getCompleteTime() != null && schedule2.getActualStartTime() != null && schedule2.getCompleteTime().getTime() <= schedule2.getActualStartTime().getTime()) {
				throw new BusinessMessage("完成时间不能小于实际开始日期");
			}
			if(schedule2.getRateProgress() >= 100 ) {
				schedule2.setRateProgress(100);
				if(schedule2.getCompleteTime() == null) {
					schedule2.setCompleteTime(new Date());
				}
			}
		}
		//--share情况下只有参与者才能完成，故id为参与者
		if(Schedule.TYPE_SHARE.equals(type)) {
			List<ScheduleParticipant> list = schedule2.getScheduleParticipantList();
			int rate = 0;
			Date completeTime = null;
			for(ScheduleParticipant participant : list) {
				if(participant.getId().equals(participantId)) {
					if(participant.getCompleteTime() != null && participant.getActualStartTime() != null && participant.getCompleteTime().getTime() <= participant.getActualStartTime().getTime()) {
						throw new BusinessMessage("完成时间不能小于实际开始日期");
					}
					rate = participant.getRateProgress();
					participant.setUpdateTime(new Date());
					completeTime = participant.getCompleteTime();
					break;
				}
			}
			// 共同任务。一人修改、所有人同步？？？
			for(ScheduleParticipant participant : list) {
				if(rate >= 100) {
					participant.setRateProgress(100);
					participant.setCompleteTime(completeTime == null ? date : completeTime);
				} else {
					participant.setRateProgress(rate);
				}
			}
			if(rate >= 100) {
				schedule2.setRateProgress(100);
				schedule2.setCompleteTime(completeTime == null ? date : completeTime);
			} else {
				schedule2.setRateProgress(rate);
			}

		}
		if(Schedule.TYPE_ILKA.equals(type)) {
			List<ScheduleParticipant> list = schedule2.getScheduleParticipantList();
			int mainRate = 0;
			Date completeDate = null;
			for(ScheduleParticipant participant : list) {
				mainRate += participant.getRateProgress();
				if(participant.getId().equals(participantId)) {
					if(participant.getCompleteTime() != null && participant.getActualStartTime() != null && participant.getCompleteTime().getTime() <= participant.getActualStartTime().getTime()) {
						throw new BusinessMessage("完成时间不能小于实际开始日期");
					}
					completeDate = participant.getCompleteTime();
					if(participant.getRateProgress() >= 100 && completeDate == null) {
						participant.setCompleteTime(new Date());
					}
				}
			}
			int rate = (int)Math.round(mainRate * (1.0 / list.size()));
			if(rate >= 100) {
				schedule2.setRateProgress(100);
				if(completeDate != null) {
					schedule2.setCompleteTime(completeDate);
				} else {
					schedule2.setCompleteTime(new Date());
				}
			} else {
				schedule2.setRateProgress(rate);
			}
		}
		schedule2.setSubmitNamer(subName);
		schedule2.setSubmitter(subId);
		schedule2.setUpdateTime(new Date());
		scheduleManager.update(schedule2);

		return getSuccessResult("操作成功");
	}
	
//	/**
//	 * 完成日程
//	 * @param request
//	 * @param response
//	 * @throws Exception
//	 */
////	@RequestMapping(value="completee",produces = "application/json;utf-8")
//	@RequestMapping("complete")
//	public void complete(HttpServletRequest request,HttpServletResponse response) throws Exception{
//		String resultMsg=null;
//		try {
//			String id = request.getParameter("id");
//			String type = request.getParameter("type");
////			String comment = request.getParameter("comment");
//			String subName = ContextUtil.getCurrentUser().getFullname();
//			String subId = ContextUtil.getCurrentUserId();
//			Date date = new Date();
//			//--single情况下是所属者
//			if(ISchedule.TYPE_SINGLE.equals(type)) {
//				Schedule schedule = new Schedule();
//				schedule.setId(id);
//				schedule.setRateProgress(100);
//				schedule.setCompleteTime(date);
//				schedule.setUpdateTime(date);
//				schedule.setSubmitNamer(subName);
//				schedule.setSubmitter(subId);
//				scheduleManager.updateOnlySchedule(schedule);
//			}
//			//--share情况下只有参与者才能完成，故id为参与者
//			if(ISchedule.TYPE_SHARE.equals(type) || ISchedule.TYPE_ILKA.equals(type)) {
//				ScheduleParticipant scheduleParticipant = scheduleParticipantDao.get(id);
//				String mainId = scheduleParticipant.getScheduleId();
//				Schedule schedule = scheduleManager.get(mainId);
//				schedule.setRateProgress(100);
//				schedule.setCompleteTime(date);
//				schedule.setUpdateTime(date);
//				schedule.setSubmitNamer(subName);
//				schedule.setSubmitter(subId);
//				scheduleManager.updateOnlySchedule(schedule);
//				List<ScheduleParticipant> list = scheduleParticipantDao.getByMainId(mainId);
//				for(ScheduleParticipant participant : list) {
//					participant.setRateProgress(100);
//					participant.setCompleteTime(date);
//					participant.setUpdateBy(subId);
//					participant.setUpdateTime(date);
////					if(id.equals(participant.getId())) {
////						participant.setSubmitComment(comment);
////					}
//					scheduleParticipantDao.update(participant);
//				}
//				
//			}
////			if(ISchedule.TYPE_ILKA.equals(type)) {
////				ScheduleParticipant scheduleParticipant = scheduleParticipantDao.get(id);
////				scheduleParticipant.setRateProgress(100);
////				scheduleParticipant.setCompleteTime(date);
////				scheduleParticipant.setUpdateBy(subId);
////				scheduleParticipant.setUpdateTime(date);
////				scheduleParticipant.setSubmitComment(comment);
////				scheduleParticipantDao.update(scheduleParticipant);
////				String mainId = scheduleParticipant.getScheduleId();
////				Schedule schedule = scheduleManager.get(mainId);
////				String participantNames = schedule.getParticipantNames();
////				String[] names = participantNames.split(",");
////				int numberOfParticipants = names.length;
////				int mainProgress = (int) Math.round(100.0 * (1.0 / numberOfParticipants));
////				if(mainProgress + schedule.getRateProgress() >= 100) {
////					schedule.setRateProgress(100);
////					schedule.setCompleteTime(date);
////				} else {
////					schedule.setRateProgress(mainProgress + schedule.getRateProgress());
////				}
////				
////				schedule.setUpdateTime(date);
////				schedule.setSubmitNamer(subName);
////				schedule.setSubmitter(subId);
////				scheduleManager.updateOnlySchedule(schedule);
////			}
//			
//		} catch (Exception e) {
//			resultMsg="完成日程失败";
//			writeResultMessage(response.getWriter(),resultMsg,e.getMessage(),ResultMessage.FAIL);
//		}
//	}
//	/**
//	 * 完成参与者日程进度
//	 * @param request
//	 * @param response
//	 * @throws Exception
//	 */
//	@RequestMapping("completePart")
//	public void completePart(HttpServletRequest request,HttpServletResponse response) throws Exception{
//		String resultMsg=null;
//		try {
//			String id = request.getParameter("id");
//			Schedule schedule = new Schedule();
//			Date date = new Date();
//			schedule.setId(id);
//			schedule.setRateProgress(100);
//			schedule.setCompleteTime(date);
//			schedule.setUpdateTime(date);
//			scheduleManager.updateOnlySchedule(schedule);
//		} catch (Exception e) {
//			resultMsg="完成日程失败";
//			writeResultMessage(response.getWriter(),resultMsg,e.getMessage(),ResultMessage.FAIL);
//		}
//	}
	/**
	 * 删除参与者,只删除参与者信息，未做任务比例重分配。
	 * @param request
	 * @param response
	 * @return 
	 * @throws Exception
	 */
	@CatchErr("删除参与者失败")
	@RequestMapping("itemDelete")
	public ResultMsg<String> itemDelete(HttpServletRequest request,HttpServletResponse response) throws Exception{
			String id = request.getParameter("id");
			ScheduleParticipant scheduleParticipant = scheduleParticipantDao.get(id);
			String mainId = scheduleParticipant.getScheduleId();
			String participantName = scheduleParticipant.getParticipantorName();
			Schedule schedule = scheduleManager.get(mainId);
			String[] participantNames = schedule.getParticipantNames().split(",");
			//List<String> list = Arrays.asList(participantNames);
			//list.remove(paticipantName);
			List<String> newNames = new ArrayList<>();
			for(int i = 0; i < participantNames.length; i++) {
				if(!participantName.equals(participantNames[i])) {
					newNames.add(participantNames[i]);
				}
			}
			String newNamesString = "";
			for(int i = 0; i < newNames.size(); i++) {
				newNamesString += newNames.get(i);
				if(i < newNames.size() -1) {
					newNamesString += ",";
				}
			}
			schedule.setParticipantNames(newNamesString);
			schedule.setUpdateTime(new Date());
			schedule.setUpdateBy(ContextUtil.getCurrentUserId());
			scheduleParticipantDao.remove(id);
			scheduleManager.updateOnlySchedule(schedule);
			//scheduleParticipantDao.remove(id);
		 
			return getSuccessResult("删除参与者成功");
	}
//	/**
//	 * 添加参与者
//	 * @param request
//	 * @param response
//	 * @param scheduleParticipant
//	 * @throws Exception
//	 */
//	@RequestMapping("saveParticipant")
//	public void saveParticipant(HttpServletRequest request,HttpServletResponse response,@RequestBody ScheduleParticipant scheduleParticipant) throws Exception{
//		String resultMsg=null;
//		String scheduleId = request.getParameter("scheduleId");
//		String id=scheduleParticipant.getId();
//		try {
//			if(StringUtil.isEmpty(id)){
//				scheduleParticipant.setId(UniqueIdUtil.getSuid());
//				if(scheduleId != null) {
//					scheduleParticipant.setScheduleId(scheduleId);
//				}
//				scheduleParticipant.setRateProgress(0);//--创建时进度为0--
//				scheduleParticipantDao.create(scheduleParticipant);
//				resultMsg="添加参与者成功";
//			}
//			writeResultMessage(response.getWriter(),resultMsg,ResultMessage.SUCCESS);
//		} catch (Exception e) {
//			resultMsg="添加参与者失败";
//			writeResultMessage(response.getWriter(),resultMsg,e.getMessage(),ResultMessage.FAIL);
//		}
//	}

	/**
	 * id为日程id
	 * isMainSchedule 判断合作形式是子任务完成还是总任务完成，默认为子任务完成
	 */
	@CatchErr("设置完成失败")
	@RequestMapping("setComplete")
	public ResultMsg<String> completeTask(@RequestParam(value = "id",required = true) String id,
										  @RequestParam(value = "isMainSchedule",required = false,defaultValue = "false") boolean isMainSchedule){

		String userId = ContextUtil.getCurrentUserId();
		String userName = ContextUtil.getCurrentUser().getFullname();
		Date currentTime = new Date();
		int mainRate = 0;
		//根据传入的ID获取该主日程信息
		Schedule schedule = scheduleDao.get(id);
		//获取参与者信息
		List<ScheduleParticipant> participants = scheduleParticipantDao.getScheduleParticipantList(schedule.getId());
		//合作方式且设置完成自己任务，只需修改自己的和总共的rate_progress
		if(!isMainSchedule && Schedule.TYPE_ILKA.equals(schedule.getType())){
			boolean flag = true; //标记是否全部完成
			for(ScheduleParticipant participant :participants){
				if(participant.getParticipantor().equals(userId)){
					participant.setRateProgress(100);
					participant.setCompleteTime(currentTime);
					scheduleParticipantDao.update(participant);
				}
				if(participant.getRateProgress() != 100){
					flag = false;
				}
				//计算主日程rate_progress
				mainRate += (int)Math.round(participant.getRateProgress() * (1.0 / participants.size()));
			}
			if(flag){
				mainRate = 100;
			}
		}else{
			for(ScheduleParticipant participant :participants){
				participant.setRateProgress(100);
				participant.setCompleteTime(currentTime);
				scheduleParticipantDao.update(participant);
			}
			//设置主日程的rate_progress
			mainRate = 100;
		}
		//3.设置主日程
		schedule.setRateProgress(mainRate);
		schedule.setCompleteTime(currentTime);
		schedule.setUpdateTime(currentTime);
		schedule.setSubmitter(userId);
		schedule.setSubmitNamer(userName);
		scheduleDao.update(schedule);
		//4.设置日程历史列表
		scheduleManager.updateScheduleHistory(schedule);

		//如果主日程100，发送完成消息
		if(mainRate == 100){
		//	scheduleMQ.scheduleNotice(id,ScheduleMQ.SCHEDULECOMPLETETAG);
		}
		return getSuccessResult("设置完成成功");
	}

	/**
	 * 日程历史列表
	 * @return
	 */



	@Override
	protected String getModelDesc() {
	return "日程";
}

}
