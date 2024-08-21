//keypadModel.kt
package bob.e2e.model

import jakarta.persistence.*
import java.util.UUID
import java.time.LocalDateTime


@Entity
@Table(name = "keypad")
data class KeypadModel(
    @Id
    var id: UUID = UUID.randomUUID(), // UUID는 반드시 초기화되며 null이 될 수 없음

    @ElementCollection
    @CollectionTable(name = "keypad_hashnum_map", joinColumns = [JoinColumn(name = "keypad_id")])
    @MapKeyColumn(name = "hash_key")
    @Column(name = "hash_value")
    val hashNumMap: Map<String, String> = HashMap(),

    val time: LocalDateTime = LocalDateTime.now()
)