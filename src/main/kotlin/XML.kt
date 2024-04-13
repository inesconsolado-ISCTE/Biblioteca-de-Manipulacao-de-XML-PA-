import java.io.File

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
            val childrenCopy = ArrayList(this.children)  //estava a dar um erro relativo a iterar uma lista e modificá-la ao mesmo tempo portanto agora estamos a iterar uma cópia e a modificar o original
            childrenCopy.forEach {
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
- só pode haver uma root tag
*/

data class Document(val encode: String, val version: String): XMLParent{

    override val children: MutableList<XMLChild> = mutableListOf()


    private val xmlDeclarationText = "<?xml version=\"$version\" encoding=\"$encode\"?>"


    //6. Add atributo globalmente e o resto
    fun addAttributeGlobally(parent: String, name: String, value: String){
        this.accept {
            if((it is Tag) && it.value.equals(parent)) {
                it.addAttribute(name, value)
                return@accept false
            }
            true
        }
    }

    //7.  renomeação de entidades globalmente ao documento (fornecendo nome antigo e nome novo)
    fun renameEntityGlobally(oldname: String, newname: String){
        this.accept {
            if((it is Tag) && it.value.equals(oldname)) {
                it.value = newname
                return@accept false
            }
            true
        }
    }

    //8. renomeação de atributos
    fun renameAttributeGlobally(parent: String, oldname: String, newname: String){
        this.accept {
            if((it is Tag) && it.value.equals(parent)) {
                it.changeAttribute(oldname, newname)
                return@accept false
            }
            true
        }
    }

    //9. remoção de entidades globalmente ao documento
    fun removeEntityGlobally(name: String){
        this.accept {
            println(it)
            if((it is Tag) && it.value.equals(name)) {
                it.parent?.removeChild(it)
                return@accept false
            }
            true
        }
    }

    //10. remoçao de atributos globalmente ao documento
    fun removeAttributeGlobally(parent: String, name: String){
        this.accept {
            if((it is Tag) && it.value.equals(parent)) {
                it.removeAttribute(name)
                return@accept false
            }
            true
        }
    }

    //4. prettyPrint: escrever tudo no ficheiro e pôr bonito
    // Add this function to your Document class
    fun prettyPrint(): String {
        return buildString {
            append("$xmlDeclarationText\n")
            children.forEach { child ->
                prettyPrintLine(child, this, 0)
            }
        }.trimEnd()
    }

    private fun prettyPrintLine(child: XMLChild, stringBuilder: StringBuilder, level: Int, isTextChild: Boolean = false) {
        val indent = " ".repeat(level * 4)
        when (child) {
            is Tag -> {
                stringBuilder.append("$indent<${child.value}")
                if (child.attributes.isNotEmpty()) {
                    stringBuilder.append(" ")
                    child.attributes.forEach { attribute ->
                        stringBuilder.append("${attribute.name}=\"${attribute.value}\" ")
                    }
                    stringBuilder.deleteCharAt(stringBuilder.length - 1)
                }

                stringBuilder.append(">")

                val hasTextChild = child.children.any { it is Text }
                if (!hasTextChild) {
                    stringBuilder.append("\n")
                }
                child.children.forEach {subChild ->
                    prettyPrintLine(subChild, stringBuilder, level + 1, subChild is Text)
                }

                if (!isTextChild  && hasTextChild) {
                    stringBuilder.append("</${child.value}>\n")
                }else
                    stringBuilder.append("$indent</${child.value}>\n")
            }
            is Text -> {
                stringBuilder.append(child.value.trim())
            }
        }
    }


    fun writeToFile(fileName: String) {
        val content = prettyPrint()
        File(fileName).writeText(content)
    }


}

data class Tag(override var value: String, override val parent: Tag? = null): XMLChild, XMLParent {
    //com val em vez de var não se pode mudar a lista toda pô-la toda a null, mas pode se fazer alterações na mesma pq é mutable
    override val children: MutableList<XMLChild> = mutableListOf()
    val attributes: MutableList<Attribute> = mutableListOf()

    init {
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
        parent?.children?.add(this)
    }

    //2. Add, remover e alterar atributos em entidades
    //Podiamos usar ignoreCase = true
    fun addAttribute(name: String, value: String) {
        val atr = Attribute(name, value)
        if (attributes.any { it.name.equals(name) }) {  //verifica se já existe um atributo com este nome
            throw IllegalArgumentException("Já existe um atributo associado a esta Tag com esse nome.")
        } else attributes.add(atr)
    }

    fun removeAttribute(name: String) {
        val atr = attributes.find { it.name == name }
        attributes.remove(atr)
    }

    //isto antes recebia um atributo e um novo nome mas eu mudei para um nome antigo em vez de um objeto atributo, para se usar numa func do documento (e assim n tem de se criar o objeto para lhe acedermos?)
    fun changeAttribute(givenName: String, newvalue: String) {
        val attrToChange = attributes.find { it.name.equals(givenName) }
        if (attrToChange != null) {
            attrToChange.value = newvalue
        }
    }
}

data class Attribute(var name: String, var value: String){
//Já não sei qual é a utilidade desta classe
}

data class Text(override var value: String, override val parent: Tag? = null): XMLChild{
    init {
        parent?.children?.add(this)
    }
}
