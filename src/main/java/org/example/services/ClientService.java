package org.example.services;

import lombok.extern.slf4j.Slf4j;
import org.example.entities.Client;
import org.example.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public void saveAllClients(List<Client> clients) {
        log.info("inside saveAll method");
        clientRepository.saveAll(clients);
    }

    public List<Object[]> calculateAverageSalaryByProfession() {
        return clientRepository.calculateAverageSalaryByProfession();
    }
}
