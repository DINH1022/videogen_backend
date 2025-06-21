package com.suplerteam.video_creator.response.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.suplerteam.video_creator.DTO.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("user_name")
    private String username;
    @JsonProperty("email")
    private String email;
    @JsonProperty("avatar")
    private String avatar;
    @JsonProperty("created_at")
    private Date createdAt;
    @JsonProperty("role")
    private String role;

    public static UserResponse createFromDTO(UserDTO dto){
        return UserResponse.builder()
                .id(dto.getId())
                .fullName(dto.getFullName())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .avatar(dto.getAvatar())
                .createdAt(dto.getCreatedAt())
                .role(dto.getRole())
                .build();
    }
}
