package account.repositories;

import account.dto.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
    long deleteByEmailIgnoreCase(String email);

    @Query("UPDATE AppUser u SET u.failedAttempt = ?1 WHERE u.email = ?2")
    @Modifying
    public void updateFailedAttempts(int failAttempts, String email);

    Optional<AppUser> findAppUserByEmailIgnoreCase(String email);

    Optional<AppUser> findAppUserByName(String name);

    Optional<List<AppUser>> findAllByOrderByIdAsc();

    boolean existsAppUserByEmailIgnoreCase(String email);


}
