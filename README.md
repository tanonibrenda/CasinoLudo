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

Todo está en el paquete `com.grupo1.casinoludo`:

| Archivo | Qué hace |
|---|---|
| `MainActivity.kt` | Bienvenida con imagen y botón JUGAR |
| `BienvenidoActivity.kt` | Reglas del juego y botón Entrar |
| `BaseApuestaActivity.kt` | Carga de las 3 apuestas con validación |
| `GiroRuletaActivity.kt` | Animación de la ruleta (ObjectAnimator) y consulta a la API |
| `ResultadoActivity.kt` | Número ganador, Acierto / No acierto y resumen |
| `Apuesta.kt`, `TipoApuesta.kt` | Una apuesta del jugador y sus tipos |
| `ResultadoRuleta.kt` | Número ganador con color, paridad y docena |
| `LogicaRuleta.kt` | Sorteo, validación, paridad (`%`), docena (`when`) y comparación |
| `RuletaApi.kt` | Retrofit + Gson, igual que la guía de la cátedra |

Las pantallas usan View Binding y los datos pasan entre pantallas con `Intent.putExtra`.

Reglas especiales: el 0 y el 00 son verdes, no tienen paridad ni docena.
El color sale **solo** de la API; paridad y docena se calculan en `LogicaRuleta`.

## API de la cátedra

`GET https://www.menupro.com.ar/api.php?usuario=usuario1&clave=1234567890&bola=7`
→ `{"bola": 7, "color": "rojo", "error": null}`

Implementada en `RuletaApi.kt` (Retrofit + Gson) y llamada con corrutinas desde `GiroRuletaActivity`.
Para ver la petición y el JSON: pestaña **Logcat** filtrando por `okhttp`.

## Tests

`app/src/test/.../LogicaRuletaTest.kt` prueba la lógica del juego
(clic derecho → Run 'LogicaRuletaTest' en Android Studio).
