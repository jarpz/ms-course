package com.microservice.song.mapping;

import com.microservice.song.api.models.RegisterSongRequest;
import com.microservice.song.api.models.SongResponse;
import com.microservice.song.persistence.model.Song;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SongMapper {

  SongResponse modelToResponse(Song song);

  Song requestToModel(RegisterSongRequest request);
}
