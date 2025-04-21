package tn.fst.spring.projet_spring.controllers.forum;


import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import tn.fst.spring.projet_spring.dto.forum.MessageRequestDTO;
import tn.fst.spring.projet_spring.dto.forum.MessageResponseDTO;
import tn.fst.spring.projet_spring.services.interfaces.IMessageService;

@Controller
@RequiredArgsConstructor
public class MessageController {
    private final SimpMessagingTemplate messagingTemplate;
    private final IMessageService messageService;

    @MessageMapping("/chat")
    public void processMessage(MessageRequestDTO messageRequest) {
        MessageResponseDTO saved = messageService.saveMessage(messageRequest);
        // 2. Envoyer le message au destinataire via WebSocket
//        String destination = "/user/" + saved.getRecipientUsername() + "/queue/messages";
        messagingTemplate.convertAndSendToUser(saved.getRecipientId().toString(), "/queue/messages", saved);
    }
}
