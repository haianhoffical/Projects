package com.dhvestudent.controller;

import com.dhvestudent.dto.*;
import com.dhvestudent.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/rooms")
    public ResponseEntity<?> getRooms(@RequestParam Long userId) {
        return ResponseEntity.ok(ApiResponse.success(chatService.getUserChatRooms(userId)));
    }

    @PostMapping("/rooms")
    public ResponseEntity<?> createRoom(@RequestParam Long userId,
                                         @RequestParam Long productId,
                                         @RequestParam Long sellerId) {
        return ResponseEntity.ok(ApiResponse.success(chatService.createOrGetRoom(userId, productId, sellerId)));
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<?> getMessages(@PathVariable Long roomId) {
        return ResponseEntity.ok(ApiResponse.success(chatService.getMessages(roomId)));
    }

    @PostMapping("/rooms/{roomId}/messages")
    public ResponseEntity<?> sendMessage(@PathVariable Long roomId,
                                          @RequestParam Long senderId,
                                          @RequestBody ChatMessageRequest req) {
        return ResponseEntity.ok(ApiResponse.success(chatService.sendMessage(roomId, senderId, req.getContent())));
    }

    @PostMapping("/rooms/{roomId}/read")
    public ResponseEntity<?> markRead(@PathVariable Long roomId, @RequestParam Long userId) {
        chatService.markAsRead(roomId, userId);
        return ResponseEntity.ok(ApiResponse.success("Đã đánh dấu đã đọc", null));
    }
}
