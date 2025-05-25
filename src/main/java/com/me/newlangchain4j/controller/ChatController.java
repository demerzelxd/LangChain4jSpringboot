package com.me.newlangchain4j.controller;

import com.me.newlangchain4j.config.AiConfig;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.service.TokenStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@RestController
@RequestMapping("/ai")
public class ChatController {

    @Autowired
    private QwenChatModel qwenChatModel;

    @Autowired
    private QwenStreamingChatModel qwenStreamingChatModel;

    @Autowired
    private AiConfig.Assistant assistant;

    @Autowired
    private AiConfig.AssistantUnique assistantUnique;

    @RequestMapping("/chat")
    public String test1(@RequestParam(defaultValue = "你是谁？") String message) {
        return qwenChatModel.chat(message);
    }

    @RequestMapping(value = "/streamchat", produces = "text/stream;charset=UTF-8")
    public Flux<String> test2(@RequestParam(defaultValue = "你是谁？") String message) {
        Flux<String> flux = Flux.create(fluxSink -> {
            qwenStreamingChatModel.chat(message, new StreamingChatResponseHandler() {
                @Override
                public void onPartialResponse(String s) {
                    fluxSink.next(s);
                }

                @Override
                public void onCompleteResponse(ChatResponse chatResponse) {
                    fluxSink.complete();
                }

                @Override
                public void onError(Throwable throwable) {
                    fluxSink.error(throwable);
                }
            });
        });
        return flux;
    }

    @RequestMapping("/chatmemory")
    public String test3(@RequestParam(defaultValue = "我叫徐庶") String message) {
        return assistant.chat(message);
    }

    /**
     * 连续对话
     *
     * @param message
     * @return
     */
    @RequestMapping(value = "/streamchatmemory", produces = "text/stream;charset=UTF-8")
    public Flux<String> test4(@RequestParam(defaultValue = "我是谁？") String message) {
        TokenStream tokenStream = assistant.streamingChat(message);
        Flux<String> flux = Flux.create(fluxSink -> {
            tokenStream.onPartialResponse(fluxSink::next)
                    .onCompleteResponse(chatResponse -> fluxSink.complete())
                    .onError(fluxSink::error)
                    .start();
        });
        return flux;
    }

    @RequestMapping("/chatmemoryunique")
    public String test5(@RequestParam(defaultValue = "我是谁") String message, Integer userId) {
        //http://localhost:8080/ai/chatmemoryunique?message=我是徐庶老师&userId=1
        return assistantUnique.chat(userId, message);
    }

    @RequestMapping(value = "/streamchatorder", produces = "text/stream;charset=UTF-8")
    public Flux<String> memoryStreamChat(@RequestParam(defaultValue = "我是谁") String message, HttpServletResponse response) {
        TokenStream stream = assistant.streamingChatForOrder(message, LocalDate.now().toString());

        return Flux.create(sink -> {
            stream.onPartialResponse(sink::next)
                    .onCompleteResponse(c -> sink.complete())
                    .onError(sink::error)
                    .start();

        });
    }

}
