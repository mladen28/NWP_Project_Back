package rs.raf.demo.services;

import rs.raf.demo.model.VacuumControl;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface VacuumControlServiceInterface {
    VacuumControl addVacuumControl(String name, String userMail);
    Optional<VacuumControl> findById(Long id);
    Collection<VacuumControl> getVacuumsByUser(String userMail);
    Collection<VacuumControl> searchVacuums(String name, List<String> statuses, LocalDate dateFrom, LocalDate dateTo, String userMail);
    void removeVacuumControl(Long id);
    void startVacuumControl(Long id,boolean scheduled) throws InterruptedException;
    void stopVacuumControl(Long id, boolean scheduled) throws InterruptedException;
    void dischargeVacuumControl(Long id, boolean scheduled) throws InterruptedException, ParseException;
    void scheduleVacuumControl(Long id, String date, String time, String action) throws ParseException;
}
