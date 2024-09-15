package udb.edu.sv.datos

data class Compra(
    val fecha: String = "",
    val total: Int = 0,
    val items: Map<String, Int> = emptyMap()
)