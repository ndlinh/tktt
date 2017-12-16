package org.caohoc23.tktt;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import vn.hus.nlp.sd.IConstants;
import vn.hus.nlp.sd.SentenceDetector;
import vn.hus.nlp.sd.SentenceDetectorFactory;
import vn.hus.nlp.tokenizer.TokenizerProvider;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableMongoRepositories(basePackages = "org.caohoc23.tktt.repository")
public class AppConfig extends SpringBootConsoleApplication {

    @Bean
    public vn.hus.nlp.tokenizer.Tokenizer tokenizer() {
        return TokenizerProvider.getInstance().getTokenizer();
    }

    @Bean
    public SentenceDetector sentenceDetector() {
        return SentenceDetectorFactory.create(IConstants.LANG_VIETNAMESE);
    }
}
