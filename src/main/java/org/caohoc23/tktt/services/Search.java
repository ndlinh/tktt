package org.caohoc23.tktt.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;
import vn.hus.nlp.sd.IConstants;
import vn.hus.nlp.sd.SentenceDetector;
import vn.hus.nlp.sd.SentenceDetectorFactory;
import vn.hus.nlp.tokenizer.Tokenizer;
import vn.hus.nlp.tokenizer.TokenizerProvider;
import vn.hus.nlp.tokenizer.tokens.TaggedWord;

import java.io.IOException;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service
public class Search {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private String lastDocId = "";

    private Integer totalDocs = 0;

    public List<String> token(String keyword) throws IOException {
        SentenceDetector sentenceDetector = SentenceDetectorFactory.create(IConstants.LANG_VIETNAMESE);
        Tokenizer tokenizer = TokenizerProvider.getInstance().getTokenizer();

        StringReader input = new StringReader(keyword);
        final String[] sentences = sentenceDetector.detectSentences(input);

        List<String> ret = new ArrayList<>();
        System.out.print("Tokens: ");
        for (String s: sentences) {
            tokenizer.tokenize(new StringReader(s));
            Iterator<TaggedWord> it = tokenizer.getResult().iterator();
            while (it.hasNext()) {
                TaggedWord t = it.next();
                ret.add(t.getText());

                System.out.print(String.format("[ %s ]", t.getText()));
            }
        }
        System.out.println("");
        return ret;
    }

    public HashMap<String, Double> calculateKeywordW(String keyword) throws IOException {
        List<String> keywords = token(keyword);
        HashMap<String, Double> keywordWeights = new HashMap<>();
        for (String s: keywords) {
            if (keywordWeights.containsKey(s)) {
                Double w = keywordWeights.get(s);
                keywordWeights.put(s, w + 1);
            } else {
                keywordWeights.put(s, 1.0);
            }
        }

        totalDocs = getTotalDocs();
        for (String s: keywordWeights.keySet()) {
            Double w = keywordWeights.get(s);
            Double idf = getIdf(s, totalDocs);
            keywordWeights.put(s, (1 + Math.log(w)) * idf);
        }

        return keywordWeights;
    }

    public Map<String, Double> search(String keyword) throws IOException {
        //Tính idf của keywords
        HashMap<String, Double> keywordW = calculateKeywordW(keyword);

        String sql = "SELECT * FROM doc_tf WHERE term IN (" + makeQueryString(keywordW.keySet()) + ") ORDER BY doc_id";
        HashMap<String, Double> result = new HashMap<>();

        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
                String docId = resultSet.getString("doc_id");
                Double w = resultSet.getDouble("w_td_norm");
                String dbTerm = resultSet.getString("term");
                if (!keywordW.keySet().contains(dbTerm)) {
                    lastDocId = docId;
                    return;
                }

                if (lastDocId.equalsIgnoreCase(docId) == false) {
                    result.put(docId, w * keywordW.get(dbTerm));
                } else {
                    Double prevW = result.get(docId);
                    if (prevW != null) {
                        result.put(docId, (w * keywordW.get(dbTerm) + prevW));
                    } else {
                        result.put(docId, w * keywordW.get(dbTerm));
                    }
                }
                lastDocId = docId;
            }
        });

        //sort result
        Map<String, Double> result2 = new LinkedHashMap<>();
        result.entrySet().stream().sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEachOrdered(x -> result2.put(x.getKey(), (x.getValue())));

        return result2;
    }

    private String makeQueryString(Set<String> keys) {
        String sep = "";
        String ret = "";
        for (String s: keys) {
            ret += sep + "'" + s + "'";
            sep = ",";
        }

        return ret;
    }

    //log10(total_doc / df)

    private Double getIdf(String term, Integer totalDocs) {
        String s = "SELECT * FROM dictionary WHERE term = '" + term + "'";
        Double[] df = {1.0};
        jdbcTemplate.query(s, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
                String dbTerm = resultSet.getString("term");
                if (dbTerm.equalsIgnoreCase(term)) {
                    df[0] = resultSet.getDouble("df");
                }
            }
        });

        return Math.log10(totalDocs / df[0]);

    }

    private Integer getTotalDocs() {
        String sql = "SELECT COUNT(DISTINCT doc_id) as total FROM doc_tf";
        Integer total = jdbcTemplate.queryForObject(sql, Integer.TYPE);

        return total;
    }

}
