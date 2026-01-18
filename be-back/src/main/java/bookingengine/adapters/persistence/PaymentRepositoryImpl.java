package bookingengine.adapters.persistence;

import bookingengine.adapters.persistence.entities.PaymentJpaEntity;
import bookingengine.adapters.persistence.mappers.PaymentMapper;
import bookingengine.adapters.persistence.repositories.PaymentJpaRepository;
import bookingengine.domain.entities.Payment;
import bookingengine.domain.repositories.PaymentRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository jpaRepository;
    private final PaymentMapper mapper;

    public PaymentRepositoryImpl(PaymentJpaRepository jpaRepository, PaymentMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Payment save(Payment payment) {
        PaymentJpaEntity entity = mapper.toEntity(payment);
        PaymentJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Payment> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Payment> findByReservationId(Long reservationId) {
        return jpaRepository.findByReservationId(reservationId).stream().map(mapper::toDomain).toList();
    }
}
