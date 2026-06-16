package org.example.mediamusicplayer.service.command;

public interface Command {
    void execute();
    void undo();
}