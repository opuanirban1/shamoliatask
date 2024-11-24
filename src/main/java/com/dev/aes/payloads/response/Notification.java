package com.dev.aes.payloads.response;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Notification {
    private String type;
    private Long fileId;
    private Long folderId;
    private Boolean status;
}
