package org.caohoc23.tktt.services;

import me.tongfei.progressbar.ProgressBar;
import org.springframework.stereotype.Service;
import vn.hus.nlp.sd.IConstants;
import vn.hus.nlp.sd.SentenceDetector;
import vn.hus.nlp.sd.SentenceDetectorFactory;
import vn.hus.nlp.tokenizer.Tokenizer;
import vn.hus.nlp.tokenizer.TokenizerProvider;
import vn.hus.nlp.tokenizer.tokens.TaggedWord;
import vn.hus.nlp.utils.FileIterator;
import vn.hus.nlp.utils.TextFileFilter;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Service
public class IndexBuilder {
    /**
     * Trả về danh sách các file có đuôi .txt trong thư mục được chỉ định.
     *
     * @param path
     * @return List of files
     * @throws FileNotFoundException
     */
    public File[] collectDocument(String path) throws FileNotFoundException {
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new FileNotFoundException(String.format("Directory %s is not exist", path));
        }

        return FileIterator.listFiles(dir, new TextFileFilter());
    }

    public void run(String path) throws IOException {
        File[] files = collectDocument(path);

        ProgressBar pb = new ProgressBar("Indexing", files.length);
        pb.start();
        for (File fs: files) {
            pb.step();
            index(fs);
        }
        pb.stop();
    }

    private void index(File doc) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(doc));
        List<TokenContainer> words = collectToken(reader);
        reader.close();
        for (TokenContainer tc: words) {
            //TODO: tính tf & idf và lưu vào DB
        }
    }

    public List<TokenContainer> collectToken(Reader input) throws IOException {
        SentenceDetector sentenceDetector = SentenceDetectorFactory.create(IConstants.LANG_VIETNAMESE);
        Tokenizer tokenizer = TokenizerProvider.getInstance().getTokenizer();

        final String[] sentences = sentenceDetector.detectSentences(input);
        final HashMap<String, TokenContainer> tracker = new HashMap<>();

        for (String s: sentences) {
            tokenizer.tokenize(new StringReader(s));
            Iterator<TaggedWord> it = tokenizer.getResult().iterator();
            while (it.hasNext()) {
                TaggedWord t = it.next();
                if (accept(t)) {
                    TokenContainer tc;
                    if (tracker.containsKey(t.getText())) {
                        tc = tracker.get(t.getText());
                        tc.count += 1;
                    } else {
                        tc = new TokenContainer(1, t);
                    }
                    tracker.put(t.getText(), tc);
                }
            }
        }

        return new ArrayList<>(tracker.values());
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

    public class TokenContainer {
        public int count;
        public TaggedWord word;

        public TokenContainer(int count, TaggedWord word) {
            this.count = count;
            this.word = word;
        }

        public String toString() {
            return String.format("%s: %d", this.word.getText(), this.count);
        }
    }
}
