package rs.raf.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.raf.demo.model.ErrorMessage;
import rs.raf.demo.model.VacuumControl;
import rs.raf.demo.model.enums.Status;
import rs.raf.demo.repositories.ErrorMessageRepository;
import rs.raf.demo.repositories.UserRepository;
import rs.raf.demo.repositories.VacuumControlRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Service
public class VacuumControlService implements VacuumControlServiceInterface{

    private VacuumControlRepository vacuumControlRepository;
    private UserRepository userRepository;
    private TaskScheduler taskScheduler;
    private ErrorMessageRepository errorMessageRepository;

    @Autowired
    public VacuumControlService(VacuumControlRepository vacuumControlRepository, UserRepository userRepository, TaskScheduler taskScheduler, ErrorMessageRepository errorMessageRepository) {
        this.vacuumControlRepository = vacuumControlRepository;
        this.userRepository = userRepository;
        this.taskScheduler = taskScheduler;
        this.errorMessageRepository = errorMessageRepository;

    }
    @Override
    @Transactional
    public VacuumControl addVacuumControl(String name, String userMail) {
        System.err.println("add vacuumControl");
        return vacuumControlRepository.save(new VacuumControl(0L, Status.STOPPED, userRepository.findByMail(userMail), true, name, LocalDate.now()/*, 0*/));
    }

    @Override
    @Transactional
    public Optional<VacuumControl> findById(Long id) {
        System.err.println("finding");
        return vacuumControlRepository.findById(id);
    }

    @Override
    @Transactional
    public Collection<VacuumControl> getVacuumsByUser(String userMail) {
        return vacuumControlRepository.findAllByAddedBy(userRepository.findByMail(userMail));
    }

    @Override
    @Transactional
    public Collection<VacuumControl> searchVacuums(String name, List<String> statuses, LocalDate dateFrom, LocalDate dateTo, String userMail) {
        ArrayList<VacuumControl> allVacuumsByUser = (ArrayList<VacuumControl>) getVacuumsByUser(userMail);
        ArrayList<VacuumControl> filteredVacuums = new ArrayList<>();
        int addFlag;

        for (VacuumControl vacuumControl : allVacuumsByUser) {
            addFlag = 0;

            if (name != null && vacuumControl.getName().toLowerCase().contains(name.toLowerCase())) addFlag++; //flag == 1
            else if (name == null) addFlag++;

            if (statuses != null && statuses.contains(vacuumControl.getStatus().toString())) addFlag++; //flag == 2
            else if (statuses == null) addFlag++;

            if (dateFrom != null && dateTo != null && vacuumControl.getCreationDate().isAfter(dateFrom) && vacuumControl.getCreationDate().isBefore(dateTo))
                addFlag++; //flag == 3
            else if (dateFrom == null || dateTo == null) addFlag++;

            if (addFlag == 3) filteredVacuums.add(vacuumControl);
        }
        return filteredVacuums;
    }

    @Transactional
    public Collection<ErrorMessage> findAllErrorsForUser(String userMail){
        return errorMessageRepository.findAllByVacuumControl_AddedBy(userRepository.findByMail(userMail));
    }

    @Override
    @Transactional
    public void removeVacuumControl(Long id) {
        System.err.println("remove vacuumControl");
        Optional<VacuumControl> optionalVacuumControl = this.findById(id);
        if (optionalVacuumControl.isPresent()) {
            VacuumControl vacuumControl = optionalVacuumControl.get();
            if (vacuumControl.getStatus() != Status.STOPPED) return;
            vacuumControl.setActive(false);
            vacuumControlRepository.save(vacuumControl);
        }
    }

    @Override
    @Async
    @Transactional
    public void startVacuumControl(Long id, boolean scheduled) throws InterruptedException {
        Optional<VacuumControl> optionalVacuumControl = vacuumControlRepository.findById(id);
        if(optionalVacuumControl.isPresent()) {
            VacuumControl vacuumControl = optionalVacuumControl.get();
            if(vacuumControl.isActive()) {
                if (vacuumControl.getStatus() == Status.STOPPED) {
                    System.err.println("Starting vacuumControl");
                    Thread.sleep((long) (Math.random() * (20000 - 15000) + 15000));
                    vacuumControl.setStatus(Status.RUNNING);
                    vacuumControlRepository.save(vacuumControl);
                    System.err.println("VacuumControl started");
                } else
                if(scheduled)
                    errorMessageRepository.save(new ErrorMessage(0L, "The vacuum's status is not 'STOPPED'.", "START", LocalDate.now(), vacuumControl));
            } else
            if(scheduled)
                errorMessageRepository.save(new ErrorMessage(0L, "The vacuum control is deactivated.", "START",LocalDate.now(), vacuumControl));
        }
    }

    @Override
    @Async
    @Transactional
    public void stopVacuumControl(Long id, boolean scheduled) throws InterruptedException {
        Optional<VacuumControl> optionalVacuumControl = vacuumControlRepository.findById(id);
        if(optionalVacuumControl.isPresent()) {
            VacuumControl vacuumControl = optionalVacuumControl.get();
            if(vacuumControl.isActive()) {
                if (vacuumControl.getStatus() == Status.RUNNING) {
                    System.err.println("Stopping vacuumControl");
                    Thread.sleep((long) (Math.random() * (20000 - 15000) + 15000));
                    vacuumControl.setStatus(Status.STOPPED);
                    vacuumControlRepository.save(vacuumControl);
                    System.err.println("VacuumControl stopped");
                } else
                if(scheduled)
                    errorMessageRepository.save(new ErrorMessage(0L, "The vacuum's status is not 'RUNNING'.", "STOP", LocalDate.now(), vacuumControl));
            } else
            if(scheduled)
                errorMessageRepository.save(new ErrorMessage(0L, "The vacuum control is deactivated.", "STOP",LocalDate.now(), vacuumControl));
        }
    }

    @Override
    @Async
    @Transactional
    public void dischargeVacuumControl(Long id, boolean scheduled) throws InterruptedException, ParseException {
        Optional<VacuumControl> optionalVacuumControl = vacuumControlRepository.findById(id);
        if(optionalVacuumControl.isPresent()) {
            VacuumControl vacuumControl = optionalVacuumControl.get();
            if(vacuumControl.isActive()) {
                if (vacuumControl.getStatus() == Status.STOPPED) {
                    System.err.println("Stopping machine for discharge");
                    Thread.sleep((long) (Math.random() * (20000 - 15000) + 15000));
                    vacuumControl.setStatus(Status.DISCHARGING);
                    vacuumControlRepository.save(vacuumControl);

                    vacuumControl = this.findById(id).get();

                    System.err.println("Starting machine for discharge");
                    Thread.sleep((long) (Math.random() * (20000 - 15000) + 15000));
                    vacuumControl.setStatus(Status.STOPPED);
                    vacuumControlRepository.save(vacuumControl);
                    System.err.println("VacuumControl discharge");
                } else
                if(scheduled)
                    errorMessageRepository.save(new ErrorMessage(0L, "The vacuum's status is not 'STOPPED'.", "DISCHARGE", LocalDate.now(), vacuumControl));
            } else
            if(scheduled)
                errorMessageRepository.save(new ErrorMessage(0L, "The vacuum control is deactivated.", "DISCHARGE",LocalDate.now(), vacuumControl));
        }
    }

    @Override
    @Transactional
    public void scheduleVacuumControl(Long id, String date, String time, String action) throws ParseException {
        Date date1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(date + " " + time);
        System.err.println("Machine scheduled for " + date1);

        this.taskScheduler.schedule(() -> {
            try {
                switch (action) {
                    case "Start":
                        startVacuumControl(id, true);
                        break;
                    case "Stop":
                        stopVacuumControl(id, true);
                        break;
                    case "Discharge":
                        dischargeVacuumControl(id, true);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, date1);
    }
}
