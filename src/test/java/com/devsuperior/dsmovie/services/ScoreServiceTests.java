package com.devsuperior.dsmovie.services;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {
	
	@InjectMocks
	private ScoreService scoreService;

    @Mock
    private UserService userService;

    @Mock
	private MovieRepository movieRepository;
	
	@Mock
	private ScoreRepository scoreRepository;

    private ScoreDTO scoreDTO;
    private ScoreEntity scoreEntity;
    private MovieEntity movie;
    private UserEntity user;

    private long existingMovieId;
    private long nonExistingMovieId;

    @BeforeEach
    public void setUp() {
        scoreDTO = ScoreFactory.createScoreDTO();
        scoreEntity = ScoreFactory.createScoreEntity();
        movie = MovieFactory.createMovieEntity();
        user = UserFactory.createUserEntity();

        movie.getScores().add(scoreEntity);

        
        existingMovieId = 1L;
        nonExistingMovieId = 999L;

        when(userService.authenticated()).thenReturn(user);
        when(movieRepository.findById(existingMovieId)).thenReturn(Optional.of(movie));
        when(movieRepository.findById(nonExistingMovieId)).thenReturn(Optional.empty());
        when(movieRepository.save(any(MovieEntity.class))).thenReturn(movie);
        when(scoreRepository.saveAndFlush(any(ScoreEntity.class))).thenReturn(scoreEntity);
    }
	
	@Test
	public void saveScoreShouldReturnMovieDTO() {
        MovieDTO result = scoreService.saveScore(scoreDTO);

        assertNotNull(result);
        assertEquals(scoreDTO.getMovieId(), result.getId());
	}
	
	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
        MovieEntity movie = MovieFactory.createMovieEntityWithId(nonExistingMovieId);
        MovieDTO movieDTO = new MovieDTO(movie);
        ScoreDTO scoreDTO = ScoreFactory.createScoreDTOWithMovie(movieDTO);

        assertThrows(ResourceNotFoundException.class, () -> {
            scoreService.saveScore(scoreDTO);
        });
	}

}
