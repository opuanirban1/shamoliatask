package com.dev.aes.payloads.response;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FolderShareUserResponseDto {
    private Long id;
    private String email;
    private String username;
}
