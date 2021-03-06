package ds.bindingtools

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.support.annotation.AnyRes
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

inline fun <reified T : Any> Context.res(@AnyRes id: Int): ReadOnlyProperty<Activity, T> = ResourcesDelegate(this, id, T::class)
inline fun <reified T : Any> Fragment.res(@AnyRes id: Int): ReadOnlyProperty<Activity, T> = ResourcesDelegate(this.context, id, T::class)

class ResourcesDelegate<out T : Any>(private val context: Context, private val id: Int, private val cls: KClass<T>) : ReadOnlyProperty<Any, T> {
    private lateinit var type: String

    @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        type = context.resources.getResourceTypeName(id)
        with(context.resources) {
            return when {
                match("drawable", Drawable::class) -> ContextCompat.getDrawable(context, id)
                match("bool", Boolean::class) -> getBoolean(id)
                match("integer", Int::class) -> getInteger(id)
                match("color", Int::class) -> ContextCompat.getColor(context, id)
                match("color", ColorStateList::class) -> ContextCompat.getColorStateList(context, id)
                match("dimen", Float::class) -> getDimension(id)
                match("dimen", Int::class) -> getDimension(id)
                match("string", String::class) -> getString(id)
                match("string", CharSequence::class) -> getText(id)
                match("array", IntArray::class) -> getIntArray(id)
                match("array", Array<Int>::class) -> getIntArray(id)
                match("array", Array<String>::class) -> getStringArray(id)
                match("array", Array<CharSequence>::class) -> getTextArray(id)
                else -> throw IllegalArgumentException()
            } as T
        }
    }

    private fun match(desiredType: String, desiredClass: KClass<*>) = desiredType == type && desiredClass.java == cls.java

}
