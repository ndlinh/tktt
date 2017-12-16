package org.caohoc23.tktt;

import me.tongfei.progressbar.ProgressBar;
import org.apache.commons.cli.*;
import org.caohoc23.tktt.serices.TokenizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import vn.hus.nlp.tokenizer.VietTokenizer;
import vn.hus.nlp.tokenizer.TokenizerOptions;
import vn.hus.nlp.tokenizer.TokenizerProvider;
import vn.hus.nlp.tokenizer.VietTokenizer;
import vn.hus.nlp.tokenizer.tokens.TaggedWord;

import java.io.StringReader;
import java.util.Iterator;

import static java.lang.System.exit;
import static java.lang.System.in;

public class SpringBootConsoleApplication implements CommandLineRunner {

    @Autowired
    private TokenizerService tokenizerService;

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(AppConfig.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {

        try {
            Options options = new Options();
            options.addOption("p", true, "Document Path");
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("p")) {
                System.out.println("Hello World!!!!!!");
                System.out.println(cmd.getOptionValue("p"));
            } else {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("java -jar target/tktt-1.0-SNAPSHOT.jar", options);
            }
        } catch (UnrecognizedOptionException e) {

        }

        StringReader input = new StringReader("Người mẫu cao tuổi trong bức hại đó là Thúy Hạnh. Trời hôm nay mưa nhiều quá.");


        Iterator<TaggedWord> taggedWords = tokenizerService.collectToken(input).iterator();
        System.out.println("BEGIN");
        while (taggedWords.hasNext()) {
            System.out.println(taggedWords.next().toString());
        }

//        ProgressBar pb = new ProgressBar("Indexing", 100);
//        pb.start();
//        for (int i = 0; i < 100; i++) {
//            pb.step();
//            Thread.sleep(300);
//        }
//        pb.stop();

        exit(0);
    }

}
