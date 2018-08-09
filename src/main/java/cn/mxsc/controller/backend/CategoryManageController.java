package cn.mxsc.controller.backend;

import cn.mxsc.common.Const;
import cn.mxsc.common.ResponseCode;
import cn.mxsc.common.ServerResponse;
import cn.mxsc.pojo.User;
import cn.mxsc.service.ICategoryService;
import cn.mxsc.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/manage/category")
public class CategoryManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;
    @RequestMapping("addCategory.do")
    public ServerResponse addCategory(HttpSession session,String categoryName,@RequestParam(value = "parentId",defaultValue = "0") Integer parentId){
        User user  = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"你还未登录，请先登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //是管理员
            return iCategoryService.addCategory(categoryName,parentId);
        }else{
            return ServerResponse.createByErrorMessage("你没有权限访问该项，需要管理员权限");
        }
    }
    @RequestMapping("updateCategoryName.do")
    public ServerResponse updateCategoryName(HttpSession session,Integer categoryId,String categoryName){
        User user  = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"你还未登录，请先登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //是管理员
            return iCategoryService.updateCategoryName(categoryId,categoryName);
        }else{
            return ServerResponse.createByErrorMessage("你没有权限访问该项，需要管理员权限");
        }
    }
    @RequestMapping("getChildrenParallelCategory.do")
    public ServerResponse getChildrenParallelCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user  = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"你还未登录，请先登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //查询Category信息，不递归，保持平级
            return iCategoryService.getChildrenParallelCategory(categoryId);
        }else{
            return ServerResponse.createByErrorMessage("你没有权限访问该项，需要管理员权限");
        }
    }
    @RequestMapping("getCategoryAndDeepChildrenCategory.do")
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user  = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"你还未登录，请先登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //查询Category信息，递归子节点
            return iCategoryService.selectCategoryAndChildrenById(categoryId);
        }else{
            return ServerResponse.createByErrorMessage("你没有权限访问该项，需要管理员权限");
        }
    }




}
