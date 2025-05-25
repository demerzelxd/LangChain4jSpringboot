package com.me.newlangchain4j.config;

import com.me.newlangchain4j.service.ToolsService;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    public interface Assistant {
        String chat(String message);

        TokenStream streamingChat(String message);

        @SystemMessage("""
                        您是“Tuling”航空公司的客户聊天支持代理。请以友好、乐于助人且愉快的方式来回复。
                                您正在通过在线聊天系统与客户互动。 
                                在提供有关预订或取消预订的信息之前，您必须始终从用户处获取以下信息：订单编号、客户姓名。
                                请讲中文。
                今天的日期是 {{current_date}}.
                        """)
        TokenStream streamingChatForOrder(@UserMessage String message, @V("current_date") String currentDate);
    }

    /**
     * 通过chatId记忆隔离
     */
    public interface AssistantUnique {
        String chat(@MemoryId int memoryId, @UserMessage String message);

        TokenStream streamingChat(@MemoryId int memoryId, @UserMessage String message);
    }

    @Bean
    public Assistant assistant(ChatLanguageModel chatLanguageModel,
                               StreamingChatLanguageModel streamingChatLanguageModel,
                               ToolsService toolsService) {
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

        return AiServices.builder(Assistant.class)
                .chatLanguageModel(chatLanguageModel)
                .tools(toolsService)
                .streamingChatLanguageModel(streamingChatLanguageModel)
                .chatMemory(chatMemory)
                .build();
    }

    @Bean
    public AssistantUnique assistantUnique(ChatLanguageModel chatLanguageModel,
                                           StreamingChatLanguageModel streamingChatLanguageModel) {
        return AiServices.builder(AssistantUnique.class)
                .chatLanguageModel(chatLanguageModel)
                .streamingChatLanguageModel(streamingChatLanguageModel)
                .chatMemoryProvider(memoryId ->
                        MessageWindowChatMemory.builder().maxMessages(10).id(memoryId).build()
                )
                .build();
    }

    @Bean
    public AssistantUnique assistantUniqueStore(ChatLanguageModel chatLanguageModel,
                                                StreamingChatLanguageModel streamingChatLanguageModel) {
        PersistentChatMemoryStore store = new PersistentChatMemoryStore();

        ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .maxMessages(10)
                .id(memoryId)
                .chatMemoryStore(store)
                .build();

        return AiServices.builder(AssistantUnique.class)
                .chatLanguageModel(chatLanguageModel)
                .streamingChatLanguageModel(streamingChatLanguageModel)
                .chatMemoryProvider(chatMemoryProvider)
                .build();
    }

}
