sealed interface XMLChild{
    val value: String
    val parent: Tag?

    //1. remover tag e texto val remove:


}


data class Document(val encode: String, val value: String){

    var children: MutableList<XMLChild> = mutableListOf()

    //6. Add atributo globalmente e o resto
    fun addAttribute(tag: Tag, name: String, value: String){}
    //4. val prettyPrint:
}

data class Tag(override val value: String, override val parent: Tag? = null): XMLChild{

    var children: MutableList<XMLChild> = mutableListOf()
    var attributes: MutableList<Attribute> = mutableListOf()

    init {
        parent?.children?.add(this)
    }

    //2. Add, remover e alterar atributos em entidades
    fun addAttribute(atr: Attribute){
    //tbm pode receber uma lista de atributos em vez de ser só um, para ser mais eficiente?
    // E se quiser adicionar só um é uma lista de um?
    }

    fun removeAttribute(atr: Attribute){

    }

    fun changeAttribute(atr: Attribute){

    }
}

data class Attribute(var name: String, var value: String){

}

data class Text(override var value: String, override val parent: Tag? = null): XMLChild{
    init {
        parent?.children?.add(this)
    }
}
