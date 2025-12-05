package com.devsuperior.dsmovie.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.services.exceptions.DatabaseException;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class MovieServiceTests {
	
	@InjectMocks
	private MovieService movieService;

    @Mock
    private MovieRepository movieRepository;

    private Page<MovieEntity> page;
    private MovieEntity movieEntity;
    private MovieDTO movieDTO;

    private long existingId;
    private long nonExistingId;
    private long dependentId;

    @BeforeEach
    public void setUp() {
        page = MovieFactory.createMovieEntityPage();
        movieEntity = MovieFactory.createMovieEntity();
        movieDTO = MovieFactory.createMovieDTO();

        existingId = 1L;
        nonExistingId = 999L;
        dependentId = 10L;

        when(movieRepository.searchByTitle(anyString(), any(Pageable.class))).thenReturn(page);
        when(movieRepository.findById(existingId)).thenReturn(Optional.of(movieEntity));
        when(movieRepository.findById(nonExistingId)).thenReturn(Optional.empty());
        when(movieRepository.getReferenceById(existingId)).thenReturn(movieEntity);
        when(movieRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);
        when(movieRepository.existsById(existingId)).thenReturn(true);
        when(movieRepository.existsById(nonExistingId)).thenReturn(false);
        when(movieRepository.existsById(dependentId)).thenReturn(true);
        when(movieRepository.save(any(MovieEntity.class))).thenReturn(movieEntity);
        doNothing().when(movieRepository).deleteById(existingId);
        doNothing().when(movieRepository).deleteById(nonExistingId);
        doThrow(DataIntegrityViolationException.class).when(movieRepository).deleteById(dependentId);
    }
	
	@Test
	public void findAllShouldReturnPagedMovieDTO() {
        Page<MovieDTO> result = movieService.findAll("", Pageable.unpaged());

        assertNotNull(result);
        assertEquals(page.getNumberOfElements(), result.getNumberOfElements());
	}
	
	@Test
	public void findByIdShouldReturnMovieDTOWhenIdExists() {
        MovieDTO result = movieService.findById(existingId);

        assertNotNull(result);
        assertEquals(existingId, result.getId());
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> {
            movieService.findById(nonExistingId);
        });
	}
	
	@Test
	public void insertShouldReturnMovieDTO() {
        MovieDTO result = movieService.insert(movieDTO);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(movieDTO.getTitle(), result.getTitle());
        assertEquals(movieDTO.getScore(), result.getScore());
        assertEquals(movieDTO.getImage(), result.getImage());
        assertEquals(movieDTO.getCount(), result.getCount());
	}
	
	@Test
	public void updateShouldReturnMovieDTOWhenIdExists() {
        MovieDTO result = movieService.update(existingId, movieDTO);

        assertNotNull(result);
        assertEquals(movieDTO.getId(), result.getId());
        assertEquals(movieDTO.getTitle(), result.getTitle());
        assertEquals(movieDTO.getScore(), result.getScore());
        assertEquals(movieDTO.getImage(), result.getImage());
        assertEquals(movieDTO.getCount(), result.getCount());
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> {
            movieService.update(nonExistingId, movieDTO);
        });
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
        assertDoesNotThrow(() -> {
            movieService.delete(existingId);
        });
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> {
            movieService.delete(nonExistingId);
        });
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
        assertThrows(DatabaseException.class, () -> {
            movieService.delete(dependentId);
        });
	}
}
