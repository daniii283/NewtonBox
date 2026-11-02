package com.newtonbox.utils;

public enum RoleEnum {
    ADMIN, /* Usuario con control total del sistema: Create, read, update, delete */
    RESEARCHER, /*Usuario que crea y administra sus propios experimentos: Create, read, update*/
    REVIEWER /* Usuario que participa en experimentos, pero con permisos limitados a la revisión y comentarios: Read */
}
