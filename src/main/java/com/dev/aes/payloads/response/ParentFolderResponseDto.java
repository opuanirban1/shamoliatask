package com.dev.aes.payloads.response;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParentFolderResponseDto {
    private Long id;
    private String name;
}
