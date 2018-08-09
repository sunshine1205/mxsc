package cn.mxsc.service.impl;

import cn.mxsc.common.Const;
import cn.mxsc.common.ServerResponse;
import cn.mxsc.common.TokenCache;
import cn.mxsc.dao.UserMapper;
import cn.mxsc.pojo.User;
import cn.mxsc.service.IUserService;
import cn.mxsc.util.MD5Util;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/***
 * IUserService接口的实现
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public ServerResponse<User> login(String username, String password) {

        int count = userMapper.checkUsername(username);
        if(count == 0){
           return  ServerResponse.createByErrorMessage("用户名不存在！");
        }
        //密码MD5加密
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5Password);
        if(user == null){
            return  ServerResponse.createByErrorMessage("密码错误！");
        }
        user.setPassword(StringUtils.EMPTY);
        return  ServerResponse.createBySuccess("登录成功",user);
    }

    @Override
    public ServerResponse<String> register(User user) {
        ServerResponse<String> response = this.checkValid(user.getUsername(),Const.USERNAME);
        if(!response.isSuccess()){
            return  response;
        }
        response = this.checkValid(user.getEmail(),Const.EMAIL);
        if(!response.isSuccess()){
            return  response;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int count =  userMapper.insert(user);
        if(count == 0){
            return  ServerResponse.createByErrorMessage("注册失败！");
        }

        return ServerResponse.createBySuccessMessage("注册成功！");
    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {

        if(StringUtils.isNotBlank(type)){
           if(Const.USERNAME.equals(type)){
               int count = userMapper.checkUsername(str);
               if(count > 0){
                   return  ServerResponse.createByErrorMessage("该用户名已存在！");
               }
           }
           if(Const.EMAIL.equals(type)){
               int count  = userMapper.checkEmail(str);
               if(count > 0){
                   return  ServerResponse.createByErrorMessage("该Email已注册！");
               }
           }
        }else{
            return  ServerResponse.createByErrorMessage("参数错误！");
        }
        return ServerResponse.createBySuccessMessage("校验成功!");
    }

    @Override
    public ServerResponse<String> selectQuestion(String username) {
        ServerResponse<String> valid = this.checkValid(username, Const.USERNAME);
        if(valid.isSuccess()){
            //说明该用户不存在
            return  ServerResponse.createByErrorMessage("该用户不存在！");
        }
        String question = userMapper.queryQuestionByUsername(username);
        if(!StringUtils.isNotBlank(question)){
           return  ServerResponse.createByErrorMessage("找回密码问题为空！");
        }
        return ServerResponse.createBySuccess(question);
    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int count  = userMapper.checkAnswer(username,question,answer);
        if(count > 0){
            //说明该用户问题及密码正确
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return  ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题答案错误");
    }

    @Override
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        if(StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误，该操作需要token");
        }
        ServerResponse<String> valid = this.checkValid(username, Const.USERNAME);
        if(valid.isSuccess()){
            //说明该用户不存在
            return  ServerResponse.createByErrorMessage("该用户不存在！");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(StringUtils.isBlank(token)){
            return  ServerResponse.createByErrorMessage("该token无效或已过期");
        }
        if(StringUtils.equals(token,forgetToken)){
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int count = userMapper.updatePasswordByUsername(username,md5Password);
            if(count > 0){
                return  ServerResponse.createBySuccessMessage("修改密码成功");
            }
        }else{
            return ServerResponse.createByErrorMessage("token错误，请重新获取重置密码的token");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    @Override
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
        //防止横向越权
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if(resultCount == 0){
          return  ServerResponse.createByErrorMessage("旧密码输入错误，请重新输入！");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        resultCount = userMapper.updateByPrimaryKeySelective(user);
        if(resultCount > 0){
            return  ServerResponse.createBySuccessMessage("重置密码成功");
        }
        return ServerResponse.createByErrorMessage("重置密码失败");
    }

    @Override
    public ServerResponse<User> updateInformation(User user) {
        int resultCount = userMapper.checkEmailByUserId(user.getId(),user.getEmail());
        if(resultCount > 0){
             return  ServerResponse.createByErrorMessage("该邮箱已被使用");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        resultCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(resultCount > 0){
            return ServerResponse.createBySuccess("个人信息修改成功",updateUser);
        }
        return  ServerResponse.createByErrorMessage("个人信息修改失败");
    }

    @Override
    public ServerResponse<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    @Override
    public ServerResponse checkAdminRole(User user) {
        if(user!=null && user.getRole().intValue()==Const.Role.ROLE_ADMIN) {
            return ServerResponse.createBySuccess();
        }
            return ServerResponse.createByError();

    }
}
