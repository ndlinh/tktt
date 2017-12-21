package org.caohoc23.tktt.services;

import org.springframework.stereotype.Service;
import vn.hus.nlp.sd.IConstants;
import vn.hus.nlp.sd.SentenceDetector;
import vn.hus.nlp.sd.SentenceDetectorFactory;
import vn.hus.nlp.tokenizer.Tokenizer;
import vn.hus.nlp.tokenizer.TokenizerProvider;
import vn.hus.nlp.tokenizer.tokens.TaggedWord;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class Search {


    public List<String> token(String keyword) throws IOException {
        SentenceDetector sentenceDetector = SentenceDetectorFactory.create(IConstants.LANG_VIETNAMESE);
        Tokenizer tokenizer = TokenizerProvider.getInstance().getTokenizer();

        StringReader input = new StringReader(keyword);
        final String[] sentences = sentenceDetector.detectSentences(input);

        List<String> ret = new ArrayList<>();
        for (String s: sentences) {
            tokenizer.tokenize(new StringReader(s));
            Iterator<TaggedWord> it = tokenizer.getResult().iterator();
            while (it.hasNext()) {
                TaggedWord t = it.next();
                ret.add(t.getText());
            }
        }

        return ret;
    }

    public List<KeywordWeight> calcualeKeywordW(String keyword) throws IOException {
        List<String> keywords = token(keyword);
        for (String s: keywords) {
            System.out.println(s);


        }
    }

    public class KeywordWeight {
        public String term;
        public Double weight;

        public KeywordWeight(String term, Double weight) {
            this.term = term;
            this.weight = weight;
        }
    }
}
