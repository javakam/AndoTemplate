package ando.library.utils.glide

import android.graphics.drawable.Drawable
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition

/**
 * Make new [RequestListener] with lambda for use with [Glide].
 * [Glide] recommend best way is create a single instance of an exception handler
 * per type of request (usually activity/fragment) rather than
 * pass one in per request to avoid some redundant object allocation.
 *
 * @param [R] The type of resource being loaded.
 * @param onResourceReady The onResourceReady will be invoked when a load completes successfully,
 * immediately before {@link Target#onResourceReady([Object], [Transition])}.
 * @param onLoadFailed The onLoadFailed will be invoked when an exception occurs during a load,
 * immediately before {@link Target#onLoadFailed([Drawable])}
 * @return The [RequestListener]
 */
inline fun <R> makeRequestListener(
    crossinline onResourceReady: (R, Any?, Target<R>?, DataSource?, Boolean) -> Boolean,
    crossinline onLoadFailed: (GlideException?, Any?, Target<R>?, Boolean) -> Boolean
): RequestListener<R> {
    return object : RequestListener<R> {
        override fun onResourceReady(
            resource: R,
            model: Any?,
            target: Target<R>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            return onResourceReady.invoke(resource, model, target, dataSource, isFirstResource)
        }

        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<R>?,
            isFirstResource: Boolean
        ): Boolean {
            return onLoadFailed.invoke(e, model, target, isFirstResource)
        }
    }
}

/**
 * Create a new [CustomTarget] and to load the resource in its original size
 * into the CustomTarget with lambda.
 *
 * Example :
 * ```
 * Glide.with(activity)
 * .load("https://www.example.com/image.jpg")
 * .intoCustomTarget({ resource, transition ->
 *     // handle resource
 * }, { drawable ->
 *     // handle error drawable
 * })
 * ```
 *
 * @param [T] The type of resource that will be delivered to the [CustomTarget].
 *
 * @param onResourceReady The onResourceReady will be invoked when the resource load has finished.
 * See also {@link Target#onResourceReady([Object], [Transition])}
 *
 * @param onLoadFailed The onLoadFailed will be invoked when a load fails.
 * See also {@link Target#onLoadFailed([Drawable])}
 *
 * @param onLoadStarted The onLoadStarted will be invoked when a load is started.
 * See also {@link Target#onLoadStarted([Drawable])}
 *
 * @param onLoadCleared The onLoadCleared will be invoked when a load is cancelled
 * and its resources are freed. See also {@link Target#onLoadCleared([Drawable])}
 *
 * @return The [CustomTarget]
 */
inline fun <T> RequestBuilder<T>.intoCustomTarget(
    crossinline onResourceReady: (T, Transition<in T>?) -> Unit,
    crossinline onLoadFailed: (Drawable?) -> Unit = {},
    crossinline onLoadStarted: (Drawable?) -> Unit = {},
    crossinline onLoadCleared: (Drawable?) -> Unit = {}
): CustomTarget<T> {
    return into(object : CustomTarget<T>() {
        override fun onLoadStarted(placeholder: Drawable?) {
            onLoadStarted.invoke(placeholder)
        }

        override fun onResourceReady(resource: T, transition: Transition<in T>?) {
            onResourceReady.invoke(resource, transition)
        }

        override fun onLoadFailed(errorDrawable: Drawable?) {
            onLoadFailed.invoke(errorDrawable)
        }

        override fun onLoadCleared(placeholder: Drawable?) {
            onLoadCleared.invoke(placeholder)
        }
    })
}

/**
 * Create a new [CustomTarget] and to load the resource with [width] and [height]
 * as the requested size into the CustomTarget with lambda.
 *
 * Example :
 * ```
 * Glide.with(activity)
 * .load("https://www.example.com/image.jpg")
 * .intoCustomTarget(100, 100, { resource, transition ->
 *     // handle resource
 * }, { drawable ->
 *     // handle error drawable
 * })
 * ```
 *
 * @param [T] The type of resource that will be delivered to the [CustomTarget].
 * @param width The requested width (> 0, or == [Target.SIZE_ORIGINAL]).
 * @param height The requested height (> 0, or == [Target.SIZE_ORIGINAL]).
 *
 * @param onResourceReady The onResourceReady will be invoked when the resource load has finished.
 * See also {@link Target#onResourceReady([Object], [Transition])}
 *
 * @param onLoadFailed The onLoadFailed will be invoked when a load fails.
 * See also {@link Target#onLoadFailed([Drawable])}
 *
 * @param onLoadStarted The onLoadStarted will be invoked when a load is started.
 * See also {@link Target#onLoadStarted([Drawable])}
 *
 * @param onLoadCleared The onLoadCleared will be invoked when a load is cancelled
 * and its resources are freed. See also {@link Target#onLoadCleared([Drawable])}
 *
 * @return The [CustomTarget]
 */
inline fun <T> RequestBuilder<T>.intoCustomTarget(
    width: Int,
    height: Int,
    crossinline onResourceReady: (T, Transition<in T>?) -> Unit,
    crossinline onLoadFailed: (Drawable?) -> Unit = {},
    crossinline onLoadStarted: (Drawable?) -> Unit = {},
    crossinline onLoadCleared: (Drawable?) -> Unit = {}
): CustomTarget<T> {
    return into(object : CustomTarget<T>(width, height) {
        override fun onLoadStarted(placeholder: Drawable?) {
            onLoadStarted.invoke(placeholder)
        }

        override fun onResourceReady(resource: T, transition: Transition<in T>?) {
            onResourceReady.invoke(resource, transition)
        }

        override fun onLoadFailed(errorDrawable: Drawable?) {
            onLoadFailed.invoke(errorDrawable)
        }

        override fun onLoadCleared(placeholder: Drawable?) {
            onLoadCleared.invoke(placeholder)
        }
    })
}

/**
 * Create a new [CustomViewTarget] and to load the resource in its original size
 * into the CustomViewTarget with lambda.
 *
 * Example :
 * ```
 * Glide.with(imageView)
 * .load("https://www.example.com/image.jpg")
 * .intoCustomViewTarget(imageView, { resource, transition ->
 *     // handle resource
 * }, {
 *    // handle error drawable
 * })
 * ```
 *
 * @param [T] The specific subclass of view wrapped by this target
 * (e.g. {@link android.widget.ImageView})
 * @param [Z] The resource type this [CustomViewTarget] will receive.
 *
 * @param onResourceReady The onResourceReady will be invoked when the resource load has finished.
 * See also {@link Target#onResourceReady([Object], [Transition])}
 *
 * @param onLoadFailed The onLoadFailed will be invoked when a load fails.
 * See also {@link Target#onLoadFailed([Drawable])}
 *
 * @param onResourceLoading The onResourceLoading will be invoked when a resource load is started.
 * See also {@link CustomViewTarget#onLoadStarted([Drawable])}
 *
 * @param onResourceCleared The onResourceCleared will be invoked when the resource is no longer
 * valid and must be freed See also {@link CustomViewTarget#onResourceCleared([Drawable])}
 *
 * @return The [CustomViewTarget]
 */
inline fun <T : View, Z> RequestBuilder<Z>.intoCustomViewTarget(
    view: T,
    crossinline onResourceReady: (Z, Transition<in Z>?) -> Unit,
    crossinline onLoadFailed: (Drawable?) -> Unit = {},
    crossinline onResourceLoading: (Drawable?) -> Unit = {},
    crossinline onResourceCleared: (Drawable?) -> Unit = {}
): CustomViewTarget<T, Z> {
    return into(object : CustomViewTarget<T, Z>(view) {
        override fun onResourceLoading(placeholder: Drawable?) {
            onResourceLoading.invoke(placeholder)
        }

        override fun onLoadFailed(errorDrawable: Drawable?) {
            onLoadFailed.invoke(errorDrawable)
        }

        override fun onResourceReady(resource: Z, transition: Transition<in Z>?) {
            onResourceReady.invoke(resource, transition)
        }

        override fun onResourceCleared(placeholder: Drawable?) {
            onResourceCleared.invoke(placeholder)
        }
    })
}