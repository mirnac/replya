package com.replya.component.ai;

import com.replya.domain.ServiceItem;
import com.replya.domain.Tenant;
import com.replya.domain.TenantConfig;
import org.springframework.stereotype.Component;

/**
 * Created by Mirna Cantero
 * Date: 2026-06-29
 */
// PromptBuilder.java — el "cerebro" estático de cada tenant, inyectado acá
@Component
public class PromptBuilder {

    public String systemPrompt(Tenant tenant, TenantConfig cfg) {

        String botName = cfg.getBotName() != null ? cfg.getBotName() : "Asistente";
        String tone = cfg.getTone() != null ? cfg.getTone() : "amable, breve y profesional";
        String name = tenant.getDisplayName();

        StringBuilder sb = new StringBuilder();
        sb.append("Sos ").append(botName).append(", el asistente de WhatsApp de \"")
                .append(name).append("\", una estética en Paraguay.\n\n");

        sb.append("Tu único rol es atender a los clientes de este negocio: responder consultas ")
                .append("sobre servicios, precios, horarios y ubicación, y ayudar a coordinar turnos. ")
                .append("Hablás en español, con un tono ").append(tone).append(".\n\n");

        sb.append("REGLAS:\n")
                .append("- Respondé SOLO sobre ").append(name).append(". Si te preguntan algo ajeno al ")
                .append("negocio (temas generales, otras empresas, pedidos de código, opiniones, etc.), ")
                .append("redirigí con amabilidad: aclarás que solo podés ayudar con consultas de ").append(name).append(".\n")
                .append("- Usá ÚNICAMENTE la información de abajo. No inventes precios, servicios ni horarios. ")
                .append("Si no sabés algo, decí que pueden consultarlo directamente con el local.\n")
                .append("- Sé breve y natural para WhatsApp. Nada de listas largas salvo que pidan ver todos los servicios.\n")
                .append("- Si quieren reservar, pedí (si falta) el servicio, el día y horario preferido, y el nombre. ")
                .append("Aclarales que la reserva queda sujeta a confirmación del local; vos no confirmás turnos automáticamente.\n\n");

        sb.append("SERVICIOS:\n");
        if (cfg.getServices() != null && !cfg.getServices().isEmpty()) {
            for (ServiceItem s : cfg.getServices()) {
                sb.append("- ").append(s.nombre());
                if (s.precio() != null)   sb.append(" — ").append(s.precio());
                if (s.duracion() != null) sb.append(" (").append(s.duracion()).append(")");
                sb.append("\n");
            }
        } else sb.append("(no especificados)\n");

        sb.append("\nHORARIOS:\n");
        if (cfg.getBusinessHours() != null && !cfg.getBusinessHours().isEmpty()) {
            cfg.getBusinessHours().forEach((k, v) -> sb.append("- ").append(k).append(": ").append(v).append("\n"));
        } else sb.append("(no especificados)\n");

        if (cfg.getAddress() != null)     sb.append("\nUBICACIÓN: ").append(cfg.getAddress());
        if (cfg.getMapsUrl() != null)     sb.append(" (").append(cfg.getMapsUrl()).append(")");
        if (cfg.getPaymentInfo() != null) sb.append("\nPAGOS: ").append(cfg.getPaymentInfo());
        if (cfg.getExtraInfo() != null)   sb.append("\nINFO ADICIONAL: ").append(cfg.getExtraInfo());

        return sb.toString();
    }
}