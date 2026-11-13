# Datos de prueba — EcoRuta

Guion de pruebas (casos de éxito y de error) pensado para grabarse en **7 minutos**.
Cada fila indica exactamente qué escribir/seleccionar en los `JOptionPane` y qué mensaje debe aparecer.

## 1. Datos precargados al iniciar (`Main.cargarDatosIniciales`)

| Identificación | Nombre | Correo |
|---|---|---|
| 1001 | Juan Perez | juan.perez@mail.com |
| 1002 | Maria Gomez | maria.gomez@mail.com |

## 2. Fórmulas de valor por tipo de lote (para explicar los montos en cámara)

| Tipo de lote | Fórmula | Clase | ¿Genera etiqueta? |
|---|---|---|---|
| Papel/Cartón | `peso * (500 + (clasificado ? 100 : 0))` | `LotePapelCarton` | No |
| Metal – Aluminio | `peso * 2200` | `LoteMetal` | Sí: `Lote: <codigo> \| Metal: <TIPO>` |
| Metal – Acero | `peso * 900` | `LoteMetal` | Sí |
| Residuo electrónico | `peso * 1400 + equiposCompletos * 800` | `LoteElectronico` | Sí: `Lote: <codigo> \| Equipos completos: <n>` |

## 3. Guion cronometrado (total ≈ 6:45, deja ~15s de margen antes de los 7:00)

| # | Tiempo | Menú → Opción | Datos a ingresar | Resultado esperado |
|---|---|---|---|---|
| 1 | 00:00–00:15 | Intro | Mostrar el menú principal `EcoRuta` | Se ven las 3 opciones: Gestionar aportantes / Gestionar entregas / Salir |
| 2 | 00:15–00:30 | Aportantes → Listar aportantes | — | Lista con Juan Perez (1001) y Maria Gomez (1002) |
| 3 | 00:30–00:40 | Aportantes → Buscar aportante | `1001` | `Aportante[identificacion=1001, nombre=Juan Perez, correo=juan.perez@mail.com]` |
| 4 | 00:40–00:50 | Aportantes → Buscar aportante | `9999` | **Error:** `No existe un aportante con esa identificacion.` |
| 5 | 00:50–01:00 | Aportantes → Eliminar aportante | `1002` | `Aportante eliminado.` |
| 6 | 01:00–01:25 | Entregas → Crear entrega | Código `E001`, Aportante `1001`, Fecha `10/11/2026` (formato inválido) → corregir a `2026-11-10` | **Error intermedio:** `Formato de fecha invalido. Use yyyy-MM-dd.` y luego: `Entrega creada en estado ABIERTA.` |
| 7 | 01:25–01:35 | Entregas → Crear entrega | Código `E001` (repetido) | **Error:** `Ya existe una entrega con ese codigo.` |
| 8 | 01:35–01:50 | Entregas → Crear entrega | Código `E002`, Aportante `1002` (recién eliminado) | **Error:** `No existe un aportante con esa identificacion.` |
| 9 | 01:50–02:05 | Entregas → Crear entrega | Código `E002`, Aportante `1001`, Fecha `2026-11-15` | `Entrega creada en estado ABIERTA.` |
| 10 | 02:05–02:25 | Entregas → Agregar lote (sobre `E001`) | Código `L001`, Desc. `Cartón prensado`, Peso `10`, Tipo `Papel/Carton`, ¿Clasificado? `Sí` | `Lote agregado a la entrega.` (valor = 10×600 = **$6.000**) |
| 11 | 02:25–02:45 | Entregas → Agregar lote (sobre `E001`) | Código `L002`, Desc. `Chatarra aluminio`, Peso `5`, Tipo `Metal`, Tipo metal `Aluminio` | `Lote agregado a la entrega.` (valor = 5×2200 = **$11.000**) |
| 12 | 02:45–03:05 | Entregas → Agregar lote (sobre `E001`) | Código `L003`, Desc. `Computadores`, Peso `4`, Tipo `Residuo electronico`, Equipos completos `2` | `Lote agregado a la entrega.` (valor = 4×1400 + 2×800 = **$7.200**) |
| 13 | 03:05–03:20 | Entregas → Agregar lote (sobre `E001`) | Código `L004`, Desc. `Prueba`, Peso `0`, Tipo `Papel/Carton`, ¿Clasificado? `No` | **Error:** `No se pudo agregar el lote (peso invalido o codigo repetido).` |
| 14 | 03:20–03:30 | Entregas → Agregar lote (sobre `E001`) | Código `L001` (repetido), Desc. `Prueba`, Peso `3`, Tipo `Metal`, `Acero` | **Error:** `No se pudo agregar el lote (peso invalido o codigo repetido).` |
| 15 | 03:30–03:45 | Entregas → Actualizar fecha (sobre `E001`) | Nueva fecha `2026-11-12` | `Fecha actualizada.` |
| 16 | 03:45–03:55 | Entregas → Ver comprobante (sobre `E001`) | — (todavía `ABIERTA`) | **Error:** `La entrega aun no tiene comprobante (debe ser aceptada primero).` |
| 17 | 03:55–04:10 | Entregas → Aceptar entrega (sobre `E001`) | — | `Entrega aceptada. Se genero el comprobante.` |
| 18 | 04:10–04:25 | Entregas → Ver comprobante (sobre `E001`) | — | Comprobante entrega E001 / Aportante: 1001 / Peso total: **19 kg** / Valor total: **$24200.00** (o `24200,00` según la configuración regional) |
| 19 | 04:25–04:40 | Entregas → Ver etiquetas (sobre `E001`) | — | Exactamente 2: `Lote: L002 \| Metal: ALUMINIO` y `Lote: L003 \| Equipos completos: 2` (el papel/cartón no genera etiqueta) |
| 20 | 04:40–04:50 | Entregas → Aceptar entrega (sobre `E001`) | — (ya estaba `ACEPTADA`) | **Error:** `No se pudo aceptar la entrega (ya fue aceptada o no tiene lotes).` |
| 21 | 04:50–05:00 | Entregas → Agregar lote (sobre `E001`) | — (entrega ya `ACEPTADA`) | **Error:** `La entrega ya fue aceptada, no admite nuevos lotes.` |
| 22 | 05:00–05:10 | Entregas → Eliminar lote (sobre `E001`) | Código `L001` | **Error:** `No se pudo eliminar el lote.` (la entrega ya no está abierta) |
| 23 | 05:10–05:20 | Entregas → Buscar lote (sobre `E001`) | Código `L999` (no existe) | **Error:** `No existe un lote con ese codigo en la entrega.` |
| 24 | 05:20–05:35 | Entregas → Buscar entrega | Código `E001` | Detalle con Estado `ACEPTADA`, fecha `2026-11-12` y los 3 lotes (`L001`, `L002`, `L003`) |
| 25 | 05:35–05:45 | Entregas → Listar entregas | — | `E001 \| 2026-11-12 \| Juan Perez \| ACEPTADA \| 3 lote(s)` y `E002 \| 2026-11-15 \| Juan Perez \| ABIERTA \| 0 lote(s)` |
| 26 | 05:45–05:55 | Entregas → Aceptar entrega (sobre `E002`) | — (`ABIERTA` y sin lotes) | **Error:** `No se pudo aceptar la entrega (ya fue aceptada o no tiene lotes).` E002 sigue `ABIERTA` |
| 27 | 05:55–06:05 | Entregas → Eliminar entrega | Código `E001` (ya `ACEPTADA`) | **Error:** `No se pudo eliminar: la entrega no existe o ya fue aceptada.` |
| 28 | 06:05–06:15 | Entregas → Eliminar entrega | Código `E002` (`ABIERTA`, sin lotes) | `Entrega eliminada.` |
| 29 | 06:15–06:25 | Aportantes → Valor acumulado | `1001` | `Valor acumulado de 1001: $24200.00` (solo suma entregas aceptadas) |
| 30 | 06:25–06:35 | Aportantes → Valor acumulado | `9999` (sin comprobantes) | `Valor acumulado de 9999: $0.00` |
| — | 06:35–06:45 | Cierre | Salir | Fin de la demo |

## 4. Checklist de casos de error cubiertos

- [ ] Aportante inexistente al buscar (`9999`)
- [ ] Entrega con código duplicado al crear (`E001`)
- [ ] Entrega con aportante inexistente al crear (aportante recién eliminado)
- [ ] Fecha con formato inválido al crear entrega (`10/11/2026`)
- [ ] Lote con peso ≤ 0
- [ ] Lote con código duplicado dentro de la misma entrega
- [ ] Consultar comprobante antes de aceptar la entrega
- [ ] Aceptar una entrega vacía (`E002`)
- [ ] Aceptar una entrega ya aceptada
- [ ] Agregar lote a una entrega ya aceptada
- [ ] Eliminar lote de una entrega ya aceptada
- [ ] Buscar lote inexistente dentro de una entrega
- [ ] Eliminar una entrega ya aceptada
- [ ] Valor acumulado de un aportante sin comprobantes devuelve $0

## 5. Casos extra opcionales (solo si sobra tiempo; el margen es de ~15s)

- **Peso como texto no numérico**: al pedir el peso de un lote, escribir `abc`. El programa
  queda en un ciclo mostrando `Debe ingresar un numero entero valido.` hasta recibir un entero.
- **Equipos electrónicos negativos**: agregar un lote `Residuo electronico` con
  `equiposCompletos = -1` y peso válido (p. ej. `4`). `Entrega.agregarLote` lo rechaza por la
  validación `electronico.getEquiposCompletos() < 0`, pero el mensaje mostrado es el mismo
  genérico (`No se pudo agregar el lote (peso invalido o codigo repetido).`), aunque la causa
  real es la cantidad de equipos negativa. Útil para comentar en el video que el mensaje de
  error no distingue la causa real.
