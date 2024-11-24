package com.dev.aes.payloads.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddToDictionaryDto {
    @NotBlank
    @Size(max = 1000)
    private String correctWord;
    @NotBlank
    @Size(max = 1000)
    private String wrongWord;
    @NotNull
    private Long fileId;
}
