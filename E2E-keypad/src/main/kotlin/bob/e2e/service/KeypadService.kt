package bob.e2e.service

import bob.e2e.dto.KeypadDto
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.security.MessageDigest
import java.util.*
import javax.imageio.ImageIO

@Service
class KeypadService {

    fun makeKeypad(): KeypadDto {
        //num 해시값 map 생성
        val hashNumMap: Map<String, String> = (0..9).map { it.toString() }.zip(
            (0..9).map {
                MessageDigest.getInstance("SHA-256").digest(it.toString().toByteArray())
                    .joinToString("") { byte -> "%02x".format(byte) }
            }
        ).toMap()

        //빈값 2개를 추가하고 keyList 배열 생성 및 셔플
        val keyList = (0..9).map { it.toString() }.toMutableList().apply {
            add("BLANK")
            add("BLANK")
        }
        val shuffledKeyList = keyList.shuffled(java.util.Random())

        //해시값 배열 생성
        val hashKeyList: List<String> = shuffledKeyList.map { key ->
            hashNumMap[key] ?: ""
        }

        // 키패드 이미지를 위한 Map 생성(num: imagePath)
        val source = ClassPathResource("keypad")
        val imagePathMap: Map<String, String> = source.file.listFiles()
            ?.filter { it.extension == "png" }
            ?.associate { file ->
                val key = when {
                    file.name == "_blank.png" -> "BLANK"
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

        return KeypadDto(UUID.randomUUID(), hashKeyList, base64Image)
    }
}

