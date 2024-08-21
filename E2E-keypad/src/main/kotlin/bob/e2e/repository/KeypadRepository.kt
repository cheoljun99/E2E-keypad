package bob.e2e.repository

import bob.e2e.model.KeypadModel
import org.springframework.stereotype.Repository
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import java.util.*

@Repository
class KeypadRepository {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    fun save(keypad: KeypadModel): KeypadModel {
        if (keypad.id == null) {
            entityManager.persist(keypad)
            entityManager.flush()  // ID를 생성하고 데이터베이스에 반영
        } else {
            entityManager.merge(keypad)
        }
        println("Saved KeypadModel ID: ${keypad.id}")
        return keypad
    }

    fun findById(id: UUID): Optional<KeypadModel> {
        return Optional.ofNullable(entityManager.find(KeypadModel::class.java, id))
    }

    fun findAll(): List<KeypadModel> {
        val query = entityManager.createQuery("SELECT k FROM KeypadModel k", KeypadModel::class.java)
        return query.resultList
    }

    fun deleteById(id: UUID) {
        findById(id).ifPresent { keypad ->
            entityManager.remove(keypad)
        }
    }
}
