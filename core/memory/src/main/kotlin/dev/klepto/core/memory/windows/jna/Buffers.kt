package dev.klepto.core.memory.windows.jna

object Buffers {
    fun toByteArrayPacked(charArray: CharArray): ByteArray {
        val byteArray = ByteArray(charArray.size * 2)
        for (i in charArray.indices) {
            val valueA = charArray[i].code.toByte()
            val valueB = (charArray[i].code shr 8).toByte()
            byteArray[i * 2] = valueA
            byteArray[i * 2 + 1] = valueB
        }
        return byteArray
    }

    fun toByteArray(charArray: CharArray): ByteArray {
        val byteArray = ByteArray(charArray.size)
        for (i in charArray.indices) {
            byteArray[i] = charArray[i].code.toByte()
        }
        return byteArray
    }

    fun toString(byteArray: ByteArray): String? {
        var length = byteArray.size
        for (i in byteArray.indices) {
            if (byteArray[i].toInt() != 0) {
                continue
            }

            length = i
            break
        }

        return String(byteArray, 0, length)
    }

    fun toString(charArray: CharArray): String? {
        return toString(toByteArray(charArray))
    }

    fun toStringPacked(charArray: CharArray): String? {
        return toString(toByteArrayPacked(charArray))
    }
}
