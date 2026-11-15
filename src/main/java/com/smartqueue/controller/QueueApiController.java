package com.smartqueue.controller;
import com.smartqueue.model.*;
import com.smartqueue.service.QueueService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController
@RequestMapping("/api")
public class QueueApiController {
 private final QueueService service;
 public QueueApiController(QueueService service){this.service=service;}
 @GetMapping("/tickets") public List<QueueTicket> all(){return service.all();}
 @GetMapping("/stats") public Map<String,Object> stats(){return service.stats();}
 @PostMapping("/tickets") public ResponseEntity<?> create(@RequestBody Map<String,String> body){
   try{return ResponseEntity.status(HttpStatus.CREATED).body(service.create(body.get("customerName")));}
   catch(IllegalArgumentException e){return ResponseEntity.badRequest().body(Map.of("error",e.getMessage()));}
 }
 @GetMapping("/tickets/{token}") public ResponseEntity<?> track(@PathVariable String token){
   return service.byToken(token).<ResponseEntity<?>>map(ResponseEntity::ok).orElseGet(()->ResponseEntity.status(404).body(Map.of("error","Token not found.")));
 }
 @PostMapping("/call-next") public ResponseEntity<?> callNext(){
   try{return ResponseEntity.ok(service.callNext());}
   catch(IllegalStateException e){return ResponseEntity.badRequest().body(Map.of("error",e.getMessage()));}
 }
 @PatchMapping("/tickets/{id}/status") public ResponseEntity<?> status(@PathVariable Long id,@RequestBody Map<String,String> body){
   try{return ResponseEntity.ok(service.changeStatus(id,TicketStatus.valueOf(body.get("status"))));}
   catch(Exception e){return ResponseEntity.badRequest().body(Map.of("error",e.getMessage()==null?"Invalid request.":e.getMessage()));}
 }
}
