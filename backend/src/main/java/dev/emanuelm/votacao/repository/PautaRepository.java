package dev.emanuelm.votacao.repository;

import dev.emanuelm.votacao.domain.Pauta;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PautaRepository extends JpaRepository<Pauta, Long> {

  Optional<Pauta> findByUuid(String uuid);

  boolean existsByUuid(String uuid);

}
