package com.mjcshuai.service.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * AI 顾问服务接口定义
 * 定义了 AI 的角色设定和交互方式
 */
public interface ConsultantService {
    
    // 这里定义了 AI 的人设。你可以随时修改这句话来改变 AI 的语气。
    @SystemMessage("你是一个学生管理系统的智能助手，请用专业、耐心且略带幽默的语气回答学生和老师关于系统使用、课程查询等方面的问题。")
    String chat(@MemoryId String memoryId, @UserMessage String message);
}