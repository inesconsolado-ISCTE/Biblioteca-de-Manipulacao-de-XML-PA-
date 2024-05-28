import java.io.File

/**
 * Data class que representa um documento XML.
 *
 * @property encode A codificação do documento XML.
 * @property version A versão do documento XML.
 * @property name O nome do documento.
 */
data class Document(val encode: String, val version: String, val name: String): ReceivesVisitor{

    //override val children: MutableList<XMLChild> = mutableListOf()

    private lateinit var rootTag: Tag

    private val xmlDeclarationText = "<?xml version=\"$version\" encoding=\"$encode\"?>"

    /**
     * Função que define a rootTag(tag raiz) do documento.
     *
     * @param root A Tag a ser definida como raiz.
     * @throws IllegalStateException Se a rootTag já foi definida.
     */
    fun setRootTag(root: Tag){
        if (::rootTag.isInitialized) {
            println(::rootTag.isInitialized)
            println(getRootTag())
            throw IllegalStateException("A tag root já foi definida para este documento.")
            }
        rootTag = root
    }

    /**
     * Função que obtém a rootTag do documento.
     *
     * @return A rootTag do documento.
     * @throws IllegalStateException Se a rootTag ainda não for definida.
     */
    fun getRootTag(): Tag{
        if (!::rootTag.isInitialized) {
            throw IllegalStateException("A tag root já foi definida para este documento.")
        }
        return rootTag
    }


    /**
     * Função que verifica se a rootTag foi inicializada.
     *
     * @return `true` se a rootTag foi inicializada, caso contrário `false`.
     */
    fun isRootTagInitialized(): Boolean {
        return ::rootTag.isInitialized
    }

    /**
     * Função que obtém o nome do documento.
     *
     * @return O nome do documento.
     */
    fun getDocName(): String{
        return name
    }

    /**
     * Função que obtém os filhos da rootTag.
     *
     * @return Uma lista de filhos da rootTag, ou uma lista vazia se a rootTag ainda não estiver inicializada.
     */
    override fun getChildrenOfTag(): List<XMLChild> {
        return rootTag.getChildrenOfTag()
    }


    /**
     * Função que verifica se uma entidade com o nome especificado existe no documento.
     *
     * @param name O nome da entidade a ser verificada.
     * @return `true` se a entidade existir, caso contrário `false`.
     */
    fun checkIfEntityExists(name: String): Boolean {
        var entityExists = false
        this.accept {
            if ((it is Tag) && it.value == name) {
                entityExists = true
                return@accept true
            }
            true
        }
        return entityExists
    }

    /**
     * Função que executa uma pesquisa XPath simples no documento XML   .
     *
     * @param xpath String do XPath a ser procurado.
     * @return Uma lista de elementos que correspondem à consulta.
     */
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

    /**
     * Função auxiliar da função `microXPath`.
     *
     * @param tag A tag atual que está sendo processada.
     * @param tags A lista de tags do caminho XPath.
     * @param index O índice atual na lista de tags.
     * @param elements A lista de elementos correspondentes encontrados.
     */
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

    /**
     * Funçaõ que procura uma Tag com o nome especificado.
     *
     * @param tagName O nome da tag a ser encontrada.
     * @return A tag encontrada, ou `null` se nenhuma Tag com o nome especificado for encontrada.
     */
    fun findTag(tagName: String): Tag?{
        var tagFound : Tag? = null
        this.accept {
            if(it is Tag && it.value == tagName){
                tagFound = it
                return@accept false
            }
            true
        }
        return tagFound
    }

    //6. Add atributo globalmente e o resto
    /**
     * Função que adiciona um atributo globalmente a todas as tags com o nome especificado.
     *
     *
     * @param parent O nome das tags onde o atributo deve ser adicionado.
     * @param name O nome do atributo a ser adicionado.
     * @param value O valor do atributo a ser adicionado.
     */
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
    /**
     * Função que renomeia uma entidade globalmente do documento.
     *
     * @param oldname O nome antigo da entidade.
     * @param newname O novo nome da entidade.
     * @throws IllegalArgumentException Se a entidade com o nome antigo não existir.
     */
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
    /**
     * Função que renomeia um atributo globalmente do documento, numa determinada Tag.
     *
     * @param parent O nome das Tag's onde o atributo deve ser renomeado.
     * @param oldname O nome antigo do atributo.
     * @param newname O novo nome do atributo.
     * @param newValue O novo valor do atributo (opcional).
     */
    fun renameAttributeGlobally(parent: String, oldname: String, newname: String, newValue: String? = null){
        this.accept {
            if((it is Tag) && it.value.equals(parent)) {
                it.changeAttribute(oldname, newname,newValue)
                return@accept false
            }
            true
        }
    }

    //9. remoção de entidades globalmente ao documento
    /**
     * Função que remove uma entidade globalmente do documento.
     *
     * @param name O nome da entidade a ser removida.
     * @throws IllegalArgumentException Se a entidade com o nome especificado não existir.
     */
    fun removeEntityGlobally(name: String){
        if(checkIfEntityExists(name)) {
            this.accept {
                //println(it)
                if ((it is Tag) && it.value == name) {
                    it.parent?.removeChild(it)
                    return@accept false
                }
                true
            }
        } else throw IllegalArgumentException("Não pode remover uma tag que não existe.")
    }

    //10. remoçao de atributos globalmente ao documento
    /**
     * Função que remove um atributo globalmente do documento.
     *
     * @param parent O nome das Tag's onde o atributo deve ser removido.
     * @param name O nome do atributo a ser removido.
     */
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
    /**
     * Função que gera uma representação formatada do documento XML.
     *
     * @return Uma string contendo o documento XML formatado.
     */
    fun prettyPrint(): String {
        return "$xmlDeclarationText\n" + prettyPrintLine(rootTag,0).trim()
    }

    /**
     *  Função auxiliar da prettyPrint que gera uma linha formatada para o elemento XML especificado.
     *
     * @param child O elemento XML a ser formatado.
     * @param level O nível de indentação.
     * @param isTextChild Indica se o elemento é um filho de texto.
     * @return Uma string contendo o elemento XML formatado.
     */
    private fun prettyPrintLine(child: XMLChild, level: Int, isTextChild: Boolean = false): String {
        val result = StringBuilder()

        when (child) {
            is Tag -> {
                val indent = "\t".repeat(level)
                result.append("$indent<${child.value}")
                if (child.getAttributes().isNotEmpty()) {
                    result.append(" ")
                    child.getAttributes().forEach { attribute ->
                        result.append("${attribute.name}=\"${attribute.value}\" ")
                    }
                    result.deleteCharAt(result.length - 1)
                }
                if (child.getChildrenOfTag().isEmpty()) {
                    result.append("/>\n")
                } else {
                    result.append(">")

                    val hasTextChild = child.getChildrenOfTag().any { it is Text }
                    if (!hasTextChild) {
                        result.append("\n")
                    }
                    if(child.getChildrenOfTag().isNotEmpty()) {
                        val children = StringBuilder()
                        child.getChildrenOfTag().forEach { subChild ->
                            children.append(prettyPrintLine(subChild, level + 1, subChild is Text))
                        }
                        result.append(children)
                    }

                    if (!isTextChild && hasTextChild) {
                        result.append("</${child.value}>\n")
                    } else
                        result.append("$indent</${child.value}>\n")
                }
            }
            is Text -> {
                result.append(child.value)
            }
        }
        return result.toString()
    }


    /**
     * Função que escreve o conteúdo formatado do documento XML em um ficheiro.
     *
     * @param fileName O nome do ficheiro onde o conteúdo XML será escrito.
     */
    fun writeToFile(fileName: String) {
        val content = prettyPrint()
        File(fileName).writeText(content)
    }
}