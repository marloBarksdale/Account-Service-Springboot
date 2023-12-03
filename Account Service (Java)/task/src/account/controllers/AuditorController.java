package account.controllers;

import account.repositories.EventRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuditorController {

    private EventRepository eventRepository;

    public AuditorController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }


    @GetMapping("/api/security/**")
    public ResponseEntity<?> showSecurityEvents() {


        return ResponseEntity.ok(eventRepository.findAll());


    }
}
