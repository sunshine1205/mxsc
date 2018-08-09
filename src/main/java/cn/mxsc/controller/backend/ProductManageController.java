package cn.mxsc.controller.backend;

import cn.mxsc.common.Const;
import cn.mxsc.common.ResponseCode;
import cn.mxsc.common.ServerResponse;
import cn.mxsc.pojo.Product;
import cn.mxsc.pojo.User;
import cn.mxsc.service.IProductService;
import cn.mxsc.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/manage/product")
public class ProductManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @RequestMapping("productSave.do")
    public ServerResponse productSave(HttpSession session, Product product){
        User user  = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"你还未登录，请先登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //是管理员进行商品添加
            return iProductService.saveOrUpdateProduct(product);
        }else{
            return ServerResponse.createByErrorMessage("你没有权限访问该项，需要管理员权限");
        }
    }
    @RequestMapping("setSaleStatus.do")
    public ServerResponse setSaleStatus(HttpSession session,Integer productId,Integer status){
        User user  = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"你还未登录，请先登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //是管理员进行销售状态修改
            return iProductService.setSaleStatus(productId,status);
        }else{
            return ServerResponse.createByErrorMessage("你没有权限访问该项，需要管理员权限");
        }
    }

    @RequestMapping("getDetail.do")
    public ServerResponse getDetail(HttpSession session,Integer productId){
        User user  = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"你还未登录，请先登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //业务
            return iProductService.manageProductDetail(productId);
        }else{
            return ServerResponse.createByErrorMessage("你没有权限访问该项，需要管理员权限");
        }
    }

    @RequestMapping("getList.do")
    public ServerResponse getList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
        User user  = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"你还未登录，请先登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //业务
            return iProductService.getProductList(pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("你没有权限访问该项，需要管理员权限");
        }
    }

    @RequestMapping("searchProduct.do")
    public ServerResponse searchProduct(HttpSession session,String productName,Integer productId,@RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
        User user  = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"你还未登录，请先登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //业务
            return iProductService.searchProductByNameAndId(productName,productId,pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("你没有权限访问该项，需要管理员权限");
        }
    }
}
