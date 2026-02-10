package com.constructflow.entity_service.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@SQLRestriction("active = true")
public class Company
        extends BaseEntity
{
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, updatable = false)
    private String code;

    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.ALL}, orphanRemoval = true)
    @Fetch(value = FetchMode.JOIN)
    private Address address;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
}
