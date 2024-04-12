//VAL É IMUTÁVEL
//VAR É MUTÁVEL

sealed interface XMLChild{
    val value: String
    val parent: XMLParent?
}

sealed interface XMLParent{
    val children: MutableList<XMLChild>

    /*Implementação da interface visitor
    XMLParent é o que está a ser visitado (daí implementarmos aqui): o documento (e consequentemente tags)
    Fazer já um accept que "faz coisas" aqui pq vai ser usado no document e na tag da mesma maneira?
     */
    fun accept(visitor: (XMLParent) -> Boolean){
        if (visitor(this)) {
            this.children.forEach {
                if(it is XMLParent)  //sem este if teriamos de ter uma função accept tbm no XMLChild
                    it.accept(visitor)
            }
        }
    }

    fun addChild(child: XMLChild){
        if(this.children.isNotEmpty()) { //se a tag tiver filhos temos de verificar o que são para deixar ou não acrescentar mais
            this.children.forEach {
                when (it) {
                    is Text -> if(child is Tag)
                        throw IllegalArgumentException("Não pode adicionar texto como filho se já houverem filhos tag.")
                    is Tag -> if(child is Text)
                        throw IllegalArgumentException("Não pode adicionar uma tag como filho se já houverem filhos texto.")
                }
            }
        }
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

/*Notinhas:
- DUVIDA: Onde é que escrevemos as coisas no xml em si? Tipo com as < > e isso tudo
- só pode haver uma root tag
- Deviamos verificar se um child pode ou não ser adicionado: se já ouver text não podemos acrescentar uma tag como child
*/

data class Document(val encode: String, val version: String): XMLParent{

    override val children: MutableList<XMLChild> = mutableListOf()

    init {
        val xmlDeclarationText = "?xml version=\"$version\" encoding=\"$encode\"?"
    }

    //6. Add atributo globalmente e o resto
    fun addAttribute(tag: Tag, name: String, value: String){

    }

    //7.  renomeação de entidades globalmente ao documento (fornecendo nome antigo e nome novo)
    fun renameEntityGlobally(oldname: String, newname: String){
        this.accept {
            if((it is Tag) && it.value.equals(oldname)) {
                    it.value = newname
                }
            false
        }
    }

    //4. prettyPrint: escrever tudo no ficheiro e pôr bonito
    fun prettyPrint(){
    }



}

data class Tag(override var value: String, override val parent: Tag? = null): XMLChild, XMLParent {
    //com val em vez de var não se pode mudar a lista toda pô-la toda a null, mas pode se fazer alterações na mesma pq é mutable
    override val children: MutableList<XMLChild> = mutableListOf()
    private val attributes: MutableList<Attribute> = mutableListOf()

    init {
        parent?.children?.add(this)
    }

    //2. Add, remover e alterar atributos em entidades
    //Podiamos usar ignoreCase = true
    /*fun addAttribute(atr: Attribute) {
        if (attributes.any { it.name.equals(atr.name) }) {  //verifica se já existe um atributo com este nome
            //lançar excepção!! JÁ TEM ESSE NOME
        } else attributes.add(atr)
    }


    //Usar o quê para procurar o atributo na lista? O nome? Então tem de ser único
    fun removeAttribute(atr: Attribute) {
        attributes.remove(atr)
    }

    fun changeAttribute(atr: Attribute, newvalue: String) {
        val attrToChange = attributes.find { it.name.equals(atr.name) }
        if (attrToChange != null) {
            attrToChange.value = newvalue
        }
    }*/
}
    
data class Attribute(var name: String, var value: String){

}

data class Text(override var value: String, override val parent: Tag? = null): XMLChild{
    init {
        parent?.children?.add(this)
    }
}
