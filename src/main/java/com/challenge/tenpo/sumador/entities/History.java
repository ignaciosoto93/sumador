package com.challenge.tenpo.sumador.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private int firstValue;

    @Column(nullable = false)
    private int secondValue;

    @Column(nullable = false)
    private Double result;

    @Column(nullable = false)
    private Double percentage;

    @PrePersist
    public void setTimestamp() {
        this.timestamp = LocalDateTime.now();
    }


}
