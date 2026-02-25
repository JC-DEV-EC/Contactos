package app.aplication.appproductos.contact.domain

import app.aplication.appproductos.contact.data.ContactEntity

data class Contact(
    val id: Int = 0,
    val name: String,
    val email: String,
    val phone: String,
    val notes: String
)

// Mappers
fun Contact.toEntity() = ContactEntity(
    id = id, name = name, email = email, phone = phone, notes = notes
)

fun ContactEntity.toDomain() = Contact(
    id = id, name = name, email = email, phone = phone, notes = notes
)
