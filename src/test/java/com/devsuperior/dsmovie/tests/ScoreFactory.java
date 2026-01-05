package com.devsuperior.dsmovie.tests;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;

public class ScoreFactory {
	
	public static Double scoreValue = 4.5;
	
	public static ScoreEntity createScoreEntity() {
		MovieEntity movie = MovieFactory.createMovieEntity();
		return createScoreEntityWithMovie(movie);
	}

    public static ScoreEntity createScoreEntityWithMovie(MovieEntity movie) {
		UserEntity user = UserFactory.createUserEntity();
		ScoreEntity score = new ScoreEntity();
		
		score.setMovie(movie);
		score.setUser(user);
		score.setValue(scoreValue);
		movie.getScores().add(score);
		return score;
	}
	
	public static ScoreDTO createScoreDTO() {
		ScoreEntity score = createScoreEntity();
		return new ScoreDTO(score.getId().getMovie().getId(), score.getValue());
	}

    public static ScoreDTO createScoreDTOWithMovie(MovieDTO movie) {
        MovieEntity movieEntity = new MovieEntity(
            movie.getId(),
            movie.getTitle(),
            movie.getScore(),
            movie.getCount(),
            movie.getImage());
		ScoreEntity score = createScoreEntityWithMovie(movieEntity);
		return new ScoreDTO(score.getId().getMovie().getId(), score.getValue());
	}
}
