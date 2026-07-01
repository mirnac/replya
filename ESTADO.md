Replya — Estado y próximas tareas

Archivo de estado puntual. Actualizalo a medida que avanzás. El contexto durable del proyecto
(decisiones, restricciones, convenciones) está en CLAUDE.md.

Última actualización: 2026-06-30

Plan de build (7 pasos)

✅ Conectar Cloud API con número de prueba — webhook recibiendo + envío funcionando.
PROBADO de punta a punta vía Postman (recepción → ruteo por phone_number_id → Graph API → mensaje llega).
✅ Schema en Supabase — tablas creadas en schema replya + tenant de muestra cargado.
   Nota: cada tabla ahora tiene id numérico (bigint identity) como PK + columna uuid única.
🔄 Loop webhook → Claude → respuesta con config del tenant inyectado — CÓDIGO COMPLETO, cableado
   de punta a punta. Falta corregir un bug y probarlo contra la DB real (ver "Bloqueantes").
🔄 Estado de conversación (últimos N mensajes) — implementado dentro del paso 3
   (ConversationService.loadHistory + dedup por wamid). Sin probar contra DB real.
⬜ Captura de turno → registro en tabla appointments (status pending_review).
   La tabla existe; falta entity Appointment, repo, y la lógica de captura de intención.
⬜ Deploy (Railway).
⬜ Probar con config de estética de muestra hasta que quede impecable.

Lo que ya está en el código (main compila limpio)

- whatsapp-gateway: WebhookController (GET verificación + POST recepción), MessageHandlerImpl (@Async,
  parsea payload, filtra statuses, solo texto), MessageSender (Graph API vía RestClient).
- Orquestación: ConversationOrchestrator — dedup wamid → carga tenant → valida activo → carga config →
  arma historial + mensaje actual → llama a Claude FUERA de tx → persiste en tx corta → responde.
- ai-engine: ClaudeClient (RestClient a /v1/messages, Haiku) + PromptBuilder (system prompt con
  servicios y horarios del tenant inyectados).
- tenant-config: entities Tenant, TenantConfig (services/business_hours como jsonb con
  @JdbcTypeCode(SqlTypes.JSON)), ServiceItem; repos Tenant/TenantConfig/Conversation.
- Persistencia de conversación: ConversationService (loadHistory paginado, persistExchange @Transactional).

Resuelto en esta sesión

✅ [BUG] ConversationOrchestrator: `configRepo.findById(tenant.getId())` buscaba TenantConfig por su PK
   surrogate. Corregido → ahora usa `findByTenantId(tenant.getId())` (método nuevo en TenantConfigRepository).
✅ [BUILD] El test contextLoads() pasa. `./gradlew build` queda en VERDE. Se levanta un Postgres real
   con Testcontainers (@ServiceConnection): Flyway corre las migraciones y el contexto completo carga.
   Se quitó el `spring.autoconfigure.exclude=DataSourceAutoConfiguration` del application.properties de
   test (era un parche viejo que impedía crear el DataSource).
✅ [RAILWAY sin Docker] El test usa @Testcontainers(disabledWithoutDocker = true): local (con Docker)
   corre y valida contra Postgres real; en Railway/CI sin Docker se SALTEA solo, así que `./gradlew build`
   pasa en el deploy sin depender de Docker. Los tests de integración se corren en la máquina/CI con Docker.

Bloqueantes / próximos pasos (en orden)

1. Probar el loop de punta a punta contra la DB real: mensaje entrante → respuesta de Claude con la
   config de "Estética Bella" → conversación persistida. (El código está completo y el build pasa;
   falta el humo de integración real con tokens de verdad.)

2. Paso 5 del plan: captura de turno. Falta entity Appointment + repo + lógica para detectar intención
   y registrar en appointments (status pending_review). Recordar: NO agendar, solo capturar.

3. Deploy a Railway (mismo proyecto que Keylia, código separado). Cargar env vars: WHATSAPP_ACCESS_TOKEN,
   ANTHROPIC_API_KEY, JDBC_DATABASE_URL/USER/PASSWORD (Session pooler de Supabase, NUNCA Transaction pooler).

Hardening pendiente (antes de prod, NO bloquea el MVP — ver CLAUDE.md)

- Validación de firma X-Hub-Signature-256 sobre el POST del webhook.
- Token permanente de WhatsApp vía System User (el de prueba dura 24h).
