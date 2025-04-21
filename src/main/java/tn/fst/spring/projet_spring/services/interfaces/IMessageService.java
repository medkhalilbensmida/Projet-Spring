package tn.fst.spring.projet_spring.services.interfaces;

import tn.fst.spring.projet_spring.dto.forum.MessageRequestDTO;
import tn.fst.spring.projet_spring.dto.forum.MessageResponseDTO;

public interface IMessageService {
    MessageResponseDTO saveMessage(MessageRequestDTO request);
}
