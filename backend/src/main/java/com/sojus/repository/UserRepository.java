package com.sojus.repository;

import com.sojus.domain.entity.User;
import com.sojus.domain.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameAndDeletedFalse(String username);
    List<User> findAllByDeletedFalse();
    List<User> findAllByRoleAndDeletedFalse(RoleName role);
    boolean existsByUsername(String username);
}
