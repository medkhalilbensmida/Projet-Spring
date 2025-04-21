package tn.fst.spring.projet_spring.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.fst.spring.projet_spring.dto.forum.MessageRequestDTO;
import tn.fst.spring.projet_spring.dto.forum.MessageResponseDTO;
import tn.fst.spring.projet_spring.model.auth.User;
import tn.fst.spring.projet_spring.model.forum.Message;
import tn.fst.spring.projet_spring.repositories.auth.UserRepository;
import tn.fst.spring.projet_spring.repositories.forum.MessageRepository;
import tn.fst.spring.projet_spring.services.interfaces.IMessageService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements IMessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    @Override
    public MessageResponseDTO saveMessage(MessageRequestDTO request) {
        User sender = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new RuntimeException("Expéditeur non trouvé"));

        User recipient = userRepository.findById(request.getRequesterId())
                .orElseThrow(() -> new RuntimeException("Destinataire non trouvé"));

        Message message = new Message();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setMessage(request.getMessage());
        message.setTimestamp(LocalDateTime.now());

        Message saved = messageRepository.save(message);

        return MessageResponseDTO.builder()
                .id(saved.getId())
                .message(saved.getMessage())
                .timestamp(saved.getTimestamp())
                .senderUsername(sender.getUsername())
                .recipientId(recipient.getId())
                .build();
    }
}
