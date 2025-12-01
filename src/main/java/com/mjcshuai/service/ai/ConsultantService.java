package com.mjcshuai.service.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * AI 顾问服务接口定义
 */
public interface ConsultantService {

    // 读取文件用于定义ai的系统信息
    @SystemMessage(fromResource = "prompts/system.txt")
    String chat(@MemoryId String memoryId, @UserMessage String message);
}