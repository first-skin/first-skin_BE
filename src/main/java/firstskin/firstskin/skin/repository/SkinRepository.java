package firstskin.firstskin.skin.repository;

import firstskin.firstskin.skin.Skin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SkinRepository extends JpaRepository<Skin, Long> {

    Optional<Skin> findByResult(String result);
}
