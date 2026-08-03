package com.dhvestudent.service;

import com.dhvestudent.entity.*;
import com.dhvestudent.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ChatService {

    @Autowired private ChatRoomRepository roomRepository;
    @Autowired private ChatMessageRepository messageRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public List<ChatRoom> getUserChatRooms(Long userId) {
        return roomRepository.findByMemberId(userId);
    }

    @Transactional
    public ChatRoom createOrGetRoom(Long userId, Long productId, Long sellerId) {
        User buyer = userRepository.findById(userId).orElseThrow();
        User seller = userRepository.findById(sellerId).orElseThrow();
        Product product = productRepository.findById(productId).orElse(null);

        ChatRoom room = roomRepository.findByProductAndTwoMembers(productId).orElse(null);
        if (room == null) {
            room = ChatRoom.builder().product(product).build();
            roomRepository.save(room);

            ChatRoomMember m1 = new ChatRoomMember(); m1.setRoom(room); m1.setUser(buyer);
            ChatRoomMember m2 = new ChatRoomMember(); m2.setRoom(room); m2.setUser(seller);
            room.getMembers().add(m1);
            room.getMembers().add(m2);
            roomRepository.save(room);
        }
        return room;
    }

    @Transactional
    public ChatMessage sendMessage(Long roomId, Long senderId, String content) {
        ChatRoom room = roomRepository.findById(roomId).orElseThrow();
        User sender = userRepository.findById(senderId).orElseThrow();

        ChatMessage msg = ChatMessage.builder()
                .room(room)
                .sender(sender)
                .content(content)
                .build();
        ChatMessage saved = messageRepository.save(msg);

        room.getMembers().stream()
            .filter(m -> !m.getUser().getId().equals(senderId))
            .forEach(m -> {
                Notification notif = Notification.builder()
                        .user(m.getUser())
                        .type("CHAT")
                        .title("Tin nhắn mới")
                        .content(sender.getFullName() + ": " + content)
                        .linkUrl("/chat?room=" + roomId)
                        .build();
                notificationRepository.save(notif);
            });

        return saved;
    }

    @Transactional(readOnly = true)
    public List<ChatMessage> getMessages(Long roomId) {
        return messageRepository.findByRoomIdOrderByCreatedAtAsc(roomId);
    }

    @Transactional
    public void markAsRead(Long roomId, Long userId) {
        List<ChatMessage> msgs = messageRepository.findByRoomIdOrderByCreatedAtAsc(roomId);
        msgs.stream()
            .filter(m -> !m.getSender().getId().equals(userId) && !m.getIsRead())
            .forEach(m -> { m.setIsRead(true); messageRepository.save(m); });
    }
}
