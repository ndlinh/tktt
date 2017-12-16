package org.caohoc23.tktt.repository;

import org.caohoc23.tktt.entity.DocIndex;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocIndexRepository extends MongoRepository<DocIndex, String> {

    /**
     * Lấy danh sách các term của document
     *
     * @param docId
     * @return
     */
    List<DocIndex> findByDocId(String docId);
}
