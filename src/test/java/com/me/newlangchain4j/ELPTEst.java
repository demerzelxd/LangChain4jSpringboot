package com.me.newlangchain4j;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByCharacterSplitter;
import dev.langchain4j.data.document.splitter.DocumentByRegexSplitter;
import dev.langchain4j.data.document.splitter.DocumentBySentenceSplitter;
import dev.langchain4j.data.segment.TextSegment;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ELPTEst {

    @Test
    public void test01 () {
        Document document = ClassPathDocumentLoader.loadDocument("rag/terms-of-service.txt", new TextDocumentParser());
        System.out.println(document.text());
        // 分割器
//        DocumentByCharacterSplitter splitter = new DocumentByCharacterSplitter(
//                20,         // 每段最长字数
//                10                              // 自然语言最大重叠字数
//        );
//        List<TextSegment> segments = splitter.split(document);

//        DocumentByRegexSplitter splitter = new DocumentByRegexSplitter(
//                "\\n\\d+\\.",  // 匹配 "1. 标题" 格式
//                "\n",            // 保留换行符作为段落连接符
//                100,             // 每个段最多 500 字符
//                20,               // 段间重叠 50 字符以保持连贯性
//                new DocumentByCharacterSplitter(100,50)
//        );
        DocumentBySentenceSplitter splitter = new DocumentBySentenceSplitter(
                500,
                30);

    }

}
