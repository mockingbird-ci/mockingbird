package pl.helenium.mockingbird.model

import pl.helenium.mockingbird.exception.NotFoundException
import java.util.concurrent.ConcurrentHashMap

class ModelCollection(private val metaModel: MetaModel) {

    private val models = ConcurrentHashMap<String, Model>()

    fun create(model: Model): Model {
        val mutableModel = model.toMutable()
        val idProperty = metaModel.id()
        val generatedId = idProperty.generate()
            ?: throw IllegalStateException("ID property has no generator specified!")
        mutableModel.setProperty(idProperty.name, generatedId)
        models[generatedId.toString()] = mutableModel
        return mutableModel
    }

    fun list(): Collection<Model> = models.values

    fun get(id: Any): Model? = models[id.toString()]

    fun update(id: Any, update: Model, updater: Updater = NaiveUpdater): Model {
        return models.computeIfPresent(id.toString()) { _, existingModel ->
            updater.update(existingModel, update)
        } ?: throw NotFoundException()
    }

    fun delete(id: Any) = models.remove(id.toString())

}

interface Updater {

    fun update(target: Model, source: Model): MutableModel

}

object NaiveUpdater : Updater {

    override fun update(target: Model, source: Model): MutableModel = source.toMutable()

}