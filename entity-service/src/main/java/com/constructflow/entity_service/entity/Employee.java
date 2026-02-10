package com.constructflow.entity_service.entity;

import com.constructflow.entity_service.enums.EmployeeRole;
import com.constructflow.entity_service.enums.EmployeeType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
public class Employee
        extends BaseEntity
{
    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private Double payRate;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    @Builder.Default
    private EmployeeType employeeType = EmployeeType.HOURLY;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    @Builder.Default
    private EmployeeRole employeeRole = EmployeeRole.WORKER;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.ALL}, orphanRemoval = true)
    @Fetch(value = FetchMode.JOIN)
    private Address address;

    @Column(nullable = false, unique = true, updatable = false)
    private Long userId;
}
