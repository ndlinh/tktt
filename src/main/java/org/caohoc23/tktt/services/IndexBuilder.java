package org.caohoc23.tktt.services;

import me.tongfei.progressbar.ProgressBar;
import org.caohoc23.tktt.entity.Dictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Service
public class IndexBuilder {

    @Autowired
    JdbcTemplate jdbcTemplate;

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

        cleanUp();

        ProgressBar pb = new ProgressBar("Indexing", files.length + 1);
        pb.start();
        for (File fs: files) {
            pb.step();
            index(fs);
        }
        pb.step();
        pb.stop();

        ProgressBar pb2 = new ProgressBar("Calculating", 4);
        pb2.start();

        pb2.step();
        createDictionary();

        pb2.step();
        calculateTfNorm();

        pb2.step();
        createDocsData();

        pb2.step();
        updateDocTfNorm2();
        pb2.stop();

    }

    private void index(File doc) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(doc));
        List<TokenContainer> words = collectToken(reader);
        reader.close();
        for (TokenContainer tc: words) {
            saveTerm(doc.getName(), tc);
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

    private void saveTerm(String docId, TokenContainer tc) {
        String sql1 = "INSERT INTO doc_tf (doc_id, term, tf) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql1, docId, tc.word.getText(), tc.count);
    }

    private void cleanUp() {
        jdbcTemplate.update("TRUNCATE TABLE doc_tf");
        jdbcTemplate.update("TRUNCATE TABLE dictionary");
        jdbcTemplate.update("TRUNCATE TABLE docs");
    }

    private void createDictionary() {
        String sql = "SELECT term, count(doc_id) as df FROM doc_tf GROUP BY term";
        List<Dictionary> list = jdbcTemplate.query(sql, new RowMapper<Dictionary>() {
            @Override
            public Dictionary mapRow(ResultSet resultSet, int i) throws SQLException {
                String term = resultSet.getString("term");
                Integer df = resultSet.getInt("df");

                return new Dictionary(term, df);
            }
        });


        for(Dictionary d: list) {
            String sql2 = "INSERT INTO dictionary (term, df) VALUES (?, ?)";
            jdbcTemplate.update(sql2, d.term, d.df);
        }
    }

    private void calculateTfNorm() {
        String sql = "UPDATE doc_tf SET tf_norm = 1 + LOG10(`tf`)";
        jdbcTemplate.update(sql);
    }

    private void createDocsData() {
        String sql = "INSERT INTO docs (doc_id, doc_norm) SELECT doc_id, SQRT(SUM(POWER(tf, 2))) as tf FROM doc_tf GROUP BY doc_id";
        jdbcTemplate.update(sql);
    }

    private void updateDocTfNorm2() {
        String sql = "UPDATE doc_tf a SET a.tf_norm2 = tf_norm / (SELECT doc_norm FROM docs b WHERE a.doc_id = b.doc_id)";
        jdbcTemplate.update(sql);
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
