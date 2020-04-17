package com.sovereign.authService.model


import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference
import com.fasterxml.jackson.annotation.JsonProperty
import javax.persistence.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size


@Entity
data class Account (
        @Id
        @Column(name = "username", unique = true, nullable = false)
        @NotNull
        @Size(min = 3, max = 32)
        var username: String = "",

        val alias: String? = null,

        @Size(min = 8)
        @get:JsonIgnore
        @set:JsonProperty
        var password: String = "",

        @get: JsonIgnore
        val salt: String = ""
){
       constructor(username: String) : this(username=username, salt = "123asdfghjklqwertyuiozxcvbnm")
}

