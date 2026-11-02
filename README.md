# Casino Ludo – Ruleta americana

TP Final de Aplicaciones Móviles y Cloud Computing.
Integrantes: Brenda Tanoni, Lucas Orozco.

## Flujo de la app

```
MainActivity (Bienvenida, botón JUGAR)
   → BienvenidoActivity (Reglas, botón Entrar)
   → BaseApuestaActivity: se carga la apuesta 1, 2 y 3 (solo se repite la carga)
   → GiroRuletaActivity: UN giro, número entre 00, 0 y 1–36; el color se pide a la API
   → ResultadoActivity: número ganador + color/paridad/docena,
                        Acierto / No acierto para cada apuesta, resumen,
                        VOLVER A JUGAR (lista vacía) o Salir
```

## Estructura del código

| Paquete | Archivo | Qué hace |
|---|---|---|
| `modelo` | `TipoApuesta.kt` | Número, Color, Paridad, Docena y sus opciones válidas |
| `modelo` | `Apuesta.kt` | Una apuesta (tipo + valor). Serializable para pasarla entre pantallas |
| `modelo` | `ResultadoRuleta.kt` | Número ganador con color, paridad y docena |
| `logica` | `LogicaRuleta.kt` | Sorteo, validación, paridad, docena, comparación de aciertos |
| `red` | `ServicioApiColor.kt` | Retrofit + corrutinas para consultar el color |
| `red` | `RespuestaColor.kt` | JSON que devuelve la API |
| `util` | `Navegacion.kt` | Claves de los Intent y márgenes de pantalla |

Reglas especiales: el 0 y el 00 son verdes, no tienen paridad ni docena.
El color sale **solo** de la API; paridad y docena se calculan en `LogicaRuleta`.

## API de la cátedra

`GET https://www.menupro.com.ar/api.php?usuario=usuario1&clave=1234567890&bola=7`
→ `{"bola": 7, "color": "rojo", "error": null}`

Implementada en `red/ServicioApiColor.kt` (Retrofit + corrutinas) y `red/RespuestaColor.kt` (Gson).
Para ver la petición y el JSON: pestaña **Logcat** filtrando por `okhttp`.

## Tests

`app/src/test/.../LogicaRuletaTest.kt` prueba la lógica del juego
(clic derecho → Run 'LogicaRuletaTest' en Android Studio).
