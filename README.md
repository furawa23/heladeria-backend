# Backend API de Sistema de Gestión de Heladerías/Cafeterías

Sistema integral de gestión para heladerías y cafeterías. Esta API robusta permite el control total del ciclo de negocio: desde la adquisición de insumos con proveedores hasta la venta final al cliente, gestionando múltiples sucursales y flujos de caja en tiempo real.

---

## Módulos del Sistema

El backend está diseñado de forma modular para cubrir todas las áreas de una operación comercial:

* **Gestión de Ventas:** Manejo dinámico de transacciones (`Venta`, `DetalleVenta`), integración con mesas (`Mesa`) y diversos métodos de pago (`TipoMetodoPago`).
* **Inventario y Sabores:** Control de stock por sede (`StockProducto`) y lógica para combinaciones de productos y sabores (`ProductoSabor`, `Sabor`).
* **Compras y Abastecimiento:** Flujo completo de pedidos a proveedores (`Proveedor`, `Compra`, `DetalleCompra`).
* **Control de Caja:** Seguimiento de ingresos y egresos diarios por sucursal (`Caja`, `MovimientoCaja`).
* **Estructura Organizacional:** Soporte multi-sede (`Sucursal`) y seguridad basada en roles (`Usuario`, `Rol`).

## Stack Tecnológico

* **Lenguaje:** Java 21
* **Framework:** Spring Boot (Data JPA, Security, Web)
* **Base de Datos:** PostgreSQ
* **Patrones de Diseño:**
* * **DTO (Data Transfer Object):** Separación estricta entre modelos de persistencia y objetos de entrada/salida (`Request` y `Response`).
    * **Service Layer:** Desacoplamiento de la lógica de negocio mediante interfaces (`Service`) e implementaciones (`ServiceImpl`).
    * **Repository Pattern:** Abstracción de la capa de datos con Spring Data JPA.

## Arquitectura de Paquetes

```text
src/main/java/com/togamma/heladeria/
├── config/              # Spring security, JWT y CORS 
├── controller/          # Endpoints REST (Exposición de la API)
   ├── almacen/
   ├── caja/
   ├── compra/
   ├── sabor/
   ├── seguridad/
   └── venta/
├── service/             # Interfaces de lógica de negocio e Implementaciones de servicios
   ├── almacen/
   ├── caja/
   ├── compra/
   ├── sabor/
   ├── seguridad/
   └── venta/
├── repository/         # Interfaces de acceso a datos (JPA)
   ├── almacen/
   ├── caja/
   ├── compra/
   ├── sabor/
   ├── seguridad/
   └── venta/
├── model/             # Modelos de base de datos (JPA Entities)
   ├── almacen/
   ├── caja/
   ├── compra/
   ├── sabor/
   ├── seguridad/
   └── venta/
└── dto/                  # Objetos de transferencia de datos
    ├── request/          # Payloads de entrada
    └── response/         # Estructuras de salida personalizadas
