package io.fourfinanceit.repository;

import io.fourfinanceit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
