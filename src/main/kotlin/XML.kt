//VAL É IMUTÁVEL
//VAR É MUTÁVEL

sealed interface XMLChild{
    val value: String
    val parent: XMLParent?
}

sealed interface XMLParent{
    var children: MutableList<XMLChild>

    fun addChild(child: XMLChild){
        children.add(child)
    }

    fun removeChild(child: XMLChild) {
        children.remove(child)
        if (child is XMLParent) {
            child.children.forEach {
                removeChild(it)
            }
        }
    }

}

//DUVIDA: Onde é que escrevemos as coisas no xml em si? Tipo com as < > e isso tudo


data class Document(val encode: String, val version: String): XMLParent{

    override var children: MutableList<XMLChild> = mutableListOf()

    init {
        val xmlDeclarationText = "?xml version=\"$version\" encoding=\"$encode\"?"
    }

    //6. Add atributo globalmente e o resto
    fun addAttribute(tag: Tag, name: String, value: String){}

    //4. prettyPrint: escrever tudo no ficheiro e pôr bonito
    fun prettyPrint(){
    }


}

data class Tag(override val value: String, override val parent: Tag? = null): XMLChild, XMLParent {

    override var children: MutableList<XMLChild> = mutableListOf()
    var attributes: MutableList<Attribute> = mutableListOf()

    init {
        parent?.children?.add(this)
    }

    //2. Add, remover e alterar atributos em entidades
    fun addAttribute(atr: Attribute) {
        if (attributes.any { it.name.equals(atr.name) }) {  //verifica se já existe um atributo com este nome
            //lançar excepção!! JÁ TEM ESSE NOME
        } else attributes.add(atr)
    }


    //Usar o quê para procurar o atributo na lista? O nome? Então tem de ser único
    fun removeAttribute(atr: Attribute) {
        attributes.remove(atr)
    }

    fun changeAttribute(atr: Attribute, newvalue: String) {
        val attrToChange = attributes.find { it.name == atr.name }
        if (attrToChange != null) {
            attrToChange.value = newvalue
        }
    }
}
    
data class Attribute(var name: String, var value: String){

}

data class Text(override var value: String, override val parent: Tag? = null): XMLChild{
    init {
        parent?.children?.add(this)
    }
}
