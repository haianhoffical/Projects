package com.dhvestudent.dto;

import com.dhvestudent.entity.ForumPost;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ForumPostRequest {
    @NotNull(message = "Chủ đề không được để trống")
    private ForumPost.ForumCategory category;

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    @NotBlank(message = "Nội dung không được để trống")
    private String content;
}
