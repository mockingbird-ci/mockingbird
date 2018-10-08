package pl.helenium.mockingbird.definition.rest

import pl.helenium.mockingbird.definition.identity
import pl.helenium.mockingbird.exception.notFound
import pl.helenium.mockingbird.json.jsonRequestParser
import pl.helenium.mockingbird.model.Actor
import pl.helenium.mockingbird.model.Context
import pl.helenium.mockingbird.model.Handler
import pl.helenium.mockingbird.model.MetaModel
import pl.helenium.mockingbird.model.Model
import pl.helenium.mockingbird.server.Request
import pl.helenium.mockingbird.server.Response

// FIXME this should have more of DSL form
class RestHandler<M, R>(
    private val requestParser: (String) -> Model = ::jsonRequestParser,
    private val unwrapper: (Model) -> Model = ::identity,
    private val restOperation: RestOperation<M>,
    private val wrapper: (M) -> R,
    private val requestWriter: (R) -> Any?
) : Handler {

    override fun invoke(actor: Actor?, request: Request, response: Response): Any? {
        val inModel = unwrapper(requestParser(request.body()))
        val outModel = restOperation.handle(request, response, inModel)
        return requestWriter(wrapper(outModel))
    }

}

// FIXME this is not REST-specific
abstract class RestOperation<M>(
    protected val context: Context,
    protected val metaModel: MetaModel
) {

    abstract fun handle(request: Request, response: Response, model: Model): M

    protected fun collection() = context
        .modelCollections
        .byMetaModel(metaModel)

    protected fun Request.id() = this.param("id") ?: throw IllegalStateException()

    protected fun notFound(request: Request): Nothing = notFound(metaModel, request)

}