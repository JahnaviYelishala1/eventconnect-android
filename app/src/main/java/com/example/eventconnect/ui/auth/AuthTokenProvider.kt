import com.google.firebase.auth.FirebaseAuth

object AuthTokenProvider {

    private var cachedToken: String? = null

    fun getToken(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            onError("User not logged in")
            return
        }

        user.getIdToken(true)
            .addOnSuccessListener {
                cachedToken = it.token
                onSuccess(it.token!!)
            }
            .addOnFailureListener {
                onError(it.message ?: "Token error")
            }
    }

    fun clear() {
        cachedToken = null
    }
}
