package bob.e2e.controller

import bob.e2e.dto.KeypadResponseDto
import bob.e2e.dto.UserInputRequestDto
import bob.e2e.service.KeypadService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class KeypadController(private val keypadService: KeypadService) {

    @GetMapping("/keypad")
    fun getKeypad(): KeypadResponseDto {
        return keypadService.makeKeypad()
    }

    @PostMapping("/submit")
    fun submitUserInput(@RequestBody userDataRequest: UserInputRequestDto): Any {
        return try {
            ResponseEntity("Data processed successfully", HttpStatus.OK)
            return keypadService.processUserInput(userDataRequest)
        } catch (e: Exception) {
            ResponseEntity("Failed to process data", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}