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

    companion object {
        private const val MAX_NAME_LENGTH = 50
        private const val MAX_EMAIL_LENGTH = 100
        private const val MAX_PHONE_LENGTH = 10
        private const val MAX_NOTES_LENGTH = 300

        private val EMAIL_PATTERN = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }

    private fun filterName(input: String): String =
        input
            .filter { it.isLetter() || it == ' ' || it == '\'' || it == '-' }
            .replace(Regex(" +"), " ")
            .take(MAX_NAME_LENGTH)

    private fun filterEmail(input: String): String =
        input
            .trim()
            .filter { it.isLetterOrDigit() || it in "@._+-" }
            .take(MAX_EMAIL_LENGTH)

    private fun filterPhone(input: String): String =
        input
            .filter { it.isDigit() }
            .take(MAX_PHONE_LENGTH)

    private fun filterNotes(input: String): String =
        input
            .filter { ch ->
                // Quitar caracteres de control / null bytes y bloquear < > como medida básica anti-inyección (XSS/HTML)
                (ch == '\n' || ch == '\t' || ch.code >= 32) &&
                    ch != '<' && ch != '>' && ch != '\u0000'
            }
            .take(MAX_NOTES_LENGTH)

    init {
        if (isEditing) loadContact()
    }

    private fun loadContact() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val contact = repository.getContactById(contactId)

            if (contact == null) {
                _state.value = _state.value.copy(isLoading = false)
                return@launch
            }

            _state.value = _state.value.copy(
                id = contact.id,
                name = filterName(contact.name),
                email = filterEmail(contact.email),
                phone = filterPhone(contact.phone),
                notes = filterNotes(contact.notes),
                isLoading = false
            )
        }
    }

    fun onNameChange(value: String) {
        // Solo letras, espacios, guion y apóstrofe.
        val filtered = filterName(value)
        _state.value = _state.value.copy(name = filtered, nameError = null)
    }

    fun onEmailChange(value: String) {
        // Solo caracteres válidos para email.
        val filtered = filterEmail(value)
        _state.value = _state.value.copy(email = filtered, emailError = null)
    }

    fun onPhoneChange(value: String) {
        // Solo dígitos, máximo 10.
        val filtered = filterPhone(value)
        _state.value = _state.value.copy(phone = filtered, phoneError = null)
    }

    fun onNotesChange(value: String) {
        // Texto libre con sanitización básica y límite de longitud.
        val filtered = filterNotes(value)
        _state.value = _state.value.copy(notes = filtered)
    }

    fun saveContact() {
        val current = _state.value

        // Normalizar/filtrar nuevamente por seguridad (protección contra input malicioso / pegado de texto)
        val normalizedName = filterName(current.name).trim()
        val normalizedEmail = filterEmail(current.email).trim()
        val normalizedPhone = filterPhone(current.phone).trim()
        val normalizedNotes = filterNotes(current.notes).trim()

        _state.value = current.copy(
            name = normalizedName,
            email = normalizedEmail,
            phone = normalizedPhone,
            notes = normalizedNotes,
            nameError = null,
            emailError = null,
            phoneError = null
        )

        var hasError = false

        // Nombre: requerido y solo caracteres permitidos (ya filtrado)
        if (normalizedName.isBlank()) {
            _state.value = _state.value.copy(nameError = "El nombre es requerido")
            hasError = true
        } else if (normalizedName.length < 2) {
            _state.value = _state.value.copy(nameError = "Ingresa al menos 2 letras")
            hasError = true
        }

        // Email: opcional, pero si se ingresa debe ser válido
        if (normalizedEmail.isNotBlank() && !EMAIL_PATTERN.matches(normalizedEmail)) {
            _state.value = _state.value.copy(emailError = "Ingresa un email válido")
            hasError = true
        }

        // Teléfono: solo dígitos, máximo 10, mínimo 7 (si se ingresa)
        if (normalizedPhone.isNotBlank()) {
            when {
                normalizedPhone.length < 7 -> {
                    _state.value = _state.value.copy(phoneError = "Ingresa un teléfono válido (7-10 dígitos)")
                    hasError = true
                }
                normalizedPhone.length > MAX_PHONE_LENGTH -> {
                    _state.value = _state.value.copy(phoneError = "Máximo $MAX_PHONE_LENGTH dígitos")
                    hasError = true
                }
            }
        }

        if (hasError) return

        viewModelScope.launch {
            val contact = Contact(
                id = current.id,
                name = normalizedName,
                email = normalizedEmail,
                phone = normalizedPhone,
                notes = normalizedNotes
            )
            if (isEditing) repository.updateContact(contact)
            else repository.insertContact(contact)

            _state.value = _state.value.copy(isSaved = true)
        }
    }
}
