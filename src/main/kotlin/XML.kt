sealed interface XMLChild{
    val value: String
}


data class Document(val encode: String, val value: String){

    var children: MutableList<XMLChild> = mutableListOf()

}

data class Tag(override val value: String):XMLChild{

    var children: MutableList<XMLChild> = mutableListOf()

}

data class Atribute(var name: String, var value: String){

}

data class Text(override var value: String): XMLChild{

}
