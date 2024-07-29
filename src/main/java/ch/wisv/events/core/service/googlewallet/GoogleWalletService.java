package ch.wisv.events.core.service.googlewallet;

import ch.wisv.events.core.exception.normal.TicketPassFailedException;
import ch.wisv.events.core.model.ticket.Ticket;

public interface GoogleWalletService {
    /**
     * Get Google Wallet pass for a Ticket.
     * @param ticket of type Ticket.
     * @return A link the user can use to add the ticket to their wallet.
     * @throws TicketPassFailedException when pass is not generated
     */
    String getPass(Ticket ticket) throws TicketPassFailedException;
}
