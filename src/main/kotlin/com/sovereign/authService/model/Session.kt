package com.sovereign.authService.model

import javax.persistence.Entity
import javax.persistence.Id
import javax.validation.constraints.NotBlank

@Entity
data class Session (
        @Id
        @NotBlank
        val token: String = "",

        @NotBlank
        val username: String ="",

        @NotBlank
        val expires: Long = 0
)