package com.itau.thecatapi.service.queue;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("local")
@Service
public class LocalQueueService implements QueueService {

    @Override
    public void sendMessage(String message) {
        System.out.println("Mensagem simulada: " + message);
    }
}

