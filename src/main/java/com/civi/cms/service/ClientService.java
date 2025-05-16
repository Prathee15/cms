package com.civi.cms.service;

import com.civi.cms.model.Client;
import com.civi.cms.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public ResponseEntity<?> getAllClients() {
        List<Client> clients = clientRepository.findAll();
        return new ResponseEntity<>(clients, HttpStatus.OK);
    }

    public ResponseEntity<?> getClientById(Long id) {
        Optional<Client> client = clientRepository.findById(id);
        if (client.isPresent()) {
            return new ResponseEntity<>(client.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Client not found with id: " + id, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> createClient(Client client) {
        try {
            Optional<Client> existingByEmail = clientRepository.findByEmail(client.getEmail());
            if (existingByEmail.isPresent()) {
                return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST);
            }
            Optional<Client> existingByMobile = clientRepository.findByPhoneNumber(client.getPhoneNumber());
            if (existingByMobile.isPresent()) {
                return new ResponseEntity<>("Mobile number already exists", HttpStatus.BAD_REQUEST);
            }
            Client savedClient = clientRepository.save(client);
            return new ResponseEntity<>(savedClient, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>("Error creating client: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> updateClient(Long id, Client clientDetails) {
        Optional<Client> existingClientOpt = clientRepository.findById(id);
        if (existingClientOpt.isPresent()) {
            clientDetails.setClientId(id);
            Client updatedClient = clientRepository.save(clientDetails);
            return new ResponseEntity<>(updatedClient, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Client not found with id: " + id, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> getClientByEmail(String emailId) {
        Optional<Client> client = clientRepository.findByEmail(emailId);
        if (client.isPresent()) {
            return new ResponseEntity<>(client.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Client not found with email: " + emailId, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> getCaseByClientId(Long id) {
        Optional<Client> client = clientRepository.findById(id);
        if (client.isPresent()) {
            return new ResponseEntity<>(client.get().getCaseDetailsList(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Client not found with id: " + id, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> getCaseByClientEmail(String email) {
        Optional<Client> client = clientRepository.findByEmail(email);
        if (client.isPresent()) {
            return new ResponseEntity<>(client.get().getCaseDetailsList(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Client not found with email: " + email, HttpStatus.NOT_FOUND);
        }
    }
}
