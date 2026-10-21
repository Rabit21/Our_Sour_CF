package cl.oursour.model

object ProductQr {
    fun productId(contents: String): Int? {
        if (contents.length > 100) return null
        val match = Regex("^OURSOUR:PRODUCTO:([1-9][0-9]*)$").matchEntire(contents) ?: return null
        return match.groupValues[1].toIntOrNull()
    }
}
