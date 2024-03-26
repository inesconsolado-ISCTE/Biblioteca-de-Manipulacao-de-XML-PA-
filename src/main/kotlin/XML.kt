sealed interface xmlElement {
    val name : String
    val text : String?
    val parent : xmlComposite?

    fun prettyPrint() : String {}

    fun addAttribute() : Boolean {}

    fun removeAttribute() : Boolean {}

    fun modifyAttribute() : Boolean {}

}

data class xmlComposite(
    override val name: String,
    override val text : String? = null,
    override val parent : xmlComposite? = null
) : xmlElement {
    //private val children = mutableListOf<xmlElement>()
    internal val children: MutableList<xmlElement> = mutableListOf()

    init {
        parent?.children?.add(this)
    }

}

data class xmlLeaf(
    override val name: String,
    override val text : String? = null,
    override val parent : xmlComposite? = null
) : xmlElement {
    init {
        parent?.children?.add(this)
    }
}

data class xmlAttribute() {}