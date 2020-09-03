package com.id.soulution.fishcatalog.modules.models

import java.io.Serializable

class User (
    var uid: String = "",
    var email: String = "",
    var full_name: String = "",
    var phone: String = "",
    var address: String = "",
    var is_register: Int = 0
): Serializable