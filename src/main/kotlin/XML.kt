sealed interface XMLChild{
    val value: String
    val parent: Tag?

    //1. remover tag e texto
}

//DUVIDA: Onde é que escrevemos as coisas no xml em si? Tipo com as < > e isso tudo

data class Document(val encode: String, val value: String){

    var children: MutableList<XMLChild> = mutableListOf()

    //pôr aquela primeira linha de um xml

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
    // é suposto nestas funções "escrever" no xml ou isso é o pretty print?
    fun addAttribute(atr: Attribute){
        if(attributes.any {it.name == atr.name}){  //verifica se já existe um atributo com este nome

        }
        else attributes.add(atr)
    //tbm pode receber uma lista de atributos em vez de ser só um, para ser mais eficiente?
    // E se quiser adicionar só um é uma lista de um?
    }


    //Usar o quê para procurar o atributo na lista? O nome? Então tem de ser único
    fun removeAttribute(atr: Attribute){
        attributes.remove(atr)
    }

    fun changeAttribute(atr: Attribute, newvalue: String){
        val attrToChange = attributes.find {it.name == atr.name}
        if (attrToChange != null) {
            attrToChange.value = newvalue
        }
    }

    fun addChild(child: XMLChild){
        children.add(child)
    }


}

data class Attribute(var name: String, var value: String){

}

data class Text(override var value: String, override val parent: Tag? = null): XMLChild{
    init {
        parent?.children?.add(this)
    }
}
