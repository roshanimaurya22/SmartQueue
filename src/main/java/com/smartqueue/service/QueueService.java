package com.smartqueue.service;
import com.smartqueue.model.*;
import com.smartqueue.repository.QueueTicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
@Service
public class QueueService {
  private final QueueTicketRepository repo;
  public QueueService(QueueTicketRepository repo){this.repo=repo;}
  public synchronized QueueTicket create(String name) {
    String clean=name==null?"":name.trim();
    if(clean.length()<2 || clean.length()>60) throw new IllegalArgumentException("Name must contain 2 to 60 characters.");
    String prefix="SQ-"+LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)+"-";
    long sequence=repo.count()+1;
    String token=prefix+String.format("%03d",sequence);
    while(repo.existsByTokenNumber(token)) { sequence++; token=prefix+String.format("%03d",sequence); }
    return repo.save(new QueueTicket(clean,token));
  }
  public List<QueueTicket> all(){return repo.findAllByOrderByCreatedAtAsc();}
  public Optional<QueueTicket> find(Long id){return repo.findById(id);}
  public Optional<QueueTicket> byToken(String token){return repo.findAll().stream().filter(t->t.getTokenNumber().equalsIgnoreCase(token)).findFirst();}
  public Map<String,Object> stats(){
    Map<String,Object> m=new LinkedHashMap<>();
    m.put("total",repo.count()); m.put("waiting",repo.countByStatus(TicketStatus.WAITING));
    m.put("serving",repo.countByStatus(TicketStatus.SERVING)); m.put("completed",repo.countByStatus(TicketStatus.COMPLETED));
    m.put("skipped",repo.countByStatus(TicketStatus.SKIPPED)); m.put("cancelled",repo.countByStatus(TicketStatus.CANCELLED));
    m.put("current",repo.findFirstByStatusOrderByCreatedAtAsc(TicketStatus.SERVING).orElse(null));
    return m;
  }
  @Transactional public synchronized QueueTicket callNext(){
    repo.findFirstByStatusOrderByCreatedAtAsc(TicketStatus.SERVING).ifPresent(t->t.setStatus(TicketStatus.COMPLETED));
    QueueTicket next=repo.findFirstByStatusOrderByCreatedAtAsc(TicketStatus.WAITING).orElseThrow(()->new IllegalStateException("There are no waiting customers."));
    next.setStatus(TicketStatus.SERVING); return repo.save(next);
  }
  @Transactional public QueueTicket changeStatus(Long id, TicketStatus status){
    QueueTicket t=repo.findById(id).orElseThrow(()->new NoSuchElementException("Ticket not found."));
    if(t.getStatus()==TicketStatus.COMPLETED || t.getStatus()==TicketStatus.CANCELLED || t.getStatus()==TicketStatus.SKIPPED)
      throw new IllegalStateException("This ticket is already closed.");
    if(status==TicketStatus.SERVING && repo.countByStatus(TicketStatus.SERVING)>0)
      throw new IllegalStateException("Complete the current serving ticket before calling another.");
    t.setStatus(status); return repo.save(t);
  }
}
