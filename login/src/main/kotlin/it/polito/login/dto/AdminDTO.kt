package it.polito.login.dto

data class AdminDTO(
    val id:Long?,
    val nickname:String,
    val email:String,
    val password: String,
    val active: Boolean,
    val recruiter: Boolean
)

fun toUserDTO(user: AdminDTO): UserDTO {
    return UserDTO(user.id, user.nickname, user.email, user.password, user.active)
}
