package com.songsincommon.songsincommon.repository;

import com.songsincommon.songsincommon.models.mongoDb.SharedTracks;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface SharedTracksRepository extends MongoRepository<SharedTracks, String> {
    @Query("{uniqueSessionCode:'?0'}")
    SharedTracks findItemByUniqueSessionCode(String uniqueSessionCode);
}
