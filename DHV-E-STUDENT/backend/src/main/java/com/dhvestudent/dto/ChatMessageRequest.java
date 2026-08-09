package com.dhvestudent.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChatMessageRequest {
    @NotNull(message = "Room ID không được để trống")
    private Long roomId;

    @NotBlank(message = "Nội dung không được để trống")
    private String content;
}
