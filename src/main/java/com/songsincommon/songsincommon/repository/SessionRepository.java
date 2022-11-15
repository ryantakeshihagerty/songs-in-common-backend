package com.songsincommon.songsincommon.repository;

import com.songsincommon.songsincommon.models.mysql.Session;
import org.springframework.data.repository.CrudRepository;

public interface SessionRepository extends CrudRepository<Session, String> {
}
