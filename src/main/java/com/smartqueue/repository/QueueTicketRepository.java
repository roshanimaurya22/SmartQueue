package com.smartqueue.repository;
import com.smartqueue.model.QueueTicket;
import com.smartqueue.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface QueueTicketRepository extends JpaRepository<QueueTicket,Long> {
  List<QueueTicket> findAllByOrderByCreatedAtAsc();
  List<QueueTicket> findByStatusOrderByCreatedAtAsc(TicketStatus status);
  Optional<QueueTicket> findFirstByStatusOrderByCreatedAtAsc(TicketStatus status);
  long countByStatus(TicketStatus status);
  boolean existsByTokenNumber(String tokenNumber);
}
