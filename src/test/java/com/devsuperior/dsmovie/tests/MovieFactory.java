package com.devsuperior.dsmovie.tests;

import java.util.List;
import java.util.stream.LongStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;

public class MovieFactory {
	
	public static MovieEntity createMovieEntity() {
		return createMovieEntityWithId(1L);
	}

	public static MovieEntity createMovieEntityWithId(Long id) {
		MovieEntity movie = new MovieEntity(id, "Test Movie", 0.0, 0, "https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg");
		return movie;
	}
    
	public static MovieDTO createMovieDTO() {
		MovieEntity movie = createMovieEntity();
		return new MovieDTO(movie);
	}

    public static Page<MovieEntity> createMovieEntityPage() {
        List<MovieEntity> list = LongStream.rangeClosed(1, 10)
            .mapToObj(id -> {
                MovieEntity e = createMovieEntity();
                e.setId(id);
                return e;
            })
            .toList();
        return new PageImpl<>(list);
    }

}
