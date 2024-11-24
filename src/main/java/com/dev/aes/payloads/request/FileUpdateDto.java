package com.dev.aes.payloads.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FileUpdateDto {
    @NotNull
    private Long id;
    private List<FileContentFieldUpdateDto> fileContentFieldList = new ArrayList<>();
}