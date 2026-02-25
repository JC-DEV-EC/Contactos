package app.aplication.appproductos.contact.domain

import kotlinx.coroutines.flow.Flow

interface ContactRepository {
    fun getAllContacts(): Flow<List<Contact>>
    suspend fun getContactById(id: Int): Contact?
    suspend fun insertContact(contact: Contact)
    suspend fun updateContact(contact: Contact)
    suspend fun deleteContact(contact: Contact)
    suspend fun deleteContactById(id: Int)
}
