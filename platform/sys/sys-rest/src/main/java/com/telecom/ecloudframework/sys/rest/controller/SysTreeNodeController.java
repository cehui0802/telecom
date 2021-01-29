package com.telecom.ecloudframework.sys.rest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.telecom.ecloudframework.base.core.util.AppUtil;
import com.telecom.ecloudframework.base.rest.tree.DeleteAuth;
import com.telecom.ecloudframework.sys.api.model.SysNodeOrderParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

import com.telecom.ecloudframework.base.api.aop.annotion.CatchErr;
import com.telecom.ecloudframework.base.api.exception.BusinessMessage;
import com.telecom.ecloudframework.base.api.response.impl.ResultMsg;
import com.telecom.ecloudframework.base.core.id.IdUtil;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.base.rest.ControllerTools;
import com.telecom.ecloudframework.base.rest.util.RequestUtil;
import com.telecom.ecloudframework.sys.core.manager.SysTreeManager;
import com.telecom.ecloudframework.sys.core.manager.SysTreeNodeManager;
import com.telecom.ecloudframework.sys.core.model.SysTreeNode;

/**
 * <pre>
 * 描述：sysTreeNode层的controller
 * 作者:aschs
 * 邮箱:aschs@qq.com
 * 日期:下午5:11:06
 * 版权:summer
 * </pre>
 */
@RestController
@RequestMapping("/sys/sysTreeNode/")
public class SysTreeNodeController extends ControllerTools {
	@Autowired
	SysTreeManager sysTreeManager;
	@Autowired
	SysTreeNodeManager sysTreeNodeManager;

	@Autowired
	private DeleteAuth deleteAuth;

	/**
	 * <pre>
	 * sysTreeEdit.html的saveNode后端
	 * 保存树节点
	 * </pre>
	 * 
	 * @param sysTreeNode
	 * @throws Exception
	 */
	@RequestMapping("save")
	@CatchErr(write2response = true, value = "保存系统树节点失败")
	public ResultMsg<SysTreeNode> save(@RequestBody SysTreeNode sysTreeNode) {
		if (StringUtil.isEmpty(sysTreeNode.getId())) {
			if (sysTreeNodeManager.getByTreeIdAndKey(sysTreeNode.getTreeId(), sysTreeNode.getKey()) != null) {
				throw new BusinessMessage("当前节点别名已存在");
			}
			sysTreeNode.setId(IdUtil.getSuid());
			handleNewSysTreeNode(sysTreeNode);
			sysTreeNodeManager.create(sysTreeNode);
		} else {
			sysTreeNodeManager.update(sysTreeNode);
		}
		return getSuccessResult(sysTreeNode, "保存系统树节点成功");
	}

	/**
	 * <pre>
	 * 调整分类树节点顺序
	 * </pre>
	 *
	 * @param param
	 * @throws Exception
	 */
	@PostMapping("changeOrder")
	@CatchErr(write2response = true, value = "更改分类树节点顺序失败")
	public ResultMsg changeOrder(@RequestBody List<SysNodeOrderParam> param) {

		param.forEach(org -> {
			if (StringUtil.isEmpty(org.getId())) {
				throw new BusinessMessage("树节点ID不能为空");
			}else {
			  SysTreeNode sysTreeNode= sysTreeNodeManager.get(org.getId());
			  if (null==sysTreeNode){
				  throw new BusinessMessage("该节点不存在");
			   }else{
				  sysTreeNodeManager.chageOrder(org);
			  }
			}
		});
		return getSuccessResult("分类树节点顺序调整成功");
	}

	/**
	 * <pre>
	 * 获取sysTreeNode的后端
	 * </pre>
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getNodes")
	@ResponseBody
	public List<SysTreeNode> getNodes(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<SysTreeNode> nodes = new ArrayList<>();
		String treeId = RequestUtil.getString(request, "treeId");
		String key = RequestUtil.getString(request, "nodeKey");
		String treeKey = RequestUtil.getString(request, "treeKey");
		String rootName = RequestUtil.getString(request, "rootName");// 前端需要一个新的根节点（名字）
		if (StringUtil.isNotEmpty(treeKey) && StringUtil.isEmpty(treeId)) {
			treeId = sysTreeManager.getByKey(treeKey).getId();
		}

		if (StringUtil.isNotEmpty(key) && StringUtil.isNotEmpty(treeId)) {
			SysTreeNode node = sysTreeNodeManager.getByTreeIdAndKey(treeId, key);
			nodes = sysTreeNodeManager.getStartWithPath(node.getPath());
		} else if (StringUtil.isNotEmpty(treeId)) {
			nodes = sysTreeNodeManager.getByTreeId(treeId);
		}

		if (StringUtil.isNotEmpty(rootName)) {
			nodes.forEach(node -> {
				if ("0".equals(node.getParentId())) {
					node.setParentId("-1");
				}
			});
			SysTreeNode rootRoot = new SysTreeNode();
			rootRoot.setKey("");
			rootRoot.setId("-1");
			rootRoot.setParentId("0");
			rootRoot.setName(rootName);
			nodes.add(rootRoot);
		}

		return nodes;
	}

	/**
	 * 根据父节点Id获取所有子节点
	 */
	@RequestMapping(value = "getChildNodes",method = {RequestMethod.GET})
	public ResultMsg<List<SysTreeNode>> getChildNodes(@RequestParam(value = "nodeId",required = true) String parentNodeId){
		List<SysTreeNode> childNodes = sysTreeNodeManager.getByParentId(parentNodeId);
		return getSuccessResult(childNodes);
	}
	/**
	 * 根据父节点key获取所有子节点
	 */
	@RequestMapping(value = "getChildNodesByKey",method = {RequestMethod.GET})
	public ResultMsg<List<SysTreeNode>> getChildNodesByKey(@RequestParam(value = "nodeKey",required = true) String nodeKey){
		List<SysTreeNode> childNodes = sysTreeNodeManager.getByParentKey(nodeKey);
		return getSuccessResult(childNodes);
	}


	/**
	 * <pre>
	 * 批量删除
	 * </pre>
	 *
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("remove")
	@CatchErr(write2response = true, value = "删除系统树节点失败")
	public ResultMsg<String> remove(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ApplicationContext ctx = AppUtil.getApplicaitonContext();
		Map<String,DeleteAuth> beanMap = ctx.getBeansOfType(DeleteAuth.class);
		String[] aryIds = RequestUtil.getStringAryByStr(request, "id");
		boolean deleteFlag = true;
		for (String id : aryIds) {
			//1.获取该分类的所有子分类ID
			SysTreeNode node = sysTreeNodeManager.get(id);
			List<SysTreeNode> nodes = sysTreeNodeManager.getStartWithPath(node.getPath() + "%");
			//2.判断子分类是否有删除权限
			for(SysTreeNode sysTreeNode: nodes){
				for(String key:beanMap.keySet()){
					DeleteAuth bean = (DeleteAuth)ctx.getBean(key);
					Boolean is_delete = bean.deleteAuth(sysTreeNode.getId());
					if(!is_delete){
						deleteFlag = false;
						break;
					}
				}
				if(!deleteFlag){
					break;
				}
			}
			//删除
			if(deleteFlag){
				sysTreeNodeManager.removeByPath(node.getPath() + "%");
				return getSuccessResult("删除系统树节点成功");
			}
		}
		return getSuccessResult("分类节点业务不为空，不能删除");
	}

	/**
	 * <pre>
	 * 处理一下新节点的数据
	 * </pre>
	 *
	 * @param sysTreeNode
	 */
	private void handleNewSysTreeNode(SysTreeNode sysTreeNode) {
		// 新增时处理一下path
		if (StringUtil.isNotEmpty(sysTreeNode.getPath())) {
			sysTreeNode.setPath(sysTreeNode.getPath() + sysTreeNode.getId() + ".");
		} else {
			sysTreeNode.setPath(sysTreeNode.getId() + ".");
		}

		// 新增处理sn
		// 获取同级节点
		List<SysTreeNode> nodes = sysTreeNodeManager.getByParentId(sysTreeNode.getParentId());
		sysTreeNode.setSn(nodes.size() + 1);
	}
}
