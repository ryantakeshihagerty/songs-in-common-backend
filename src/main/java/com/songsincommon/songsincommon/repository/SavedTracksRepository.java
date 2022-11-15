package com.songsincommon.songsincommon.repository;

import com.songsincommon.songsincommon.models.mongoDb.UserSavedTracks;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SavedTracksRepository extends MongoRepository<UserSavedTracks, String> {
}
