package com.example.financemanager.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val slashFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")

object LocalDateSlashSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDateSlash", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.format(slashFormatter))
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        val str = decoder.decodeString()
        return LocalDate.parse(str, slashFormatter)
    }
}
