import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun main() {
    runFile()

}


fun runFile() {
    /**
     * основная функция которая выполняет действия по условиям
     */
    var image: BufferedImage
    while (true){
        println("Task (hide, show, exit):")
        when (val string = readln().lowercase()) {
            "exit" -> {
                println("Bye!")
                return
            }
            "hide" -> {
                println("Input image file:")
                val inputName = readln()
                val nameFileIn = File(inputName)
                try {
                    image = readImage(nameFileIn)
                } catch (e: Exception) {
                    println(e.message)
                    continue
                }
                println("Output image file:")
                val outputName = readln()
                val nameFileOut = File(outputName)
                println("Message to hide:")
                val massage = readln()
                val codingMessage = coding(massage)
                if (image.height * image.width < codingMessage.size) {
                    println("The input image is not large enough to hold this message.")
                    continue
                }

                image = editFile(image, codingMessage)
                saveImage(nameFileOut, image)
                println("Message saved in $outputName image.")
            }
            "show" -> {
                println("Input image file:")
                val inputName = readln()
                val nameFileIn = File(inputName)
                try {
                    image = readImage(nameFileIn)
                } catch (e: Exception) {
                    println(e.message)
                    continue
                }
                println("Message:")
                val listCode = convertFromImageToByteList(image)
                val string = encoding(listCode)
                println(string)
            }
            else -> {
                println("Wrong task: $string")
            }
        }
    }
}
fun saveImage(nameFile: File, image: BufferedImage) {
//    val height: Int = 600// высота png изображения
//    val width: Int = 800// ширина png изображения
//    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)// конструктор изображения
//    val nameFile = nameFile// имя переданное в функцию
    /**
     * функция сохраняет файл изображения
     */
    ImageIO.write(image, "png", nameFile)// конструктор для создания файла
}
fun readImage(nameFile: File): BufferedImage {
    /**
     * функция читает файл изображения
     */
    return ImageIO.read(nameFile)
}
fun editFile(image: BufferedImage, arrayByte: List<Int>): BufferedImage {
    /**
     * функция переберает изображение по пиксельно и меняем значение цвета пикселя по цветам
     */
    var index = 0
    var bit = 0
    var newColor: Int
    for (y in 0 until image.height) {//перебор по ширине
        for (x in 0 until image.width) {//перебор по высоте
            val color = Color(image.getRGB(x, y))//считываем значение пикселя в rgb
            if (index < arrayByte.size) {
                 bit = arrayByte[index]
                 newColor = Color(color.red,//не меняем красный цвет
                    color.green,//не меняет зеленый цвет
                    setLeastSignificantBitToBit(color.blue, bit)).rgb//меняем синий цвет. и конструктором сохраняем цвет пикселя

            } else {
                 newColor = Color(color.red,//не меняем красный цвет
                    color.green,//не меняет зеленый цвет
                    color.blue).rgb//меняем синий цвет. и конструктором сохраняем цвет пикселя
            }
            index += 1
            image.setRGB(x, y, newColor)// презаписываем цвет каждого пикселя
        }
    }

    return image
}
fun setLeastSignificantBitToBit(pixel: Int, bit: Int): Int {
    /**
     * фунция принемает значение пикселя и изменяет последний бит на полученный
     */
    var pixel = pixel.shr(1).shl(1)//затераем в 0 нулевой бит
        pixel += bit// в нулевой бит записываем значение из переменной bit
    return pixel
}
fun coding(string: String): List<Int> {
    val str = string + "003"
    val arrayByte = str.encodeToByteArray()
    val codingMassage: MutableList<String> = mutableListOf()
    val code = mutableListOf<Int>()
    for (index in arrayByte.indices) {
        var byteToBit = arrayByte[index].toString(2)
        when(byteToBit.length - 1) {
            7 -> byteToBit = "$byteToBit"
            6 -> byteToBit = "0$byteToBit"
            5 -> byteToBit = "00$byteToBit"
            4 -> byteToBit = "0000$byteToBit"
            3 -> byteToBit = "00000$byteToBit"
            2 -> byteToBit = "000000$byteToBit"
            1 -> byteToBit = "0000000$byteToBit"
        }
        codingMassage += byteToBit
    }
    for (index in codingMassage.indices){
        val tempByte = codingMassage[index]
        for (index in tempByte.indices) {
            val num = tempByte[index].code
            code += if (num == 48) 0 else 1
        }
    }
    return code.toList()
}
fun encoding(codeMessage: List<Int>): String {
    var codeToByte: ByteArray = byteArrayOf()
    val tempCodeMessage = codeMessage.toMutableList()
    while (tempCodeMessage.size != 0) {
        var tempByte = 0
        if (tempCodeMessage.size > 7 ) {
            for (index in 0 .. 7) {
                tempByte = (tempByte shl 1) or tempCodeMessage[index]
            }
            codeToByte += tempByte.toByte()
            for (i in 0 .. 7) {
                tempCodeMessage.removeFirst()
            }
        } else {
            tempCodeMessage.clear()
        }

    }
    val str = codeToByte.toString(Charsets.UTF_8)
    return str.substringBefore("003")
}
fun convertFromImageToByteList(image: BufferedImage): List<Int> {
    val codeMessage = mutableListOf<Int>()
    for (y in 0 until image.height) {
        for (x in 0 until image.width) {
            val color = Color(image.getRGB(x, y))//считываем значение пикселя в rgb
            codeMessage.add(color.blue and 1)
        }
    }
    return codeMessage.toList()
}