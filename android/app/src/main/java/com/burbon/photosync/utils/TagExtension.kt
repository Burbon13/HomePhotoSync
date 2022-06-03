package com.burbon.photosync.utils

val Any.TAG: String
    get() {
        val tag = javaClass.simpleName
        return if (tag.length <= 23) "PHOTO-SYNC: $tag" else "PHOTO-SYNC: ${tag.substring(0, 23)}"
    }
