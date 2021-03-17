package ando.toolkit

object ReflectionUtils {

    fun <T> initializeClass(clazz: Class<out T>): T {
        try {
            val constructor = clazz.getConstructor()
            return constructor.newInstance()
        } catch (e: NoSuchMethodException) {
            throw RuntimeException("Can't initialize class ${clazz.name}, no <init>()", e)
        }
    }

    fun <T> initializeClassWithArgs(clazz: Class<out T>, vararg args: Pair<Any, Class<*>>): T {
        val (argList, argTypes) = args.unzip()

        try {
            val constructor = clazz.getConstructor(*argTypes.toTypedArray())
            return constructor.newInstance(*argList.toTypedArray())
        } catch (e: NoSuchMethodException) {
            throw RuntimeException(
                "Can't initialize class ${clazz.name}, no <init>(${argTypes.joinToString()})",
                e
            )
        }
    }

}