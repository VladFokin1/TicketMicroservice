package org.example.service;

import org.example.dto.TicketFilterDto;
import org.example.model.Ticket;
import org.example.repository.TicketRepository;
import org.example.repository.UserRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public TicketService(TicketRepository ticketRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
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
    }

    public List<Ticket> getPurchasedTickets(Long userId) {
        // Проверка существования пользователя
        userRepository.findById(userId)
                .orElseThrow(() -> new EmptyResultDataAccessException("Пользователь не найден", 1));

        return ticketRepository.findByUserId(userId);
    }
}
