package cn.mxsc.controller.portal;

import cn.mxsc.common.Const;
import cn.mxsc.common.ResponseCode;
import cn.mxsc.common.ServerResponse;
import cn.mxsc.pojo.User;
import cn.mxsc.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/***
 * User控制器
 */
@RestController
@RequestMapping("/user/")
public class UserController {
    @Autowired
    private IUserService iUserService;
    @RequestMapping(value = {"login.do"},method = RequestMethod.POST)
    public ServerResponse<User> login(String username, String password, HttpSession session){
        ServerResponse<User> response = iUserService.login(username, password);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return  response;
    }

    @RequestMapping(value = {"logout.do"},method = RequestMethod.POST)
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return  ServerResponse.createBySuccess();
    }

    @RequestMapping(value = {"register.do"},method = RequestMethod.POST)
    public ServerResponse<String> register(User user){
        return  iUserService.register(user);
    }

    @RequestMapping(value = {"checkValid.do"},method = RequestMethod.POST)
    public ServerResponse<String> checkValid(String str,String type){
       return  iUserService.checkValid(str,type);
    }

    @RequestMapping(value = {"getUserInfo.do"},method = RequestMethod.POST)
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user != null){
            return  ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息！");
    }

    @RequestMapping(value = {"forgetGetQuestion.do"},method =RequestMethod.POST)
    public ServerResponse<String> forgetGetQuestion(String username){
        return  iUserService.selectQuestion(username);
    }

    @RequestMapping(value = {"forgetCheckQuestion.do"},method = RequestMethod.POST)
    public ServerResponse<String> forgetCheckQuestion(String username,String question,String answer){
       return iUserService.checkAnswer(username,question,answer);
    }

    @RequestMapping(value = {"forgetRestPassword.do"},method = RequestMethod.POST)
    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
       return iUserService.forgetResetPassword(username,passwordNew,forgetToken);
    }

    @RequestMapping(value = {"resetPassword.do"},method = RequestMethod.POST)
    public ServerResponse<String> resetPassword(HttpSession session,String passwordOld,String passwordNew){

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return  ServerResponse.createByErrorMessage("该用户未登录，请登录");
        }
       return  iUserService.resetPassword(passwordOld,passwordNew,user);

    }

    @RequestMapping(value = {"updateInformation.do"},method =RequestMethod.POST)
    public ServerResponse<User> updateInformation(HttpSession session,User user){

        User currUser = (User) session.getAttribute(Const.CURRENT_USER);
        if(currUser == null){
            return  ServerResponse.createByErrorMessage("该用户未登录，请登录");
        }
        user.setId(currUser.getId());
        user.setUsername(currUser.getUsername());
        ServerResponse<User> response = iUserService.updateInformation(user);
        if(response.isSuccess()){
            response.getData().setUsername(currUser.getUsername());
            session.setAttribute(Const.USERNAME,response.getData());
        }
        return response;
    }

    @RequestMapping(value = "getInformation.do",method = RequestMethod.POST)
    public ServerResponse<User> getInformation(HttpSession session){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录,需要强制登录status=10");
        }
        return iUserService.getInformation(currentUser.getId());
    }


}
