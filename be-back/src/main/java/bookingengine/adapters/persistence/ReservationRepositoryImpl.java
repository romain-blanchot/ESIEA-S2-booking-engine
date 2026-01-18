package bookingengine.adapters.persistence;

import bookingengine.adapters.persistence.entities.ReservationJpaEntity;
import bookingengine.adapters.persistence.mappers.ReservationMapper;
import bookingengine.adapters.persistence.repositories.ReservationJpaRepository;
import bookingengine.domain.entities.Reservation;
import bookingengine.domain.entities.ReservationStatus;
import bookingengine.domain.repositories.ReservationRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ReservationRepositoryImpl implements ReservationRepository {

    private final ReservationJpaRepository jpaRepository;
    private final ReservationMapper mapper;

    public ReservationRepositoryImpl(ReservationJpaRepository jpaRepository, ReservationMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Reservation save(Reservation reservation) {
        ReservationJpaEntity entity = mapper.toEntity(reservation);
        ReservationJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Reservation> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Reservation> findByStatus(ReservationStatus status) {
        ReservationJpaEntity.ReservationStatusJpa jpaStatus = ReservationJpaEntity.ReservationStatusJpa.valueOf(status.name());
        return jpaRepository.findByStatus(jpaStatus).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Reservation> findByChambreId(Long chambreId) {
        return jpaRepository.findByChambreId(chambreId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Reservation> findByUtilisateurId(Long utilisateurId) {
        return jpaRepository.findByUtilisateurId(utilisateurId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
}
