package io.b3.quicktalk

import android.content.Context
import dagger.ObjectGraph

object AppContext {

    // TODO: Check if it is stored in static field
    private var CONTEXT: Context? = null

    private var DI: ObjectGraph? = null

    fun initContext(context: Context) {
        CONTEXT = context
    }

    fun context(): Context? = CONTEXT

    fun addModule(module: AppModule) {
        DI = ObjectGraph.create(module)
    }

    fun inject(target: Any) {
        DI!!.inject(target)
    }

}