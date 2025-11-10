# `ui.responsive`

Utilidades para adaptar composables al tamaño disponible.

## Componentes

| Elemento | Descripción |
| --- | --- |
| `Responsive` | Composable contenedor que usa `BoxWithConstraints` para calcular el lado mínimo disponible, aplica `WindowInsets.systemBars` y entrega `UiDims` parametrizados a su `content`. |
| `UiDims` | Data class con medidas derivadas (`spaceXs`, `spaceSm`, `buttonHeight`, `titleSp`, etc.) y factoría `from(minSide)`. |

Las pantallas (por ejemplo `PantallaMenu`) reciben `UiDims` para escalar tipografías, alturas de botones y espacios sin condicionales específicos por dispositivo.