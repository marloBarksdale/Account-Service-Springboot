package account.repositories;

import account.dto.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {


    Optional<AppUser> findAppUserByEmail(String email);

    Optional<AppUser> findAppUserByName(String name);

    boolean existsAppUserByEmail(String email);

}
