package com.sovereign.authService.repository

import com.sovereign.authService.model.Session
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SessionRepository : JpaRepository<Session, String>{
    //fun findByExpiresLessThan(numb:Long):MutableSet<Session>

    fun deleteAllByExpiresLessThan(numb:Long)

    fun deleteAllByUsername(username:String)
}