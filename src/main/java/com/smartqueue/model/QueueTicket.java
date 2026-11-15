package com.smartqueue.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name="queue_tickets")
public class QueueTicket {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false) private String customerName;
  @Column(nullable=false, unique=true) private String tokenNumber;
  @Enumerated(EnumType.STRING) @Column(nullable=false) private TicketStatus status = TicketStatus.WAITING;
  @Column(nullable=false) private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt = LocalDateTime.now();
  public QueueTicket() {}
  public QueueTicket(String customerName, String tokenNumber) { this.customerName=customerName; this.tokenNumber=tokenNumber; }
  @PreUpdate public void touch(){ updatedAt=LocalDateTime.now(); }
  public Long getId(){return id;} public String getCustomerName(){return customerName;}
  public String getTokenNumber(){return tokenNumber;} public TicketStatus getStatus(){return status;}
  public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getUpdatedAt(){return updatedAt;}
  public void setStatus(TicketStatus status){this.status=status;}
}
