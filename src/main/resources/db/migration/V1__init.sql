-- Flyway migration V1.
-- File naming: V<version>__<description>.sql  (double underscore after the version).
-- Migrations run in version order on app startup and are applied exactly once.
-- Paste your schema DDL below, or rename this file to describe what it does.

-- Example:
-- CREATE TABLE tenant (
--     phone_number_id TEXT PRIMARY KEY,
--     display_name    TEXT NOT NULL,
--     created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
-- );
-- =========================================
-- Replya · Schema inicial (Supabase / Postgres)
-- =========================================
create schema if not exists replya;
-- gen_random_uuid() ya viene habilitado en Supabase

-- ---------- tenants ----------
create table replya.tenants (
                                id               bigint generated always as identity primary key,
                                uuid             uuid not null unique default gen_random_uuid(),  -- id externo/estable
                                phone_number_id  text not null unique,            -- llave de ruteo del webhook
                                waba_id          text,
                                display_name     text not null,                   -- nombre de la estética
                                ruc              text,
                                status           text not null default 'active'
                                    check (status in ('active','paused')),
                                created_at       timestamptz not null default now(),
                                updated_at       timestamptz not null default now()
);

-- ---------- tenant_config ----------
-- El "cerebro" de cada cliente. Estos campos se inyectan en el system prompt.
create table replya.tenant_config (
                                      id               bigint generated always as identity primary key,
                                      uuid             uuid not null unique default gen_random_uuid(),
                                      tenant_id        bigint not null unique references replya.tenants(id) on delete cascade,
                                      bot_name         text not null default 'Asistente',
                                      tone             text not null default 'amable, breve y profesional',
                                      services         jsonb not null default '[]'::jsonb,  -- [{nombre, precio, duracion}]
                                      business_hours   jsonb not null default '{}'::jsonb,  -- {lun_vie, sab, dom}
                                      address          text,
                                      maps_url         text,
                                      payment_info     text,
                                      extra_info       text,                                -- FAQ libre / aclaraciones
                                      updated_at       timestamptz not null default now()
);

-- ---------- conversations (log de mensajes) ----------
create table replya.conversations (
                                      id               bigint generated always as identity primary key,
                                      uuid             uuid not null unique default gen_random_uuid(),
                                      tenant_id        bigint not null references replya.tenants(id) on delete cascade,
                                      customer_wa_id   text not null,                   -- número del cliente final
                                      role             text not null check (role in ('user','assistant')),
                                      content          text not null,
                                      wamid            text,                            -- id de WhatsApp (dedup)
                                      created_at       timestamptz not null default now()
);
create index idx_conversations_thread
    on replya.conversations (tenant_id, customer_wa_id, created_at desc);
create unique index uq_conversations_wamid
    on replya.conversations (wamid) where wamid is not null;

-- ---------- appointments (cola de revisión) ----------
create table replya.appointments (
                                     id                  bigint generated always as identity primary key,
                                     uuid                uuid not null unique default gen_random_uuid(),
                                     tenant_id           bigint not null references replya.tenants(id) on delete cascade,
                                     customer_wa_id      text not null,
                                     customer_name       text,
                                     service             text,
                                     requested_slot      text,                         -- "viernes a la tarde", tal cual lo dijo
                                     requested_datetime  timestamptz,                  -- opcional, para fase 2
                                     status              text not null default 'pending_review'
                                         check (status in ('pending_review','confirmed','rejected','done')),
                                     notes               text,
                                     created_at          timestamptz not null default now(),
                                     updated_at          timestamptz not null default now()
);
create index idx_appointments_review
    on replya.appointments (tenant_id, status, created_at desc);

-- ---------- google_calendar_credentials (andamiaje, fase 2) ----------
create table replya.google_calendar_credentials (
                                                    id                   bigint generated always as identity primary key,
                                                    uuid                 uuid not null unique default gen_random_uuid(),
                                                    tenant_id            bigint not null unique references replya.tenants(id) on delete cascade,
                                                    google_refresh_token text,                        -- guardar cifrado
                                                    google_access_token  text,
                                                    token_expiry         timestamptz,
                                                    calendar_id          text,
                                                    connected_at         timestamptz,
                                                    created_at           timestamptz not null default now(),
                                                    updated_at           timestamptz not null default now()
);

-------------------------------------------------------------------------

with t as (
    insert into replya.tenants (phone_number_id, waba_id, display_name, ruc)
        values ('1267771856409165', '1995050401129213', 'Estética Bella Demo', '2570585-7')
        returning id
)
insert into replya.tenant_config
(tenant_id, bot_name, services, business_hours, address, payment_info, extra_info)
select t.id, 'Asistente de Estética Bella',
       '[{"nombre":"Corte","precio":"60.000 Gs","duracion":"45 min"},
         {"nombre":"Manicura","precio":"50.000 Gs","duracion":"40 min"},
         {"nombre":"Coloración","precio":"180.000 Gs","duracion":"2 hs"}]'::jsonb,
       '{"lun_vie":"9:00-19:00","sab":"8:00-13:00","dom":"cerrado"}'::jsonb,
       'Av. San Blas 1234, Ciudad del Este',
       'Efectivo, transferencia y tarjeta.',
       'Se atiende con reserva. La seña no es obligatoria.'
from t;
