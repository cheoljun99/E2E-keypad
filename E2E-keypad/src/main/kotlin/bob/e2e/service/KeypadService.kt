package bob.e2e.service

import bob.e2e.dto.KeypadResponse
import bob.e2e.model.Keypad
import bob.e2e.repository.KeypadRepository
import org.springframework.stereotype.Service
import java.security.MessageDigest

@Service
class KeypadService(private val keypadRepository: KeypadRepository) {

    fun getKeypad(): KeypadResponse {
        val keypad = keypadRepository.getKeypad()
        val hashedKeys = keypad.keys.map { hash(it) }
        return KeypadResponse(hashedKeys)
    }

    private fun hash(key: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(key.toByteArray())
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}
