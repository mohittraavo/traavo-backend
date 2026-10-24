package com.travo.tcms.platform.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.travo.tcms.platform.dto.TicketRecord;
import com.travo.tcms.platform.dto.TicketStatusUpdateRequest;
import com.travo.tcms.platform.service.TicketService;

@RestController
@RequestMapping("/admin/support")
public class TicketController {
  private final TicketService ticketService;

  public TicketController(TicketService ticketService) {
    this.ticketService = ticketService;
  }

  @GetMapping("/tickets")
  public List<TicketRecord> listTickets() {
    return ticketService.listTickets();
  }

  @PatchMapping("/tickets/{ticketId}")
  public TicketRecord updateTicket(@PathVariable long ticketId,
                                   @Valid @RequestBody TicketStatusUpdateRequest request) {
    return ticketService.updateTicket(ticketId, request);
  }
}
