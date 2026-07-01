package com.togamma.heladeria.service.seguridad;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ActiveSessionService {

    private final ConcurrentHashMap<String, String> activeTokens = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> lastActivityTimestamps = new ConcurrentHashMap<>();

    // Umbral de inactividad: 30 segundos (30000 milisegundos)
    private static final long INACTIVITY_THRESHOLD = 30000;

    /**
     * Intenta registrar una nueva sesión para un usuario.
     * Si ya tiene una sesión activa y reciente, rechaza el login retornando false.
     */
    public synchronized boolean registerSession(String username, String token) {
        long now = System.currentTimeMillis();

        if (activeTokens.containsKey(username)) {
            Long lastActivity = lastActivityTimestamps.get(username);
            if (lastActivity != null && (now - lastActivity < INACTIVITY_THRESHOLD)) {
                return false; // Ya tiene sesión activa en otro dispositivo
            }
        }

        // Registrar/Sobrescribir sesión
        activeTokens.put(username, token);
        lastActivityTimestamps.put(username, now);
        return true;
    }

    /**
     * Actualiza el timestamp de última actividad si el token es el activo.
     */
    public void updateActivity(String username, String token) {
        String activeToken = activeTokens.get(username);
        if (token.equals(activeToken)) {
            lastActivityTimestamps.put(username, System.currentTimeMillis());
        }
    }

    /**
     * Verifica si un token específico es el token activo actualmente.
     */
    public boolean isTokenActive(String username, String token) {
        String activeToken = activeTokens.get(username);
        return token != null && token.equals(activeToken);
    }

    /**
     * Cierra la sesión activa de un usuario de forma explícita.
     */
    public void removeSession(String username) {
        activeTokens.remove(username);
        lastActivityTimestamps.remove(username);
    }
}
