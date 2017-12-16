package org.caohoc23.tktt.serices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.hus.nlp.sd.SentenceDetector;
import vn.hus.nlp.tokenizer.Tokenizer;
import vn.hus.nlp.tokenizer.TokenizerProvider;
import vn.hus.nlp.tokenizer.tokens.TaggedWord;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class TokenizerService {

    @Autowired
    private SentenceDetector sentenceDetector;

    public List<TaggedWord> collectToken(Reader input) throws IOException {
        final List<TaggedWord> words = new ArrayList<TaggedWord>();
        final String[] sentences = sentenceDetector.detectSentences(input);
        final Tokenizer tokenizer = TokenizerProvider.getInstance().getTokenizer();
        for (String s: sentences) {
            tokenizer.tokenize(new StringReader(s));
            words.addAll(tokenizer.getResult());
        }

        return words;
    }
}
