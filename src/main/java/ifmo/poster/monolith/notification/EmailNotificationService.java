package ifmo.poster.monolith.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    public void send(String email, String subject, String body) {
        if (email == null || email.isBlank()) {
            log.warn("Skip email notification: recipient email is empty. subject={}", subject);
            return;
        }
        log.info("EMAIL STUB to={} subject={} body={}", email, subject, body);
    }
}
