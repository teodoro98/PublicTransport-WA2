package it.polito.login.dto

data class TurnstileDTO(
    val id:Long?,
    val nickname:String,
    val password: String,
    val active: Boolean
) {
    companion object {
        fun toUserDTO(user: TurnstileDTO, email: String): UserDTO {
            return UserDTO(user.id, user.nickname, email, user.password, user.active)
        }
    }
}


