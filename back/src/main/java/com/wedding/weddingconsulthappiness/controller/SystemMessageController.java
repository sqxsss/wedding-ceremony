package com.wedding.weddingconsulthappiness.controller;

import com.wedding.model.po.System_message;
import com.wedding.weddingconsulthappiness.service.SystemMessageService;
import com.wedding.weddingconsulthappiness.vo.MessageState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/systemmessage")
public class SystemMessageController {
    @Autowired
    SystemMessageService ts;
    @ResponseBody
    @RequestMapping(value="/add",method = RequestMethod.POST)
    public int addsm(@RequestBody System_message sm, HttpServletRequest resquest){
        sm.setId(ts.getId());
        return ts.addSystemMessage(sm);
    }
    @ResponseBody
    @RequestMapping(value = "/get",method = RequestMethod.POST)
    public List<System_message> getsm(@RequestBody Integer number){
        List<System_message>list=ts.selectAll();
        ArrayList<System_message>result=new ArrayList<>();
        for(System_message sm:list){
            if(sm.getSenderId()+sm.getReceiverId()==number){
                result.add(sm);
            }
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/read",method = RequestMethod.POST)
    public int read(@RequestBody String str){
        int from=Integer.parseInt(str.split("_")[0]);
        int to=Integer.parseInt(str.split("_")[1]);
        List<System_message>list=ts.selectAll();
        for(System_message s:list){
            if(s.getSenderId()==from&&s.getReceiverId()==to){
                s.setState(1);
                ts.updateByPrimaryKey(s);
            }
        }
        return 1;
    }

    @ResponseBody
    @RequestMapping(value = "/getAll",method = RequestMethod.GET)
    public List<System_message> getAll(){
        return ts.selectAll();
    }
    @ResponseBody
    @RequestMapping(value = "/getState",method = RequestMethod.GET)
    public List<MessageState> getState(){
        List<System_message>list=ts.selectAll();
        HashMap<Integer,MessageState>map=new HashMap<>();
        for(System_message s:list){
            int id=s.getSenderId();
            if(!map.containsKey(id)){
                MessageState m=new MessageState(id,"","无新消息");
                map.put(id,m);
            }
            if(s.getState()==0){
                MessageState m=new MessageState(id,"","有新消息");
                map.put(id,m);
            }
        }
        List<MessageState>result=new ArrayList<MessageState>();
        for(Integer key:map.keySet()){
            result.add(map.get(key));
        }
        return result;
    }
    @ResponseBody
    @RequestMapping(value = "/update",method = RequestMethod.GET)
    public int update(){
        System_message sm=new System_message();
        sm.setId(0);
        sm.setSenderId(1);
        sm.setReceiverId(3);
        sm.setContent("hhh");
        sm.setState(1);
        return ts.updateByPrimaryKey(sm);
    }
    @ResponseBody
    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    public int deletesm(){
        return ts.deleteByPrimaryKey(0);
    }


}
