package com.getarrays.userservice.domain

import lombok.Data
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
@Data
data class Role (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long?,
    var name: String,
)