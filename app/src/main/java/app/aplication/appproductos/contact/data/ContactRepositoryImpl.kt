package app.aplication.appproductos.contact.data

import app.aplication.appproductos.contact.domain.Contact
import app.aplication.appproductos.contact.domain.ContactRepository
import app.aplication.appproductos.contact.domain.toDomain
import app.aplication.appproductos.contact.domain.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val dao: ContactDao
) : ContactRepository {

    override fun getAllContacts(): Flow<List<Contact>> =
        dao.getAllContacts().map { list -> list.map { it.toDomain() } }

    override suspend fun getContactById(id: Int): Contact? =
        dao.getContactById(id)?.toDomain()

    override suspend fun insertContact(contact: Contact) =
        dao.insertContact(contact.toEntity())

    override suspend fun updateContact(contact: Contact) =
        dao.updateContact(contact.toEntity())

    override suspend fun deleteContact(contact: Contact) =
        dao.deleteContact(contact.toEntity())

    override suspend fun deleteContactById(id: Int) =
        dao.deleteContactById(id)
}
