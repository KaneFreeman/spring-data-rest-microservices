package kanefreeman.jpa.repositories;

import kanefreeman.jpa.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface TicketRepository extends JpaRepository<Ticket, Integer>, QuerydslPredicateExecutor<Ticket> {
}
