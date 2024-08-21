package bob.e2e.service

import bob.e2e.dto.KeypadResponseDto
import bob.e2e.dto.UserInputRequestDto
import bob.e2e.dto.userInputResponseDto
import bob.e2e.model.KeypadModel
import bob.e2e.repository.KeypadRepository
import org.springframework.transaction.annotation.Transactional
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.http.MediaType

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.security.MessageDigest
import java.time.LocalDateTime
import java.util.*
import javax.imageio.ImageIO

@Service
class KeypadService(private val keypadRepository: KeypadRepository) {

    private val restTemplate = RestTemplate()


    @Transactional
    fun makeKeypad(): KeypadResponseDto {
        // num 및 BLANK1, BLANK2 해시값 map 생성
        val hashNumMap: Map<String, String> = (0..9).map { it.toString() }.plus(listOf("BLANK1", "BLANK2")).zip(
            (0..9).map {
                MessageDigest.getInstance("SHA-1").digest(it.toString().toByteArray())
                    .joinToString("") { byte -> "%02x".format(byte) }
            } + listOf(
                MessageDigest.getInstance("SHA-1").digest("BLANK1".toByteArray())
                    .joinToString("") { byte -> "%02x".format(byte) },
                MessageDigest.getInstance("SHA-1").digest("BLANK2".toByteArray())
                    .joinToString("") { byte -> "%02x".format(byte) }
            )
        ).toMap()

        // BLANK1, BLANK2를 추가하고 keyList 배열 생성 및 셔플
        val keyList = (0..9).map { it.toString() }.toMutableList().apply {
            add("BLANK1")
            add("BLANK2")
        }
        val shuffledKeyList = keyList.shuffled(java.util.Random())

        // 해시값 배열 생성
        val hashKeyList: List<String> = shuffledKeyList.map { key ->
            hashNumMap[key]!!
        }

        // 키패드 이미지를 위한 Map 생성(num: imagePath)
        val source = ClassPathResource("keypad")
        val imagePathMap: Map<String, String> = source.file.listFiles()
            ?.filter { it.extension == "png" }
            ?.associate { file ->
                val key = when {
                    file.name == "_blank1.png" -> "BLANK1"
                    file.name == "_blank2.png" -> "BLANK2"
                    else -> file.name.substringAfter('_').substringBefore('.')
                }
                key to file.absolutePath
            } ?: emptyMap()

        // 하나의 이미지로 생성
        val rows = 3
        val cols = 4
        val cellSize = 100 // 이미지 셀의 가로, 세로 크기

        val padImage = BufferedImage(cellSize * cols, cellSize * rows, BufferedImage.TYPE_INT_ARGB)
        val graphics = padImage.createGraphics()

        shuffledKeyList.forEachIndexed { index, key ->
            val imagePath = imagePathMap[key] ?: return@forEachIndexed
            val image = ImageIO.read(File(imagePath))
            val x = (index % cols) * cellSize
            val y = (index / cols) * cellSize
            graphics.drawImage(image, x, y, cellSize, cellSize, null)
        }
        graphics.dispose()

        // Base64로 인코딩
        val byteArrOutput = ByteArrayOutputStream()
        ImageIO.write(padImage, "png", byteArrOutput)
        val base64Image = Base64.getEncoder().encodeToString(byteArrOutput.toByteArray())

        /////디버깅용
        println(hashNumMap)
        println(shuffledKeyList)
        println(hashKeyList)
        println(base64Image)
        /////


        // KeypadModel 인스턴스 생성 및 저장
        val keypadModel = KeypadModel(
            id = UUID.randomUUID(),
            hashNumMap = hashNumMap,
            time = LocalDateTime.now()
        )
        keypadRepository.save(keypadModel)

        println("Saved KeypadModel ID: ${keypadModel.id}")

        return KeypadResponseDto(keypadModel.id!!, hashKeyList, base64Image, keypadModel.time)
    }
    fun processUserInput(userInputRequest: UserInputRequestDto) : userInputResponseDto {
        // keypadId로 데이터베이스에서 키패드 정보 조회
        // keypadId를 터미널에 출력
        println("Received keypadId: ${userInputRequest.keypadId}")
        println("Received encryptedUserData: ${userInputRequest.userInput}")

        // keypadId로 데이터베이스에서 키패드 정보 조회
        val keypadModel = keypadRepository.findById(UUID.fromString(userInputRequest.keypadId))
            .orElseThrow {
                println("Invalid Keypad ID: ${userInputRequest.keypadId}")
                IllegalArgumentException("Invalid Keypad ID")
            }
        // keypadModel을 터미널에 출력
        println("Fetched KeypadModel: $keypadModel")

        // POST 요청을 위한 데이터 준비
        val requestBody = mapOf(
            "userInput" to userInputRequest.userInput, // userInput은 UserInputRequestDto의 필드로 가정
            "keyHashMap" to keypadModel.hashNumMap
        )
        println("requestBody: $requestBody")


        // HTTP 헤더 설정
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }

        // HTTP 엔티티 생성 (헤더와 바디 포함)
        val requestEntity = HttpEntity(requestBody, headers)

        try {
            // 외부 API로 POST 요청 보내기
            val response: ResponseEntity<String> = restTemplate.exchange(
                "http://146.56.119.112:8081/auth",
                HttpMethod.POST,
                requestEntity,
                String::class.java
            )

            // 응답 처리
            val responseBody = response.body ?: "No response body"
            println("Response from external API: $responseBody")
            return userInputResponseDto("Success",responseBody)

        } catch (e: Exception) {
            // 예외 발생 시 오류 메시지 출력
            println("Error occurred while making POST request: ${e.message}")
            e.printStackTrace()
            return userInputResponseDto("Fail","Fail")
        }

    }

}
