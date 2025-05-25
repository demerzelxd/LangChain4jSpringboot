package com.me.newlangchain4j.service;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Service;

@Service
public class ToolsService {

    // 告诉AI 什么对话才调用这个方法
    @Tool("某个地区某个性别某个名字的数量")
    public Integer changshaNameCount(
            @P("地区")
            String location,
            @P("性别")
            String gender,
            // 告诉AI 需要提取的信息
            @P("姓名")
            String name) {
        // todo...
        System.out.println(location);
        System.out.println(gender);
        System.out.println(name);

        // 结果
        return 10;
    }

    @Tool("退票")
    public String cancelBooking(
            @P("订单编号")
            String orderId,
            // 告诉AI 需要提取的信息
            @P("客户姓名")
            String name) {
        // todo... 业务方法，退票数据库操作
        System.out.println(name);
        System.out.println(orderId);

        // 结果
        return "退票成功";
    }
}