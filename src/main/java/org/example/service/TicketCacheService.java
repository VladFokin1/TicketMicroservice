package org.example.service;

import org.example.model.Ticket;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class TicketCacheService {

    // Ключ для Redis: "tickets:user:{userId}"
    private static final String CACHE_KEY_PREFIX = "tickets:user:";

    private final RedisTemplate<String, List<Ticket>> redisTemplate;

    public TicketCacheService(RedisTemplate<String, List<Ticket>> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public List<Ticket> getCachedTickets(Long userId) {
        String key = CACHE_KEY_PREFIX + userId;
        List<Ticket> tickets = redisTemplate.opsForValue().get(key);

        if (tickets != null) {
            return tickets;
        }
        // Если данных нет - возвращаем пустой список
        return Collections.emptyList();
    }

    public void cacheTickets(Long userId, List<Ticket> tickets) {
        String key = CACHE_KEY_PREFIX + userId;

        // Сохраняем на 1 час
        redisTemplate.opsForValue().set(
                key,
                tickets,
                Duration.ofHours(1)
        );
    }

    public void evictUserCache(Long userId) {
        String key = CACHE_KEY_PREFIX + userId;
        redisTemplate.delete(key);
    }

    public void refreshCacheExpiration(Long userId) {
        String key = CACHE_KEY_PREFIX + userId;
        redisTemplate.expire(key, 1, TimeUnit.HOURS);
    }
}
