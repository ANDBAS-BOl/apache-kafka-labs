package com.appandres.emailnotification.infrastructure.out.jpa.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "processed-events")
@Data
@NoArgsConstructor
public class ProcessedEventEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public ProcessedEventEntity(String messageId, String productId) {
        this.messageId = messageId;
        this.productId = productId;
    }

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String messageId;

    @Column(nullable = false)
    private String productId;

}
