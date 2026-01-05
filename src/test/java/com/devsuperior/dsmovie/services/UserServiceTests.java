package com.devsuperior.dsmovie.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.projections.UserDetailsProjection;
import com.devsuperior.dsmovie.repositories.UserRepository;
import com.devsuperior.dsmovie.tests.UserDetailsFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import com.devsuperior.dsmovie.utils.CustomUserUtil;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class UserServiceTests {

	@InjectMocks
	private UserService service;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomUserUtil customUserUtil;

    private UserEntity userEntity;

    private List<UserDetailsProjection> userDetailsProjections;

    private String existingUserName;
    private String nonExistingUserName;

    @BeforeEach
    public void setUp() {
        userEntity = UserFactory.createUserEntity();

        
        existingUserName = userEntity.getUsername();
        nonExistingUserName = "nonecziste@email.com";

        userDetailsProjections = UserDetailsFactory.createCustomClientUser(existingUserName);

        when(userRepository.findByUsername(existingUserName)).thenReturn(Optional.of(userEntity));
        when(userRepository.findByUsername(nonExistingUserName)).thenReturn(Optional.empty());

        when(userRepository.searchUserAndRolesByUsername(existingUserName)).thenReturn(userDetailsProjections);
        when(userRepository.searchUserAndRolesByUsername(nonExistingUserName)).thenReturn(Collections.emptyList());
    }

	@Test
	public void authenticatedShouldReturnUserEntityWhenUserExists() {
        when(customUserUtil.getLoggedUsername()).thenReturn(existingUserName);

        UserEntity result = service.authenticated();

        assertNotNull(result);
        assertEquals(userEntity.getUsername(), result.getUsername());
	}

	@Test
	public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
        when(customUserUtil.getLoggedUsername()).thenReturn(nonExistingUserName);

        assertThrows(UsernameNotFoundException.class, () -> {
            service.authenticated();
        });
	}

	@Test
	public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {
        UserDetails result = service.loadUserByUsername(existingUserName);

        assertNotNull(result);
        assertEquals(existingUserName, result.getUsername());
	}

	@Test
	public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
        assertThrows(UsernameNotFoundException.class, () -> {
            service.loadUserByUsername(nonExistingUserName);
        });
	}
}
