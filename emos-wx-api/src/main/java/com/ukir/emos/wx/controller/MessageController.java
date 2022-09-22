package com.ukir.emos.wx.controller;

import com.ukir.emos.wx.common.util.Result;
import com.ukir.emos.wx.config.shiro.JwtUtil;
import com.ukir.emos.wx.controller.form.DeleteMessageRefByIdForm;
import com.ukir.emos.wx.controller.form.SearchMessageByIdForm;
import com.ukir.emos.wx.controller.form.SearchMessageByPageForm;
import com.ukir.emos.wx.controller.form.UpdateUnreadMessage;
import com.ukir.emos.wx.service.MessageService;
import com.ukir.emos.wx.task.MessageTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

/**
 * 消息模块Web层
 **/
@RestController
@RequestMapping("/message")
@Api("消息模块接口")
public class MessageController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageTask messageTask;

    @PostMapping("/searchMessageByPage")
    @ApiOperation("分页查询消息")
    public Result searchMessageByPage(@Valid @RequestBody SearchMessageByPageForm form, @RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);
        int page = form.getPage();
        int length = form.getLength();

        long start = (page - 1) * length; //查询分页的记录 第一页：1 - 1 * 40 = 40条
        List<HashMap> list = messageService.searchMessageByPage(userId, start, length);
        return Result.ok().put("result", list);
    }


    /**
     * 根据Id查询消息
     *
     * @param form
     * @return
     */
    @PostMapping("/searchMessageById")
    @ApiOperation("根据Id查询消息")
    public Result searchMessageById(@Valid @RequestBody SearchMessageByIdForm form) {
        HashMap map = messageService.searchMessageById(form.getId());
        return Result.ok().put("result", map);
    }


    @PostMapping("/updateUnreadMessage")
    @ApiOperation("更新未读消息")
    public Result updateUnreadMessage(@Valid @RequestBody UpdateUnreadMessage form) {
        long rows = messageService.updateUnreadMessage(form.getId());
        return Result.ok().put("result", rows == 1 ? true : false);
    }

    @PostMapping("/deleteMessageRefById")
    @ApiOperation("删除消息")
    public Result deleteMessageRefById(@Valid @RequestBody DeleteMessageRefByIdForm form) {
        long rows = messageService.deleteMessageRefById(form.getId());
        return Result.ok().put("result", rows == 1 ? true : false);
    }

    @GetMapping("/refreshMessage")
    @ApiOperation("刷新用户的消息")
    public Result refreshMessage(@RequestHeader("token") String token){
        int userId = jwtUtil.getUserId(token);
        //异步接收消息
        int i = messageTask.receiveAsync(userId + "");
        //查询接收了多少条消息
        long lastCount = messageService.searchLastcount(userId);
        //查询未读消息
        long unreadCount = messageService.searchUnreadcount(userId);
        return Result.ok().put("lastRows",lastCount).put("unreadRows",unreadCount);

    }




}
