package org.caohoc23.tktt;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import vn.hus.nlp.sd.IConstants;
import vn.hus.nlp.sd.SentenceDetector;
import vn.hus.nlp.sd.SentenceDetectorFactory;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class AppConfig extends SpringBootConsoleApplication {
    @Bean
    public SentenceDetector sentenceDetector() {
        return SentenceDetectorFactory.create(IConstants.LANG_VIETNAMESE);
    }
}
