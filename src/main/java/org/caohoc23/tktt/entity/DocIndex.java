package org.caohoc23.tktt.entity;

import org.springframework.data.annotation.Id;

public class DocIndex {

    @Id
    public String id;

    public String term;

    public Float frequency;

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