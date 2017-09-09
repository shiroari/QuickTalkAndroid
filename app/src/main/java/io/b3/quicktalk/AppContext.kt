package io.b3.quicktalk

import android.app.Activity
import dagger.ObjectGraph

object AppContext {

    fun inject(activity: Activity) {
        ObjectGraph.create(AppModule(activity.applicationContext))
                .inject(activity)
    }

}
