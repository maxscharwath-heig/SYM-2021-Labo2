package ch.heigvd.sym_labo2.com

interface CommunicationEventListener {
    fun handleServerResponse(response: ByteArray)
}