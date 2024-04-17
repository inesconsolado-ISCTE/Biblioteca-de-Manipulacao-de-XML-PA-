import java.io.File

//VAL É IMUTÁVEL
//VAR É MUTÁVEL

sealed interface XMLChild{
    val value: String
    val parent: XMLParent
}

sealed interface XMLParent{
    val children: MutableList<XMLChild>

    //Implementação da interface visitor
    //XMLParent é o que está a ser visitado (daí implementarmos aqui): o documento (e consequentemente tags)

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
                    is Text -> if (child is Tag)
                        throw IllegalArgumentException("Não pode adicionar texto como filho se já houverem filhos tag.")

                    is Tag -> if (child is Text)
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
- é melhor separar tudo em ficheiros diferentes, em termos de "bom design"? Se sim, tiramos o sealed das interfaces
*/

data class Document(val encode: String, val version: String): XMLParent{

    override val children: MutableList<XMLChild> = mutableListOf()

    //private var rootTag: Tag

    private val xmlDeclarationText = "<?xml version=\"$version\" encoding=\"$encode\"?>"

    /*fun setRootTag(root: Tag){
        rootTag = root
    }

    fun getRootTag(): Tag{
        return rootTag
    }

     */

    fun microXPath(xpath: String): MutableList<XMLChild> {

        val elements = mutableListOf<XMLChild>()
        val tags = xpath.split("/")
        println(tags)
        println(tags[0])

        //assumindo que o string que passam começa na tag root
        val firstTag = this.children.find { it.value == tags[0] && it is Tag}
        if(firstTag is XMLParent){
            firstTag.children.forEach {
                if(it.value == tags[1] && it is Tag){
                    if(tags.size-1 > 1) {
                        helper(it, tags, 2, elements)
                    }
                    else {
                        elements.add(it)
                    }
                }
            }
        }
        return elements
    }

    private fun helper(tag: XMLParent, tags: List<String>, index: Int, elements: MutableList<XMLChild>) {
        var currentParent: XMLParent = tag
        println("estou no helper")
        val foundTags = mutableListOf<XMLChild>()
        currentParent.children.forEach{
            println(it)
            println(tags[index])
            if(it.value == tags[index] && it is XMLParent){
                if(index != tags.size-1) {
                    println(index)
                    currentParent = it
                    helper(currentParent, tags, index + 1, elements)
                }
                else {
                    println("encontrei: $it")
                    foundTags.add(it)
                }
            }
        }
        println("aqui estão:$foundTags")
        elements.addAll(foundTags)
    }



    //6. Add atributo globalmente e o resto
    fun addAttributeGlobally(parent: String, name: String, value: String){
        this.accept {
            if((it is Tag) && it.value == parent) {
                it.addAttribute(name, value)
                return@accept false
            }
            true
        }
    }

    //7.  renomeação de entidades globalmente ao documento (fornecendo nome antigo e nome novo)
    fun renameEntityGlobally(oldname: String, newname: String){
        this.accept {
            if((it is Tag) && it.value == oldname) {
                it.value = newname
                return@accept false
            }
            true
        }
    }

    //8. renomeação de atributos
    fun renameAttributeGlobally(parent: String, oldname: String, newname: String,newValue: String? = null){
        this.accept {
            if((it is Tag) && it.value.equals(parent)) {
                it.changeAttribute(oldname, newname,newValue)
                return@accept false
            }
            true
        }
    }

    //9. remoção de entidades globalmente ao documento
    fun removeEntityGlobally(name: String){
        this.accept {
            //println(it)
            if((it is Tag) && it.value == name) {
                it.parent.removeChild(it)
                return@accept false
            }
            true
        }
    }

    //10. remoçao de atributos globalmente ao documento
    fun removeAttributeGlobally(parent: String, name: String){
        this.accept {
            if((it is Tag) && it.value == parent) {
                it.removeAttribute(name)
                return@accept false
            }
            true
        }
    }

    //4. prettyPrint: escrever tudo no ficheiro e pôr bonito
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
                if (child.getAttributes().isNotEmpty()) {
                    stringBuilder.append(" ")
                    child.getAttributes().forEach { attribute ->
                        stringBuilder.append("${attribute.name}=\"${attribute.value}\" ")
                    }
                    stringBuilder.deleteCharAt(stringBuilder.length - 1)
                }
                if (child.children.isEmpty()) {
                    stringBuilder.append("/>\n")
                } else {
                    stringBuilder.append(">")

                    val hasTextChild = child.children.any { it is Text }
                    if (!hasTextChild) {
                        stringBuilder.append("\n")
                    }
                    child.children.forEach { subChild ->
                        prettyPrintLine(subChild, stringBuilder, level + 1, subChild is Text)
                    }

                    if (!isTextChild && hasTextChild) {
                        stringBuilder.append("</${child.value}>\n")
                    } else
                        stringBuilder.append("$indent</${child.value}>\n")
                    }
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

data class Tag(override var value: String, override val parent: XMLParent): XMLChild, XMLParent {

    override val children: MutableList<XMLChild> = mutableListOf()
    private val attributes: MutableList<Attribute> = mutableListOf()

    init {
        parent.addChild(this)
    }

    fun getAttributes(): List<Attribute>{
        return attributes
    }

    //2. Add, remover e alterar atributos em entidades
    //Podiamos usar ignoreCase = true
    fun addAttribute(name: String, value: String) {
        val atr = Attribute(name, value)
        if (attributes.any { it.name == name }) {  //verifica se já existe um atributo com este nome
            throw IllegalArgumentException("Já existe um atributo associado a esta Tag com esse nome.")
        } else attributes.add(atr)
    }

    fun removeAttribute(name: String) {
        val atr = attributes.find { it.name == name }
        attributes.remove(atr)
    }

    //isto antes recebia um atributo e um novo nome mas eu mudei para um nome antigo em vez de um objeto atributo, para se usar numa func do documento (e assim n tem de se criar o objeto para lhe acedermos?)
    fun changeAttribute(givenName: String, newname: String, newValue: String? = null) {
        val attrToChange = attributes.find { it.name == givenName }
        if (attrToChange != null) {
            attrToChange.name = newname
            if (newValue != null) {
                attrToChange.value = newValue
            }
        }
    }

}

data class Attribute(var name: String, var value: String){
//Já não sei qual é a utilidade desta classe
}

data class Text(override var value: String, override val parent: Tag): XMLChild{
    init {
        parent.addChild(this)
    }
}
