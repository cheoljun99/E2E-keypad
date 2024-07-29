package bob.e2e.repository

import bob.e2e.model.Keypad
import org.springframework.stereotype.Repository

@Repository
class KeypadRepository {

    fun getKeypad(): Keypad {
        val keys = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
        return Keypad(keys)
    }
}
