Replya — Estado y próximas tareas

Archivo de estado puntual. Actualizalo a medida que avanzás. El contexto durable del proyecto
(decisiones, restricciones, convenciones) está en CLAUDE.md.

Plan de build (7 pasos)

✅ Conectar Cloud API con número de prueba — webhook recibiendo + envío funcionando.
PROBADO de punta a punta vía Postman (recepción → ruteo por phone_number_id → Graph API → mensaje llega).
✅ Schema en Supabase — tablas creadas en schema replya + tenant de muestra cargado.
🔄 Loop webhook → Claude → respuesta con config del tenant inyectado — código diseñado, EN INTEGRACIÓN.
🔄 Estado de conversación (últimos N mensajes) — incluido dentro del paso 3 (historial = parte del prompt).
⬜ Captura de turno → registro en tabla appointments (status pending_review).
⬜ Deploy.
⬜ Probar con config de estética de muestra hasta que quede impecable.