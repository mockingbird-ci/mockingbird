package pl.helenium.mockingbird.model

class MetaModels {

    private val metaModels = mutableMapOf<String, MetaModel>()

    fun register(metaModel: MetaModel) {
        metaModels[metaModel.name] = metaModel
    }

    fun byName(name: String) =
        metaModels[name] ?: throw IllegalArgumentException("No MetaModel could be found for name $name!")

}

data class MetaModel(val name: String) {

    private val properties = mutableListOf<Property>()

    fun id() = properties.find { it.id } ?: throw IllegalStateException("MetaModel $name does not have ID!")

    fun dsl() = MetaModelDsl()

    inner class MetaModelDsl {

        fun properties(dsl: PropertiesDsl.() -> Unit) = PropertiesDsl().dsl()

        inner class PropertiesDsl {

            fun id(name: String = "id") = Property(name)
                .id()
                .also { properties.add(it) }

        }

    }

}

class Property(val name: String) {

    var id = false
        private set

    var generate: () -> Any? = { null }
        private set

    fun id() = apply { id = true }

    fun generator(generator: () -> Any?) = apply { this.generate = generator }

}