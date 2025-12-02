package com.mjcshuai.service.ai;

import com.mjcshuai.constant.AiProperties;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

/**
 * AI 助手管理器 (单例模式)
 * @author:ChatGPT
 * 基于langchain4j框架，调用了OpenAI的gpt-4o-mini模型
 * 负责初始化连接 OpenAI 服务，并组装 Memory 和 Service
 */
@Slf4j
public class AiAssistantManager {

    // 持有全局唯一的服务实例
    private static ConsultantService instance;

    // 私有构造方法，防止外部直接 new
    private AiAssistantManager() {}

    /**
     * 获取 AI 服务实例的唯一入口
     * 第一次调用时会进行初始化
     */
    public static synchronized ConsultantService getService() {
        if (instance == null) {
            instance = createService();
        }
        return instance;
    }

    // 具体的初始化逻辑
    private static ConsultantService createService() {
        log.info("正在初始化 AI 服务...");
        
        // 1. 配置大模型连接信息
        // ----------------------------------------------------------
        // 这里使用的是 LangChain4j 提供的公共演示 Key，速度受限。
        // 实际使用请替换为你自己的 OpenAI API Key 或其他模型服务商的配置。
        // ----------------------------------------------------------
        ChatLanguageModel model = OpenAiChatModel.builder()
                .baseUrl(AiProperties.BASE_URL)
                .apiKey(AiProperties.OPENAI_API_KEY)
                .modelName(AiProperties.MODEL_NAME)
                .timeout(Duration.ofSeconds(60)) // 设置超时时间
                .logRequests(true)  // 在控制台打印请求日志
                .logResponses(true) // 在控制台打印响应日志
                .build();

        // 2. 配置上下文记忆
        // 为每个不同的 memoryId (用户) 创建一个独立的记忆窗口，记住最近 20 条交互
        ChatMemoryProvider memoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(20)
                .build();

        // 3. 组装并创建代理服务对象
        return AiServices.builder(ConsultantService.class)
                .chatLanguageModel(model)
                .chatMemoryProvider(memoryProvider)
                .build();
    }
}