package excepciones;

public class ParticipanteNoEncontradoException extends EventPlannerException {
    
    public ParticipanteNoEncontradoException(String participanteId) {
        super("Participante no encontrado con ID: " + participanteId, "ERR_ORGANIZADOR_005");
    }
}
