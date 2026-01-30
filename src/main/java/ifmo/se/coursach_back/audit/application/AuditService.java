package ifmo.se.coursach_back.audit.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ifmo.se.coursach_back.shared.domain.Account;
import ifmo.se.coursach_back.audit.domain.AuditEvent;
import ifmo.se.coursach_back.shared.application.ports.AccountRepositoryPort;
import ifmo.se.coursach_back.audit.application.ports.AuditEventRepositoryPort;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final AuditEventRepositoryPort auditEventRepository;
    private final AccountRepositoryPort accountRepository;
    private final ObjectMapper objectMapper;

    public void log(UUID accountId, String action, String entityType, UUID entityId, Map<String, Object> metadata) {
        AuditEvent event = new AuditEvent();
        if (accountId != null) {
            Account account = accountRepository.findById(accountId).orElse(null);
            event.setAccount(account);
        }
        event.setAction(action);
        event.setEntityType(entityType);
        event.setEntityId(entityId);
        if (metadata != null && !metadata.isEmpty()) {
            event.setMetadataJson(toJson(metadata));
        }
        auditEventRepository.save(event);
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
