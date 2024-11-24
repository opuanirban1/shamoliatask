package com.dev.aes.payloads.response;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class AllDocTypeFieldTypeResponse {

    private Long id;


    private String typevalue;


    private String status;


    private String create_at;


    private String update_at;

}
