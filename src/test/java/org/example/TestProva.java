package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestProva{

    @Test
    public void testDiProva() {
        // 1. Arrange: Preparo i dati
        int a = 2;
        int b = 3;

        // 2. Act: Eseguo l'operazione
        int risultato = a + b;

        // 3. Assert: Verifico che il risultato sia quello che mi aspetto
        assertEquals(5, risultato, "Il calcolo deve restituire 5");
    }
}
