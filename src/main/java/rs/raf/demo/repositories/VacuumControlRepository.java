package rs.raf.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import rs.raf.demo.model.User;
import rs.raf.demo.model.VacuumControl;

import javax.persistence.LockModeType;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;


public interface VacuumControlRepository extends JpaRepository<VacuumControl, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public Collection<VacuumControl> findAllByAddedBy(User user);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    public Optional<VacuumControl> findById(Long id);
}
