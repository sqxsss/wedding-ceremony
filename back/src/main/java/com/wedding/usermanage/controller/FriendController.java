package com.wedding.usermanage.controller;

import com.wedding.model.ReturnMessage;
import com.wedding.usermanage.service.FriendService;
import com.wedding.usermanage.vo.FriendVO;
import com.wedding.usermanage.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RequestMapping("/friend")
@Controller
public class FriendController {

    @Autowired
    private FriendService friendService;

    @ResponseBody
    @RequestMapping(value = "/getFriendList",method = RequestMethod.GET)
    public ReturnMessage getFriendList(HttpServletRequest httpServletRequest){
        HttpSession session=httpServletRequest.getSession(false);
        if(session!=null){
            LoginVO loginVO=(LoginVO) session.getAttribute("userinfo");
            return new ReturnMessage(true,friendService.getFriendList(loginVO.getUserid()));
        }
        String[] message=new String[1];
        message[0]="尚未登录";
        return new ReturnMessage(false,message);
    }

    @ResponseBody
    @RequestMapping(value = "/changeRemark",method = RequestMethod.POST)
    public ReturnMessage changeRemark(@RequestBody FriendVO friendVO, HttpServletRequest httpServletRequest){
        HttpSession session=httpServletRequest.getSession(false);
        if(session!=null){
            LoginVO loginVO=(LoginVO) session.getAttribute("userinfo");
            return new ReturnMessage(true,friendService.changeRemark(loginVO.getUserid(),friendVO));
        }
        return new ReturnMessage(false,"尚未登录");
    }

    @ResponseBody
    @RequestMapping(value = "/deleteFriend",method = RequestMethod.POST)
    public ReturnMessage deleteFriend(@RequestBody FriendVO friendVO, HttpServletRequest httpServletRequest){
        HttpSession session=httpServletRequest.getSession(false);
        if(session!=null){
            LoginVO loginVO=(LoginVO) session.getAttribute("userinfo");
            return new ReturnMessage(true,friendService.deleteFriend(loginVO.getUserid(),friendVO));
        }
        return new ReturnMessage(false,"尚未登录");
    }



}
