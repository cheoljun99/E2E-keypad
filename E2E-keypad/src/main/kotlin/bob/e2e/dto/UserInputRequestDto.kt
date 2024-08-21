package bob.e2e.dto

data class UserInputRequestDto(
    val keypadId: String,
    val userInput: String? // nullable로 변경
)