package com.replya.domain;

/**
 * Created by Mirna Cantero
 * Date: 2026-06-29
 */
// ServiceItem.java  (tenant-config) — forma de cada servicio en el jsonb
public record ServiceItem(String nombre, String precio, String duracion) {}