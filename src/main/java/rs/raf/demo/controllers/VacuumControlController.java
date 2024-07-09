package rs.raf.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.raf.demo.model.VacuumControl;
import rs.raf.demo.model.enums.Status;
import rs.raf.demo.repositories.VacuumControlRepository;
import rs.raf.demo.requests.AddRequest;
import rs.raf.demo.requests.ScheduleRequest;
import rs.raf.demo.services.UserService;
import rs.raf.demo.services.VacuumControlService;

import javax.websocket.server.PathParam;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/api/vacuums")
public class VacuumControlController {
    private final VacuumControlService vacuumControlService;
    private final UserService userService;
    private final VacuumControlRepository vacuumControlRepository;

    @Autowired
    public VacuumControlController(VacuumControlService vacuumControlService, UserService userService, VacuumControlRepository vacuumControlRepository) {
        this.vacuumControlService = vacuumControlService;
        this.userService = userService;
        this.vacuumControlRepository = vacuumControlRepository;
    }

    @GetMapping(value = "/get_filtered", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<VacuumControl>> getVacuumControlFiltered(
            @PathParam("mail") String mail,
            @PathParam("name") String name,
            @PathParam("status") String status,
            @PathParam("dateFrom") String dateFrom,
            @PathParam("dateTo") String dateTo){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parsedFrom = null;
        LocalDate parsedTo = null;
        List<String> vacuumControlFilterStatuses = null;

        if (dateFrom != null) parsedFrom = LocalDate.parse(dateFrom, formatter);
        if (dateTo != null) parsedTo = LocalDate.parse(dateTo, formatter);
        if (status != null) vacuumControlFilterStatuses = new ArrayList<>(Arrays.asList(status.split(",")));

        return ResponseEntity.ok().body(vacuumControlService.searchVacuums(name, vacuumControlFilterStatuses, parsedFrom, parsedTo, mail));
    }
    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<VacuumControl>> getVacuumsByUser(@PathParam("mail") String mail){
        return ResponseEntity.ok().body(vacuumControlService.getVacuumsByUser(mail));
    }

    @PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public VacuumControl addVacuumControl(@RequestBody AddRequest addRequest){
        System.err.println(addRequest.getName() + " --- " + addRequest.getMail());
        return vacuumControlService.addVacuumControl(addRequest.getName(), addRequest.getMail());
    }

    @DeleteMapping(value = "/remove/{id}")
    private ResponseEntity<?> removeVacuumControl(@PathVariable("id") Long id){
        vacuumControlService.removeVacuumControl(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/start/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> startVacuumControl(@PathVariable Long id) throws InterruptedException {

        Optional<VacuumControl> optionalMachine = vacuumControlService.findById(id);

        if(optionalMachine.isPresent() && optionalMachine.get().getStatus() == Status.STOPPED) {
            vacuumControlService.startVacuumControl(id, false);
            return ResponseEntity.ok(optionalMachine.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/stop/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> stopVacuumControl(@PathVariable Long id) throws InterruptedException {

        Optional<VacuumControl> optionalMachine = vacuumControlService.findById(id);

        if(optionalMachine.isPresent() && optionalMachine.get().getStatus() == Status.RUNNING) {
            vacuumControlService.stopVacuumControl(id, false);
            return ResponseEntity.ok(optionalMachine.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/discharge/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> dischargeVacuumControl(@PathVariable Long id) throws InterruptedException, ParseException {

        Optional<VacuumControl> optionalMachine = vacuumControlService.findById(id);

        if(optionalMachine.isPresent() && optionalMachine.get().getStatus() == Status.RUNNING) {
            vacuumControlService.dischargeVacuumControl(id, false);
            return ResponseEntity.ok(optionalMachine.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/schedule")
    public ResponseEntity<?> scheduleVacuumControl(@RequestBody ScheduleRequest scheduleRequest) throws ParseException {
        vacuumControlService.scheduleVacuumControl(scheduleRequest.getId(),scheduleRequest.getDate(),scheduleRequest.getTime(),scheduleRequest.getAction());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/errors", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getErrorHistory(@PathParam("mail") String mail){
        return ResponseEntity.ok(vacuumControlService.findAllErrorsForUser(mail));
    }

}
