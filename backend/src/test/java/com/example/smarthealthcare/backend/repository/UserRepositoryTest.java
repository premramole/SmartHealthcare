package com.example.smarthealthcare.backend.repository;

import com.example.smarthealthcare.backend.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void whenFindByUserId_thenReturnUser() {
        // Given
        User user = new User("user123", "John Doe", "john@example.com", "patient");
        entityManager.persistAndFlush(user);

        // When
        Optional<User> found = userRepository.findByUserId("user123");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("John Doe");
    }

    @Test
    void whenFindByEmail_thenReturnUser() {
        // Given
        User user = new User("user123", "John Doe", "john@example.com", "patient");
        entityManager.persistAndFlush(user);

        // When
        Optional<User> found = userRepository.findByEmail("john@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo("user123");
    }

    @Test
    void whenFindByNonExistentUserId_thenReturnEmpty() {
        // When
        Optional<User> found = userRepository.findByUserId("nonexistent");

        // Then
        assertThat(found).isEmpty();
    }
}