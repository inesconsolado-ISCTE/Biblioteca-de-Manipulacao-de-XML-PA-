import java.io.File

data class Document(val encode: String, val version: String): ReceivesVisitor{

    //override val children: MutableList<XMLChild> = mutableListOf()

    private lateinit var rootTag: Tag

    private val xmlDeclarationText = "<?xml version=\"$version\" encoding=\"$encode\"?>"

    fun setRootTag(root: Tag){
        rootTag = root
    }

    fun getRootTag(): Tag{
        return rootTag
    }

    override fun getChildrenOfTag(): List<XMLChild> {
        return rootTag.getChildrenOfTag()
    }

    fun checkIfEntityExists(name: String): Boolean {
        var entityExists = false
        this.accept {
            if ((it is Tag) && it.value == name) {
                entityExists = true
                return@accept true
            }
            false
        }
        return entityExists
    }

    fun microXPath(xpath: String): MutableList<XMLChild> {

        val elements = mutableListOf<XMLChild>()
        val tags = xpath.split("/")
        println(tags)
        println(tags[0])

        //assumindo que o string que passam começa na tag root
        val firstTag = this.getChildrenOfTag().find { it.value == tags[0] && it is Tag}
        if(firstTag is ReceivesVisitor){
            firstTag.getChildrenOfTag().forEach {
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

    private fun helper(tag: ReceivesVisitor, tags: List<String>, index: Int, elements: MutableList<XMLChild>) {
        var currentParent: ReceivesVisitor = tag
        println("estou no helper")
        val foundTags = mutableListOf<XMLChild>()
        currentParent.getChildrenOfTag().forEach{
            println(it)
            println(tags[index])
            if(it.value == tags[index] && it is ReceivesVisitor){
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
        if(checkIfEntityExists(oldname)) {
            this.accept {
                if ((it is Tag) && it.value == oldname) {
                    it.value = newname
                    return@accept false
                }
                true
            }
        }
        else throw IllegalArgumentException("Não pode alterar uma tag que não existe.")
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
        if(checkIfEntityExists(name)) {
            this.accept {
                //println(it)
                if ((it is Tag) && it.value == name) {
                    it.parent.removeChild(it)
                    return@accept false
                }
                true
            }
        } else throw IllegalArgumentException("Não pode remover uma tag que não existe.")
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
            getChildrenOfTag().forEach { child ->
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
                if (child.getChildrenOfTag().isEmpty()) {
                    stringBuilder.append("/>\n")
                } else {
                    stringBuilder.append(">")

                    val hasTextChild = child.getChildrenOfTag().any { it is Text }
                    if (!hasTextChild) {
                        stringBuilder.append("\n")
                    }
                    child.getChildrenOfTag().forEach { subChild ->
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