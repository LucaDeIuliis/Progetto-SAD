package org.example.mediamusicplayer.service.command;


import java.util.Stack;

public class CommandManager {

    private final Stack<Command> undoStack = new Stack<>();

    // Esegue il comando e lo salva nella memoria
    public void executeCommand(Command command) {
        command.execute();
        undoStack.push(command);
    }

    // Annulla l'ultimo comando (se esiste)
    public boolean undoLastCommand() {
        if (!undoStack.isEmpty()) {
            Command lastCommand = undoStack.pop();
            lastCommand.undo();
            return true; // Ritorna true se ha annullato qualcosa
        }
        return false; // Ritorna false se la memoria è vuota
    }

    // Svuota la memoria (es. quando facciamo azioni non annullabili)
    public void clearHistory() {
        undoStack.clear();
    }
}