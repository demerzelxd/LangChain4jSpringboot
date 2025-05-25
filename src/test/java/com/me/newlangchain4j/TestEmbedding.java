package com.me.newlangchain4j;

import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.junit.jupiter.api.Test;

public class TestEmbedding {
    public static void main(String[] args) {
        // 向量模型
        QwenEmbeddingModel embeddingModel = QwenEmbeddingModel.builder()
                .apiKey("sk-d9d9b8f5db1e4ff491511c142dfc82dc")
                .build();
        // 文本向量化
        Response<Embedding> embed = embeddingModel.embed("你好，我叫徐庶");
        System.out.println(embed.content());
        System.out.println(embed.content().vector().length);

    }

    @Test
    public void test02() {
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        QwenEmbeddingModel embeddingModel = QwenEmbeddingModel.builder()
                .apiKey("sk-d9d9b8f5db1e4ff491511c142dfc82dc")
                .build();

        // 利用向量模型进行向量化， 然后存储向量到向量数据库
        TextSegment segment1 = TextSegment.from("""
                预订航班:
                - 通过我们的网站或移动应用程序预订。
                - 预订时需要全额付款。
                - 确保个人信息（姓名、ID 等）的准确性，因为更正可能会产生 25 的费用。
                """);
        Embedding embedding1 = embeddingModel.embed(segment1).content();
        embeddingStore.add(embedding1, segment1);

        // 利用向量模型进行向量化， 然后存储向量到向量数据库
        TextSegment segment2 = TextSegment.from("""
                取消预订:
                - 最晚在航班起飞前 48 小时取消。
                - 取消费用：经济舱 75 美元，豪华经济舱 50 美元，商务舱 25 美元。
                - 退款将在 7 个工作日内处理。
                """);
        Embedding embedding2 = embeddingModel.embed(segment2).content();
        embeddingStore.add(embedding2, segment2);


        // 需要查询的内容 向量化
        Embedding queryEmbedding = embeddingModel.embed("退票要多少钱").content();

        // 去向量数据库查询
        // 构建查询条件
        EmbeddingSearchRequest build = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(1)
                .minScore(0.7)
                .build();

        // 查询
        EmbeddingSearchResult<TextSegment> segmentEmbeddingSearchResult = embeddingStore.search(build);
        segmentEmbeddingSearchResult.matches().forEach(embeddingMatch -> {
            System.out.println(embeddingMatch.score()); // 0.8144288515898701
            System.out.println(embeddingMatch.embedded().text()); // I like football.
        });

    }

}
