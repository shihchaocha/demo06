package com.example.demo06;

import org.springframework.core.io.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SpringAiBoardGameService implements BoardGameService {

    private final ChatClient chatClient;
    private final GameRulesService gameRulesService;

    public SpringAiBoardGameService(ChatClient.Builder chatClientBuilder,
                                    GameRulesService gameRulesService) {
        this.chatClient = chatClientBuilder.build();
        this.gameRulesService = gameRulesService;
    }



    /**
        private static final String questionPromptTemplate = """
        Answer this question about {game}: {question}
        """;
    */
    @Value("classpath:/promptTemplates/systemPromptTemplate.st")
    Resource questionPromptTemplate;

    @Override
    public Answer askQuestion(Question question) {
        var gameRules = gameRulesService.getRulesFor(question.gameTitle());

        var answerText = chatClient.prompt()
                .system(systemSpec -> systemSpec
                        .text(questionPromptTemplate)
                        .param("gameTitle", question.gameTitle())
                        .param("question", question.question())
                        .param("rules", gameRules))
                .user(question.question())
                .call()
                .content();

        return new Answer(question.gameTitle(), answerText);
    }

}
