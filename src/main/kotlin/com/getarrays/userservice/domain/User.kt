package com.getarrays.userservice.domain

import lombok.Data
import javax.persistence.*

@Entity
@Data
data class User (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long?,
    var name: String,
    var username: String,
    var password: String,
    @ManyToMany(fetch = FetchType.EAGER)
    var role: MutableList<Role>,
)
