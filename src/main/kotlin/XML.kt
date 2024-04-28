import java.io.File

//VAL É IMUTÁVEL
//VAR É MUTÁVEL


sealed interface XMLChild{
    val value: String
    val parent: Tag
}

sealed interface ReceivesVisitor{
    fun getChildrenOfTag(): List<XMLChild>

    //Implementação da interface visitor
    //XMLParent é o que está a ser visitado (daí implementarmos aqui): o documento (e consequentemente tags)

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


data class Tag(override var value: String, override val parent: Tag): XMLChild, ReceivesVisitor {

    private val children: MutableList<XMLChild> = mutableListOf()
    private val attributes: MutableList<Attribute> = mutableListOf()

    init {
        parent.addChild(this)
    }

    override fun getChildrenOfTag(): List<XMLChild> {
        return children
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
        if (child is ReceivesVisitor) {
            child.getChildrenOfTag().forEach {
                removeChild(it)
            }
        }
    }


    fun getAttributes(): List<Attribute>{
        return attributes
    }

    //pensei em devolver Boolean ou então a posição dele se existir
    fun checkIfAttributeExists(name: String): Boolean{
        if (attributes.any { it.name == name }) {
            return true
        } else {
            return false
        }
    }

    //2. Add, remover e alterar atributos em entidades
    //Podiamos usar ignoreCase = true
    fun addAttribute(name: String, value: String) {
        val atr = Attribute(name, value)
        if (checkIfAttributeExists(name)) {  //verifica se já existe um atributo com este nome
            throw IllegalArgumentException("Não é possivel adicionar o atributo dado, já existe um associado a esta Tag com esse nome.")
        } else attributes.add(atr)
    }

    fun removeAttribute(name: String) {
        if(checkIfAttributeExists(name)){ //caso o atr exista
            val atr = attributes.find { it.name == name }
            attributes.remove(atr)
        } else throw IllegalArgumentException("Não é possivel remover o atributo dado, não existe nenhum associado a esta Tag com esse nome.")
    }

    //isto antes recebia um atributo e um novo nome mas eu mudei para um nome antigo em vez de um objeto atributo, para se usar numa func do documento (e assim n tem de se criar o objeto para lhe acedermos?)
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

data class Attribute(var name: String, var value: String){
    init{
        if (name.any { it.isWhitespace() }) {
            throw IllegalArgumentException("O nome do atributo não pode conter espaços.")
        }
    }
}

data class Text(override var value: String, override val parent: Tag): XMLChild{
    init {
        parent.addChild(this)
    }
}
