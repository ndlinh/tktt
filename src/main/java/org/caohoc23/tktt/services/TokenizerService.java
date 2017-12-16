package org.caohoc23.tktt.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import vn.hus.nlp.sd.SentenceDetector;
import vn.hus.nlp.tokenizer.Tokenizer;
import vn.hus.nlp.tokenizer.TokenizerProvider;
import vn.hus.nlp.tokenizer.tokens.TaggedWord;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class TokenizerService {

    @Autowired
    private Tokenizer tokenizer;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    private SentenceDetector sentenceDetector;

    public List<TaggedWord> collectToken(Reader input) throws IOException {
        final List<TaggedWord> words = new ArrayList<TaggedWord>();
        final String[] sentences = sentenceDetector.detectSentences(input);
        for (String s: sentences) {
            tokenizer.tokenize(new StringReader(s));
            Iterator<TaggedWord> it = tokenizer.getResult().iterator();
            while (it.hasNext()) {
                TaggedWord t = it.next();
                if (accept(t)) {
                    words.add(t);
                }
            }
        }

        return words;
    }

    /**
     * Chỉ chấp nhận từ, bỏ các dấu câu, ký tự đặc biệt.
     */
    private final boolean accept(TaggedWord word) {
        final String token = word.getText();
        if (token.length() == 1) {
            return Character.isLetterOrDigit(token.charAt(0));
        }
        return true;
    }
}
