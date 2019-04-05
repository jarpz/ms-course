package com.microservice.song.persistence;

import com.microservice.song.persistence.model.Song;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends
        CrudRepository<Song, Integer> {
}
