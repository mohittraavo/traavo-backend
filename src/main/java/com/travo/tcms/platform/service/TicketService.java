package com.travo.tcms.platform.service;

import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.travo.tcms.platform.dto.TicketRecord;
import com.travo.tcms.platform.dto.TicketStatusUpdateRequest;
import com.travo.tcms.platform.entity.TicketEntity;
import com.travo.tcms.platform.repository.TicketRepository;

@Service
public class TicketService {

  private final TicketRepository ticketRepository;

  public TicketService(TicketRepository ticketRepository) {
    this.ticketRepository = ticketRepository;
  }

  public List<TicketRecord> listTickets() {
    return ticketRepository.findAllByOrderByCreatedAtDesc().stream().map(entity ->
        new TicketRecord(entity.id, entity.tenantId, entity.campusName, entity.subject, entity.description,
            entity.priority, entity.status, entity.resolutionNote, entity.createdAt, entity.updatedAt)).toList();
  }

  public TicketRecord updateTicket(long ticketId, TicketStatusUpdateRequest request) {
    TicketEntity entity = ticketRepository.findById(ticketId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));
    entity.status = request.status();
    entity.resolutionNote = request.resolutionNote();
    entity.updatedAt = Instant.now();
    ticketRepository.save(entity);
    return new TicketRecord(entity.id, entity.tenantId, entity.campusName, entity.subject, entity.description,
        entity.priority, entity.status, entity.resolutionNote, entity.createdAt, entity.updatedAt);
  }
}
