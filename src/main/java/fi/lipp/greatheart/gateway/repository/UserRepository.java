package fi.lipp.greatheart.gateway.repository;

import fi.lipp.greatheart.gateway.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {

    Optional<UserEntity> findByLogin(String login);
}
