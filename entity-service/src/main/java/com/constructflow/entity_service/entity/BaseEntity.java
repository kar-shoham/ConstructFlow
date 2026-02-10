package com.constructflow.entity_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@MappedSuperclass
public abstract class BaseEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdOn;

    @Column(nullable = false)
    private LocalDateTime modifiedOn;

    @Column(nullable = false, updatable = false)
    private Long createdBy;

    @Column(nullable = false)
    private Long modifiedBy;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @PrePersist
    public void prePersist()
    {
        this.createdOn = LocalDateTime.now();
        this.modifiedOn = LocalDateTime.now();

        if (Objects.isNull(this.createdBy)) {
            this.createdBy = 1L;
        }
        this.modifiedBy = createdBy;

        if (Objects.isNull(this.active)) {
            this.active = true;
        }
    }

    @PreUpdate
    public void preUpdate()
    {
        if (Objects.isNull(this.modifiedBy)) {
            this.modifiedBy = 1L;
        }
    }
}
