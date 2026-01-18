package bookingengine.adapters.persistence;

import bookingengine.adapters.persistence.entities.ChambreJpaEntity;
import bookingengine.adapters.persistence.mappers.ChambreMapper;
import bookingengine.adapters.persistence.repositories.ChambreJpaRepository;
import bookingengine.domain.entities.Chambre;
import bookingengine.domain.repositories.ChambreRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ChambreRepositoryImpl implements ChambreRepository {

    private final ChambreJpaRepository jpaRepository;
    private final ChambreMapper mapper;

    public ChambreRepositoryImpl(ChambreJpaRepository jpaRepository, ChambreMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Chambre save(Chambre chambre) {
        ChambreJpaEntity entity = mapper.toEntity(chambre);
        ChambreJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Chambre> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Chambre> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Chambre> findByDisponible(boolean disponible) {
        return jpaRepository.findByDisponible(disponible).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Chambre> findByType(String type) {
        return jpaRepository.findByType(type).stream().map(mapper::toDomain).toList();
    }
}
