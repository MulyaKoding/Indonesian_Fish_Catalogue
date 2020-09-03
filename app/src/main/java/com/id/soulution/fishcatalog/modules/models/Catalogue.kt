package com.id.soulution.fishcatalog.modules.models

import java.io.Serializable

data class Catalogue (
    var uid: String = "",
    var user_id: String = "",
    var categories: MutableList<Category> = arrayListOf(),
    var name: String = "",
    var description: String = "",
    var location: String = "",
    var type: Int = -1,
    var uri: String = ""
): Serializable