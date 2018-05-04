package com.jelly.eoss.web.admin;

import com.jelly.eoss.dao.BaseDao;
import com.jelly.eoss.model.AdminMenu;
import com.jelly.eoss.service.MenuManagerService;
import com.jelly.eoss.util.ComUtil;
import com.jelly.eoss.util.Const;
import com.jelly.eoss.util.DateUtil;
import com.jelly.eoss.util.Pager;
import com.jelly.eoss.web.BaseAction;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/system/resource")
public class ResourceAction extends BaseAction {
	private static final Logger log = LoggerFactory.getLogger(ResourceAction.class);

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private MenuManagerService menuManagerService;
	
	@RequestMapping(value = "/toList")
	public ModelAndView toList(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Integer page = ServletRequestUtils.getIntParameter(request, "page", 1);
		
		Map<String, String> param = this.getRequestMap(request);
		param.put("leaf", "1");
		RowBounds rb = new RowBounds((page -1) * Const.PAGE_SIZE, Const.PAGE_SIZE);
		
		Integer totalRow = this.baseDao.mySelectOne("_EXT.Menu_QueryMenuPage_Count", param);
		List<Map<String, Object>> dataList = this.baseDao.getSqlSessionTemplate().selectList("_EXT.Menu_QueryMenuPage", param, rb);
		
		Pager pager = new Pager(page.intValue(), Const.PAGE_SIZE, totalRow.intValue());
		pager.setData(dataList);
		
		request.setAttribute("pager", pager);
		this.resetAllRequestParams(request);
		return new ModelAndView("/system/resourceList.jsp");
	}

	@RequestMapping(value = "/toAdd")
	public ModelAndView toAdd(HttpServletRequest request, HttpServletResponse response, AdminMenu menu) throws Exception{
		return new ModelAndView("/system/resourceAdd.jsp");
	}
	
	@RequestMapping(value = "/add")
	public ModelAndView add(HttpServletRequest request, HttpServletResponse response, AdminMenu menu) throws Exception{
		int id = ComUtil.QueryNextID("id", "menu");
		menu.setUrl(Const.BASE_PATH + menu.getUrl());
		menu.setId(id);
		menu.setLeaf(1);
		menu.setPath(menu.getPath() + "#" + id);
		menu.setCreateDatetime(DateUtil.GetCurrentDateTime(true));
		this.baseDao.myInsert(AdminMenu.Insert, menu);
		log.debug(menu.getTarget());
		return new ModelAndView("/system/resource/toList");
	}
	
	@RequestMapping(value = "/delete")
	public void delete(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String id = request.getParameter("id");
//		Log.Debug("id:" + id);
		this.baseDao.myDelete(AdminMenu.DeleteByPk, id);
		response.getWriter().write("y");
	}
	
	@RequestMapping(value = "/toUpdate")
	public ModelAndView toUpdate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String id = request.getParameter("id");
		AdminMenu menu = this.baseDao.mySelectOne(AdminMenu.SelectByPk, id);
		
		//装饰zTreeNode
		Map<String, String> pm = new HashMap<String, String>();
		pm.put("onlyParent", "yes");
		pm.put("openAll", "yes");
		pm.put("checkedIds", String.valueOf(menu.getPid()));
		pm.put("rootNocheck", "yes");
		String zTreeNodeJson = this.menuManagerService.queryMenuSub(pm);
		
		request.setAttribute("menu", menu);
		request.setAttribute("zTreeNodeJson", zTreeNodeJson);
		return new ModelAndView("/system/resourceUpdate.jsp");
	}
	
	@RequestMapping(value = "/update")
	public ModelAndView update(HttpServletRequest request, HttpServletResponse response, AdminMenu menu) throws ServletException, IOException{
		AdminMenu m = this.baseDao.mySelectOne(AdminMenu.SelectByPk, menu.getId());
		m.setName(menu.getName());
		m.setTarget(menu.getTarget());
		m.setLev(menu.getLev());
		m.setPath(menu.getPath());
		m.setUrl(menu.getUrl());
		m.setPid(menu.getPid());
		this.baseDao.myUpdate(AdminMenu.Update, m);
		return new ModelAndView("/system/resource/toList");
	}
	
	//getter and setter
	public BaseDao getBaseDao() {
		return baseDao;
	}

	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}

	public MenuManagerService getMenuManagerService() {
		return menuManagerService;
	}

	public void setMenuManagerService(MenuManagerService menuManagerService) {
		this.menuManagerService = menuManagerService;
	}
}