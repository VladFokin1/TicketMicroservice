package org.example.service;

import org.example.dto.ticket.TicketCreateDto;
import org.example.dto.ticket.TicketFilterDto;
import org.example.dto.ticket.TicketPurchaseEvent;
import org.example.dto.ticket.TicketUpdateDto;
import org.example.exception.NotFoundException;
import org.example.model.Ticket;
import org.example.repository.TicketRepository;
import org.example.repository.UserRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketCacheService ticketCacheService;
    private final KafkaTemplate<String, TicketPurchaseEvent> kafkaTemplate;

    public TicketService(TicketRepository ticketRepository,
                         UserRepository userRepository,
                         TicketCacheService ticketCacheService,
                         KafkaTemplate<String, TicketPurchaseEvent> kafkaTemplate) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.ticketCacheService = ticketCacheService;
        this.kafkaTemplate = kafkaTemplate;
    }

    public Page<Ticket> getAvailableTickets(TicketFilterDto filter, int page, int size) {
        // Проверка существования пользователя (если передан в фильтре)
        if (filter.getUserId() != null) {
            userRepository.findById(filter.getUserId())
                    .orElseThrow(() -> new EmptyResultDataAccessException("Пользователь не найден", 1));
        }

        List<Ticket> tickets = ticketRepository.findAvailableTickets(filter, page, size);
        return new PageImpl<>(tickets, PageRequest.of(page, size), tickets.size());
    }

    @Transactional
    public void purchaseTicket(Long ticketId, Long userId) {
        // Проверка существования пользователя
        userRepository.findById(userId)
                .orElseThrow(() -> new EmptyResultDataAccessException("Пользователь не найден", 1));

        // Получение билета
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EmptyResultDataAccessException("Билет не найден", 1));

        // Проверка доступности билета
        if (ticket.getUserId() != null) {
            throw new IllegalStateException("Билет уже куплен");
        }

        // Обновление билета
        ticketRepository.updateUserId(ticketId, userId);
        ticketCacheService.evictUserCache(userId);

        // Создаем событие для Kafka
        TicketPurchaseEvent event = new TicketPurchaseEvent();
        event.setTicketId(ticketId);
        event.setUserId(userId);
        event.setPrice(ticket.getPrice());
        event.setRouteId(ticket.getRouteId().longValue());
        event.setDateTime(ticket.getDateTime());
        event.setSeatNumber(ticket.getSeatNumber());

        kafkaTemplate.send("ticket-purchases", event);
    }

    public List<Ticket> getPurchasedTickets(Long userId) {
        // Проверка существования пользователя
        userRepository.findById(userId)
                .orElseThrow(() -> new EmptyResultDataAccessException("Пользователь не найден", 1));

        //проверка в кэше
        List<Ticket> cachedTickets = ticketCacheService.getCachedTickets(userId);
        if (!cachedTickets.isEmpty()) {
            return cachedTickets;
        }

        //загрузка из бд и кэширование
        List<Ticket> dbTickets = ticketRepository.findByUserId(userId);
        ticketCacheService.cacheTickets(userId, dbTickets);
        return dbTickets;
    }

    public Ticket createTicket(TicketCreateDto dto) {
        Ticket ticket = new Ticket();
        ticket.setRouteId(dto.getRouteId());
        ticket.setDateTime(dto.getDateTime());
        ticket.setSeatNumber(dto.getSeatNumber());
        ticket.setPrice(dto.getPrice());

        ticketRepository.save(ticket);
        return ticket;
    }

    public Ticket updateTicket(Long id, TicketUpdateDto dto) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ticket not found"));

        ticket.setDateTime(dto.getDateTime());
        ticket.setSeatNumber(dto.getSeatNumber());
        ticket.setPrice(dto.getPrice());

        ticketRepository.save(ticket);
        return ticket;
    }

    public void deleteTicket(Long id) {
        if (!ticketRepository.findById(id).isPresent()) {
            throw new NotFoundException("Ticket not found");
        }
        ticketRepository.delete(id);
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.getAll();
    }
}
