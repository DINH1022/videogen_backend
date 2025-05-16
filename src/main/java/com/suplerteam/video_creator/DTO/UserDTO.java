package com.suplerteam.video_creator.DTO;

import com.suplerteam.video_creator.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String fullName;
    private String username;
    private String email;
    private String avatar;
    private Date createdAt;
    private String role;

    public static UserDTO createFromEntity(User entity){
        return UserDTO.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .avatar(entity.getAvatar())
                .createdAt(entity.getCreatedAt())
                .build();

    }
}
