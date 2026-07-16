# 🎯 Sniper

**Sniper** es una herramienta de precisión ultra-rápida diseñada para capturar colores del mundo real al instante utilizando la cámara de tu dispositivo. Es el accesorio premium definitivo diseñado para integrarse de forma nativa con el ecosistema de **HexColor PRO**.

<p align="center">
  <img src="app/src/main/res/drawable/sniper_icono_nuevootro.png" width="150" alt="Sniper Logo">
</p>

---

## ✨ Características Principales

* **Extracción de Precisión (Sniper Reticle):** Abre la app e identifica cualquier color al instante apuntando con la mira de precisión central en tiempo real.
* **Integración Directa con HexColor PRO:** Con un solo toque en el botón dorado, el color capturado viaja automáticamente a la base de datos de favoritos de tu app principal sin pasos intermedios.
* **Rendimiento Ultra-Ligero:** Diseñada exclusivamente para la captura inmediata: abrir, apuntar, guardar y listo.
* **Interfaz Premium:** Diseñada en Jetpack Compose con una estética oscura y dorada a juego con la suite de utilidades de HexColor.

---

## 🛠️ Cómo Funciona (Bajo el Capó)

Para lograr una comunicación instantánea y limpia entre aplicaciones sin depender de servidores o nubes pesadas, **Sniper** utiliza el sistema nativo de Android **`ContentProvider`**.

---

## 🚀 Arquitectura y Tecnologías

* **Jetpack Compose:** Interfaz de usuario moderna, reactiva y fluida.
* **CameraX (ImageAnalysis):** Análisis de frames en tiempo real de alto rendimiento para extraer el píxel central sin latencia.
* **ContentResolver:** Canal de comunicación inter-procesos (IPC) seguro para inyectar datos de forma transparente en la app principal.
* **Kotlin Coroutines:** Procesamiento asíncrono para mantener la interfaz a 60 FPS estables durante el análisis de cámara.

---

## ⚙️ Integración del Desarrollador

Si necesitas depurar o comprobar la query de inserción que realiza **Sniper** hacia **HexColor PRO**, el canal utiliza la siguiente estructura de datos:

### URI del Proveedor
`content://com.example.hexcolor.provider/favorites`

### Campos de Datos (ContentValues)
| Clave | Tipo | Descripción | Ejemplo |
| :--- | :--- | :--- | :--- |
| `color_hex` | `String` | Código hexadecimal del color con hash. | `#FFD700` |
| `color_name` | `String` | Nombre identificador autogenerado por la mira. | `Sniper #FFD700` |

---

## 📸 Capturas de Pantalla

<p align="center">
  <img src="https://github.com/user-attachments/assets/cf580dfe-c4ca-4f93-b7ac-b01bfab56ab0" width="30%" />
  <img src="https://github.com/user-attachments/assets/fddf45de-1499-4c38-9dcc-905948716338" width="30%" />
</p>

---

## 📄 Licencia

Este proyecto es propiedad privada de **Jaypsmall** como parte de la suite comercial de aplicaciones de utilidad general. Todos los derechos reservados.
