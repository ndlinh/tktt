package org.caohoc23.tktt.entity;

import org.springframework.data.annotation.Id;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "doc_indexes")
public class DocIndex {

    @Id
    public String id;

    public String term;

    public Float frequency;

    @Indexed
    public String docId;

    public DocIndex() {

    }

    public DocIndex(String docId, String term, Float frequency) {
        this.docId = docId;
        this.term = term;
        this.frequency = frequency;
    }

    @Override
    public String toString() {
        return String.format("%s\t\t%s\t\t%s", docId, term, frequency);
    }
}