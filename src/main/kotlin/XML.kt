//VAL É IMUTÁVEL
//VAR É MUTÁVEL

/**
 * Interface que representa um elemento filho em uma estrutura XML.
 *
 * Um elemento filho pode ser tanto uma Tag como um Text
 *
 * @property value Esta propriedade representa o valor atribuído ao XMLChild. No caso do Text é o próprio texto a inserir e
 *                  no caso da Tag é o nome da mesma.
 *
 * @property parent Esta propriedade representa o pai da XMLChild. No caso será uma Tag ou null caso não tenha uma Tag pai.
 *                  A última hipótese só ocorre na rootTag de um documento.
 */
sealed interface XMLChild{
    val value: String
    val parent: Tag?
}

/**
 * Interface que representa um elemento que pode receber um visitante.
 *
 * Esta interface é usada para implementar o padrão de desenho Visitor, permitindo
 * que objetos possam ser "visitados" por um visitante, que executa operações
 * em uma estrutura de objetos.
 */
sealed interface ReceivesVisitor{

    /**
     * Obtém os filhos do elemento atual que são do tipo [XMLChild].
     *
     * @return Uma lista de elementos [XMLChild] que são filhos do elemento atual.
     */
    fun getChildrenOfTag(): List<XMLChild>

    //Implementação da interface visitor
    //XMLParent é o que está a ser visitado (daí implementarmos aqui): o documento (e consequentemente tags)

    /**
     * Aceita um visitante e permite que ele execute operações no elemento atual
     * e nos seus filhos.
     *
     * @param visitor Uma função que representa o visitante, a função retorna um
     * boolean que indica se o visitante deve continuar a visitar os filhos do elemento atual.
     *
     * A implementação padrão verifica se o visitante deseja continuar a visita (retornando true).
     * Se sim, cria uma cópia da lista de filhos para evitar problemas de modificação
     * concorrente durante a iteração. Cada filho que também implementa [ReceivesVisitor]
     * chama recursivamente o método accept para permitir que o visitante opere na hierarquia.
     */
    fun accept(visitor: (ReceivesVisitor) -> Boolean){
        if (visitor(this)) {
            val childrenCopy = ArrayList(this.getChildrenOfTag())  //estava a dar um erro relativo a iterar uma lista e modificá-la ao mesmo tempo portanto agora estamos a iterar uma cópia e a modificar o original
            childrenCopy.forEach {
                if (it is ReceivesVisitor)  //sem este if teriamos de ter uma função accept tbm no XMLChild
                    it.accept(visitor)
            }
        }
    }
}

/*Notinhas:
- só pode haver uma root tag
- é melhor separar tudo em ficheiros diferentes, em termos de "bom design"? Se sim, tiramos o sealed das interfaces
*/

/**
 * Data class que representa uma Tag em um documento XML.
 *
 * @property value O valor da tag; o seu nome: <value>
 * @property document O documento ao qual esta Tag pertence.
 * @property parent A Tag pai da Tag. No caso de null a Tag é a rootTag do documento.
 */
data class Tag(override var value: String, private val document: Document, override val parent: Tag?=null ) : XMLChild, ReceivesVisitor {

    private val children: MutableList<XMLChild> = mutableListOf()
    private val attributes: MutableList<Attribute> = mutableListOf()
    val doc = document

    init {

        if (parent == null) {
            document.setRootTag(this)
        } else {
            parent.addChild(this)
        }
    }

    /**
     * Função que obtém os filhos da Tag que são do tipo [XMLChild].
     *
     * @return Uma lista de filhos desta tag.
     */
    override fun getChildrenOfTag(): List<XMLChild> {
        return children
    }

    /**
     * Função que adiciona um filho à Tag.
     *
     * @param child O filho a ser adicionado. Podendo ser outra Tag ou Text.
     * @throws IllegalArgumentException Se o filho for um [Text] e esta tag já tiver filhos do tipo [Tag], ou vice-versa.
     */
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

    /**
     * Função que remove um filho da Tag.
     *
     * @param child O filho a ser removido.
     */
    fun removeChild(child: XMLChild) {
        children.remove(child)
        if (child is ReceivesVisitor) {
            child.getChildrenOfTag().forEach {
                removeChild(it)
            }
        }
    }


    /**
     * Função que obtém os atributos de uma Tag.
     *
     * @return Uma lista de atributos da tag.
     */
    fun getAttributes(): List<Attribute>{
        return attributes
    }

    //pensei em devolver Boolean ou então a posição dele se existir
    /**
     * Função que verifica se um atributo com o nome especificado existe.
     *
     * @param name O nome do atributo a ser verificado.
     * @return true se o atributo existir, caso contrário false.
     */
    private fun checkIfAttributeExists(name: String): Boolean{
        if (attributes.any { it.name == name }) {
            return true
        } else {
            return false
        }
    }

    //2. Add, remover e alterar atributos em entidades
    //Podiamos usar ignoreCase = true

    /**
     * Função que adiciona um atributo à Tag.
     *
     * @param name O nome do atributo.
     * @param value O valor do atributo.
     * @throws IllegalArgumentException Se já existir um atributo com o mesmo nome.
     */
    fun addAttribute(name: String, value: String) {
        val atr = Attribute(name, value)
        if (checkIfAttributeExists(name)) {  //verifica se já existe um atributo com este nome
            throw IllegalArgumentException("Não é possivel adicionar o atributo dado, já existe um associado a esta Tag com esse nome.")
        } else attributes.add(atr)
    }

    /**
     * Função que remove um atributo da Tag.
     *
     * @param name O nome do atributo a ser removido.
     * @throws IllegalArgumentException Se não existir um atributo com o nome especificado.
     */
    fun removeAttribute(name: String) {
        if(checkIfAttributeExists(name)){ //caso o atr exista
            val atr = attributes.find { it.name == name }
            attributes.remove(atr)
        } else throw IllegalArgumentException("Não é possivel remover o atributo dado, não existe nenhum associado a esta Tag com esse nome.")
    }

    //isto antes recebia um atributo e um novo nome mas eu mudei para um nome antigo em vez de um objeto atributo, para se usar numa func do documento (e assim n tem de se criar o objeto para lhe acedermos?)
    /**
     * Função que altera um atributo da Tag.
     *
     * @param givenName O nome do atributo a ser alterado.
     * @param newname O novo nome do atributo.
     * @param newValue O novo valor do atributo (opcional).
     * @throws IllegalArgumentException Se não existir um atributo com o nome especificado.
     */
    fun changeAttribute(givenName: String, newname: String, newValue: String? = null) {
        if(checkIfAttributeExists(givenName)){ //caso o atr exista
            val attrToChange = attributes.find { it.name == givenName }
            if (attrToChange != null) {
                attrToChange.name = newname
                if (newValue != null) {
                    attrToChange.value = newValue
                }
            }
        } else throw IllegalArgumentException("Não é possivel alterar o atributo dado, não existe nenhum associado a esta Tag com esse nome.")

    }

}
/**
 * Data class que representa um atributo de uma Tag.
 *
 * @property name O nome do atributo. Não pode conter espaços em branco.
 * @property value O valor do atributo.
 *
 * @throws IllegalArgumentException Se o nome do atributo contiver espaços em branco.
 */
data class Attribute(var name: String, var value: String){
    init{
        if (name.any { it.isWhitespace() }) {
            throw IllegalArgumentException("O nome do atributo não pode conter espaços.")
        }
    }
}

/**
 * Data class que representa um Text em documento XML.
 *
 * @property value O texto a ser inserido.
 * @property parent A tag pai deste Text.
 */
data class Text(override var value: String, override val parent: Tag): XMLChild{
    init {
        parent.addChild(this)
    }
}