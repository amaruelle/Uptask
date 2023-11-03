package org.leftbrained.uptaskapp.classes

import kotlinx.serialization.Serializable

@Serializable
data class Tag (
    var name: String = "",
    var color: String = "#f0f0f0"
)
