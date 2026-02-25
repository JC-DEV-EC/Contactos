package app.aplication.appproductos.contact.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.aplication.appproductos.contact.domain.Contact
import app.aplication.appproductos.contact.domain.ContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ContactFormState(
    val id: Int = 0,
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val notes: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)

@HiltViewModel
class ContactDetailViewModel @Inject constructor(
    private val repository: ContactRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val contactId: Int = savedStateHandle["contactId"] ?: -1

    private val _state = MutableStateFlow(ContactFormState())
    val state: StateFlow<ContactFormState> = _state.asStateFlow()

    val isEditing get() = contactId != -1

    init {
        if (isEditing) loadContact()
    }

    private fun loadContact() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val contact = repository.getContactById(contactId)
            contact?.let {
                _state.value = _state.value.copy(
                    id = it.id,
                    name = it.name,
                    email = it.email,
                    phone = it.phone,
                    notes = it.notes,
                    isLoading = false
                )
            }
        }
    }

    fun onNameChange(value: String) {
        _state.value = _state.value.copy(name = value, nameError = null)
    }

    fun onEmailChange(value: String) {
        _state.value = _state.value.copy(email = value, emailError = null)
    }

    fun onPhoneChange(value: String) {
        _state.value = _state.value.copy(phone = value, phoneError = null)
    }

    fun onNotesChange(value: String) {
        _state.value = _state.value.copy(notes = value)
    }

    fun saveContact() {
        val current = _state.value
        var hasError = false

        if (current.name.isBlank()) {
            _state.value = _state.value.copy(nameError = "El nombre es requerido")
            hasError = true
        }

        val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        if (current.email.isNotBlank() && !emailPattern.matches(current.email)) {
            _state.value = _state.value.copy(emailError = "Ingresa un email válido")
            hasError = true
        }

        if (current.phone.isNotBlank() && current.phone.length < 7) {
            _state.value = _state.value.copy(phoneError = "Ingresa un teléfono válido")
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            val contact = Contact(
                id = current.id,
                name = current.name.trim(),
                email = current.email.trim(),
                phone = current.phone.trim(),
                notes = current.notes.trim()
            )
            if (isEditing) repository.updateContact(contact)
            else repository.insertContact(contact)

            _state.value = _state.value.copy(isSaved = true)
        }
    }
}
