package com.rap.support.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners({AuditingEntityListener.class})
public class AuditEntity {

    @CreatedDate
    @Column(name = "create_dtm", updatable = false)
    protected LocalDateTime createDtm;

    @Column(name = "create_user_id", updatable = false)
    protected String createUserId;

    @LastModifiedDate
    @Column(name = "last_update_dtm")
    protected LocalDateTime lastUpdateDtm;

    @Column(name = "last_update_user_id")
    protected String lastUpdateUserId;

}

