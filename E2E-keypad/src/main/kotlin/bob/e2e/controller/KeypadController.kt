package bob.e2e.controller

import bob.e2e.dto.KeypadDto
import bob.e2e.service.KeypadService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/keypad")
class KeypadController(private val keypadService: KeypadService) {

    @GetMapping
    fun getKeypad(): KeypadDto {
        return keypadService.makeKeypad()
    }
}