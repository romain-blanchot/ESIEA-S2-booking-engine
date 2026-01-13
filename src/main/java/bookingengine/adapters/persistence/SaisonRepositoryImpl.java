package bookingengine.adapters.persistence;

import bookingengine.adapters.persistence.entities.SaisonJpaEntity;
import bookingengine.adapters.persistence.mappers.SaisonMapper;
import bookingengine.adapters.persistence.repositories.SaisonJpaRepository;
import bookingengine.domain.entities.Saison;
import bookingengine.domain.repositories.SaisonRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class SaisonRepositoryImpl implements SaisonRepository {

    private final SaisonJpaRepository jpaRepository;
    private final SaisonMapper mapper;

    public SaisonRepositoryImpl(SaisonJpaRepository jpaRepository, SaisonMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Saison save(Saison saison) {
        SaisonJpaEntity entity = mapper.toEntity(saison);
        SaisonJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Saison> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Saison> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Optional<Saison> findByDate(LocalDate date) {
        return jpaRepository.findByDate(date).map(mapper::toDomain);
    }
}
