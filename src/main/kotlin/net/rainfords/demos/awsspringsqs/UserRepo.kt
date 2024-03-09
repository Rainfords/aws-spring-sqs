package net.rainfords.demos.awsspringsqs

import org.springframework.stereotype.Repository
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Predicate

@JvmRecord
data class User(val id: String, val name: String, val email: String)

@JvmRecord
data class UserCreatedEvent(val id: String, val username: String, val email: String)

@Repository
class UserRepository {
    private val persistedUsers: MutableMap<String, User> = ConcurrentHashMap<String, User>()

    fun save(userToSave: User) {
        persistedUsers[userToSave.id] = userToSave
    }

    fun findById(userId: String): Optional<User> {
        return Optional.ofNullable(persistedUsers[userId])
    }

    fun findByName(name: String?): Optional<User> {
        return persistedUsers.values.stream()
            .filter(Predicate<User> { user: User -> user.name.equals(name) })
            .findFirst()
    }
}
