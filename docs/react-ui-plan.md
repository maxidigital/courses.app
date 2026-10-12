# Plan: reemplazar la interfaz de Telegram por una app React

Estado: **pendiente de ejecución** (definido el 2026-09-08, se implementa otro día).
Branch a crear cuando se arranque: `feat/react-ui`.

---

## 1. Objetivo y decisiones tomadas

Sacar la capa de Telegram de `courses.app` y reemplazarla por una app web en React, con
una **filosofía de interfaz distinta**: no replicar el wizard conversacional, sino una
**consola de operaciones** (panorama + manipulación directa).

Decisiones cerradas con el dueño:

| Tema | Decisión |
|---|---|
| **Alcance v1** | (a) **Solo cursos**: panel de próximos cursos + detalle de curso + envío de emails. Sin columna de formularios médicos, sin contactos/búsqueda. |
| **Backend** | (a) **API Java real ahora**. Se quita Telegram y se exponen los servicios de dominio (Calendar / Sheets / Email) como REST/JSON sobre el `HttpServer` que ya existe. El React consume datos reales end-to-end. |
| **Recordatorio de las 18:00** | Se convierte en un **digest diario por email** al admin (no franja, no Telegram). |
| **Auth** | Igual que `misnotas`: una contraseña compartida en el header `x-app-password`, `AUTH_PASSWORD` por env, backoff anti fuerza-bruta. |
| **Dónde vive el front** | Carpeta `web/` dentro de este repo. La app Java sirve `web/dist`. |

Fuera de alcance en la v1: formularios médicos, contactos/búsqueda (`/find`, `/list`),
disparadores manuales `/check24|48|72`, vista de calendario/mes, control de concurrencia,
reescritura del almacenamiento (Calendar/Sheets siguen siendo la fuente de verdad).

---

## 2. Cambio de filosofía de la interfaz

| Hoy (Telegram) | Web (objetivo) |
|---|---|
| Un curso por vez, dentro de una conversación | **Panel** con todos los cursos próximos y su estado de un vistazo |
| Wizard lineal (día 1 hora → día 1 lugar → día 2 hora → …) | **Formulario**: todos los días visibles, editable siempre, reenviable |
| `/check48`, `/day2`, `/find` — comandos que hay que saber | Acciones visibles **en contexto** dentro de cada curso |
| Ping a las 18:00 que se puede perder | **Digest diario por email** con lo que requiere atención + link al panel |
| 4 flujos desconectados (detalles finales, Día 2, médico, recordatorios) | **Una página por curso** con todo junto (en v1: detalles finales + Día 2) |
| Estado en el `callbackData` y en la descripción del evento | Estado en el cliente (Zustand) + API; el evento sigue guardando `#DETAILS#{...}` |
| Mensajes que "expiran", lista `menus` que crece sin fin | Sin estado efímero server-side por chat |

---

## 3. Qué se elimina (capa Telegram)

Borrar:

- `src/main/java/main/MiBot.java`
- `src/main/java/main/telegram/` → `TelegramCenter.java`
- `src/main/java/main/courses/menus/` → **todas** (`AllSetMenu`, `CourseConfirmMenu`,
  `CourseLocationMenu`, `CourseSelectionMenu`, `CoursesMenu`, `CourseStartTimeMenu`,
  `Day2ConfirmMenu`, `FutureMenu`, `InlineMenu`, `ListMenu`, `MedicalFormReminderMenu`,
  `MedicalIssueMenu`, `MoreMenu`, `MultiMedicalFormReminderMenu`)
- `src/main/java/main/courses/menuchats/` → **todas** (`TelegramChatMain`, `MenuChat`,
  `CoursesMenuChat`, `CourseSelectionMenuChat`, `CourseStartTimeMenuChat`,
  `CourseConfirmMenuChat`, `Day2ConfirmMenuChat`, `FindMenuChat`, `FindContact`,
  `ListMenuChat`, `AllSetMenuChat`, `MedicalIssueMenuChat`, `MedicalFormReminderMenuChat`,
  `MultiMedicalFormReminderMenuChat`, `Dates`)
- `src/main/java/main/courses/ContactTelegramMessage.java`
- `src/main/java/main/courses/CoursesTelegramMessage.java`
- `src/main/java/main/courses/CoursesDaySelector.java` (orquestador de menús Telegram)
- `pom.xml`: dependencia `blue.underwater:telegram.admin`
- `Main.java`: quitar `implements TelegramAdmin.Listener`, el método `onUpdateReceived`,
  `getChat`, y todas las llamadas `TelegramCenter.getInstance()...` / `toRoot` / `toAdmin`.

Watchers de médico/contactos (`MedicalWatcher`, `ContactsWatcher`, `NewMedicalFormHandler`,
`NewContactHandler`): hoy solo sirven para **notificar a Telegram**. En v1:
- `ContactsService` se sigue cargando **una vez** al arranque (se necesita para resolver
  el nombre de pila en los emails, ver `resolveFirstName`). Se puede dejar sin watcher.
- `MedicalFormsService` + `MedicalWatcher`: **no inicializar** en v1 (evita pegar a Sheets
  cada 5 min sin usar el dato). Dejar el código, comentar la llamada en `Main`.

`MainHandler.java`: hoy imprime 10 líneas de `"aaaa…"` por request y hace `echo` de un
`code` de OAuth. Como el Calendar usa service account, ese OAuth parece muerto
(`GoogleAuthUrlBuilder` también). Decisión al ejecutar: **reemplazarlo por el fallback de
la SPA** (servir `web/dist/index.html` para rutas que no sean `/api`, `/confirm`, `/static`)
o borrarlo y crear `SpaHandler`.

---

## 4. Qué se conserva (dominio) — y qué hay que EXTRAER antes de borrar

Se conserva intacto:

- `main/calendar/`: `CalendarService`, `Course`, `EventStudents`, `Student`,
  `EventDetailsParser`. (Borrar leftovers: `EmailExtractor`, `RecentCoursesService`,
  `Test.java`, `CalendarServiceTest.java`, `RecentCoursesServiceTest.java`.)
- `main/contacts/`: `Contact`, `ContactFactory`, `ContactsReader`, `ContactsService`.
- `main/courses/`: `Offering`, `ProductType`, `EmailDispatch`, `ContactsFinder`,
  `CoursesParser`, `FreedivingCourseParticipant`.
- `main/reminder/`: `CourseReminderEmailBuilder` (armado de los emails HTML),
  `DailyReminderService` (se repurposea → digest), `Day2ReminderService` (su lógica de
  cálculo se reutiliza desde la API / digest).
- `main/sheets/`: `SettingsService` (¡las ubicaciones salen de acá!), `RegisterSheetsAdmin`,
  `RemindersSheetsAdmin`, `InternalSheetsAdmin`.
- `main/server/`: `ConfirmHandler` (queda **público**, sin auth), `StaticHandler`.
- `main/`: `ConfigManager`, `Main` (reescrito), `Logger`, `Tools`, `Day`.

### ⚠️ Lógica de dominio atrapada en clases de menú (extraer PRIMERO)

Estas clases se borran, pero tienen lógica real usada por `EventDetailsParser`,
`Day2ReminderService` y el flujo de envío. **Extraer a clases de dominio nuevas antes de
borrar los menús**:

- `CourseStartTimeMenu.slotToTimeStr(int)`, `.slotLabel(int)` → **`main/courses/TimeSlots.java`**
  (mapeo slot ↔ hora larga / hora corta).
- `CourseLocationMenu.getName(int)`, `.getUrl(int)` → **`main/courses/Locations.java`**
  (lee de `SettingsService`; index ↔ nombre/URL).

Usos actuales a re-apuntar: `EventDetailsParser` (import de `CourseStartTimeMenu`),
`Day2ReminderService`, `TelegramChatMain` (que desaparece).

### Métodos nuevos en `CalendarService`

- `saveDetails(XEvent event, String detailsStr)`: hoy guardar `#DETAILS#{...}` en la
  descripción del evento está **acoplado** al envío (`markStudentsAsPendingAndSetDetails`).
  Separar: un método para guardar el cronograma sin mandar emails (lo usa
  `PUT /api/courses/{eventId}/schedule`).
- Confirmar que `markStudentsAsPendingAndSetDetails(event, emails, detailsStr)` sigue
  sirviendo para el post-envío.

### Identidad de curso

Hoy los flujos direccionan cursos por `(fecha, índice)` porque re-piden
`getCoursesStartingOn(date)` en cada callback. Usar **`eventId`**
(`course.getXEvent().getId()`, estable) como identificador en la API; la fecha queda como
hint para acotar la búsqueda en Calendar.

---

## 5. API Java (nueva capa `main/api/`)

- Sobre `com.sun.net.httpserver` (el que ya usa `Main`). Un `HttpHandler` por recurso o un
  router mínimo.
- **JSON**: al sacar Telegram se pierde Jackson (venía transitivo de `telegrambots`).
  Agregar explícito a `pom.xml`: `com.fasterxml.jackson.core:jackson-databind` (o Gson).
- **Auth**: middleware que valida `x-app-password` contra `AUTH_PASSWORD` con
  `MessageDigest.isEqual` (tiempo constante) + backoff incremental por IP (100ms·2^n, tope
  2s), en memoria, estilo `misnotas/server/index.mjs`. Todo `/api/*` detrás de auth.
  `/confirm` y `/static` quedan públicos.
- `Cache-Control: no-store` en todo `/api`.

### Endpoints

| Método | Ruta | Qué hace |
|---|---|---|
| `GET` | `/api/me` | Valida la contraseña. `{ ok: true }` |
| `GET` | `/api/config` | Slots de horario (`TimeSlots`) + ubicaciones (`Locations`/`SettingsService`): `{ timeSlots: [{slot,label,time}], locations: [{index,name,url}] }` |
| `GET` | `/api/courses?days=14` | Cursos que empiezan entre hoy y hoy+`days`. Ver forma abajo. |
| `GET` | `/api/courses/{eventId}?date=YYYY-MM-DD` | Detalle de un curso (misma forma, con `students` completo). |
| `PUT` | `/api/courses/{eventId}/schedule` | Body `{ date, times: "12", locs: "01" }`. Guarda `#DETAILS#{...}` en el evento (`CalendarService.saveDetails`). No manda emails. |
| `POST` | `/api/courses/{eventId}/send-confirmations` | Body `{ date, times, locs }`. Lógica hoy en `TelegramChatMain.handleCourseConfirmCallback`: arma un email por alumno no pendiente/confirmado, `EmailDispatch.sendBatch`, marca pendientes con los que salieron, guarda detalles. Devuelve `{ sent: [...], failed: {email:reason}, skipped: n }`. |
| `POST` | `/api/courses/{eventId}/send-day2` | Body `{ date }`. Lógica hoy en `handleDay2ConfirmCallback`: solo "Freediver Course", usa el slot `[1]` de times/locs guardados, `CourseReminderEmailBuilder.buildDay2Reminder`, `EmailDispatch.sendBatch`. Devuelve el mismo shape. |

### Forma de `Course` en la API

```jsonc
{
  "eventId": "abc123@google.com",
  "date": "2026-09-20",          // ISO, día 1
  "type": "Freediver Course",
  "days": 2,                      // Offering.days
  "durationLabel": "5–6 hours",  // Offering.duration
  "participantCount": 5,
  "schedule": {                   // null si no hay #DETAILS# guardado
    "times": "12",               // 1 dígito por día (slot)
    "locs": "01",
    "days": [
      { "day": 1, "date": "2026-09-20", "timeSlot": 1, "timeLabel": "10:00", "locIndex": 0, "locName": "Cala X", "locUrl": "https://maps..." },
      { "day": 2, "date": "2026-09-21", "timeSlot": 2, "timeLabel": "11:00", "locIndex": 1, "locName": "Cala Y", "locUrl": "https://maps..." }
    ]
  },
  "students": [
    { "email": "a@b.com", "name": "Ana", "state": "NEW|PENDING|CONFIRMED", "medical": null }
  ],
  "confirmedCount": 2,
  "pendingCount": 1,
  "status": "NEEDS_DETAILS | DETAILS_SET | EMAILS_SENT | ALL_CONFIRMED"
}
```

### Derivación de `status` (server-side, una sola fuente)

- `NEEDS_DETAILS`: no hay bloque `#DETAILS#` en el evento (`EventDetailsParser.parse().found == false`).
- `DETAILS_SET`: hay detalles, pero ningún alumno en `PENDING`/`CONFIRMED`.
- `EMAILS_SENT`: hay al menos un `PENDING` y todavía no están todos `CONFIRMED`.
- `ALL_CONFIRMED`: todos los alumnos `CONFIRMED`.
- `Student.state`: `isConfirmed()` → CONFIRMED; `isPending()` → PENDING; si no → NEW.

---

## 6. Digest diario por email (reemplazo del ping de las 18:00)

- `DailyReminderService` mantiene su schedule (18:00 Europe/Madrid) pero en vez de
  `sendMenuToAdmins(...)` arma un **digest HTML/texto** y lo manda por `email.admin`
  (Mailjet) a `ADMIN_DIGEST_EMAIL` (nueva env var).
- Contenido: cursos que empiezan en las próximas **72h**, con su `status`, Nº de
  participantes, cuántos confirmados, y un **link al panel** (`app.url` + `/#/curso/{eventId}`).
  Incluir una línea "Hoy terminó Día 1 de X — mandá instrucciones del Día 2" cuando
  aplique (fusiona el `Day2ReminderService` de las 17:00 en el mismo digest, o se deja
  como segundo digest; preferible: **uno solo**).
- Si nada requiere atención: no mandar nada (o una línea "todo al día").
- `Day2ReminderService`: su cálculo (`checkDay2`) se reutiliza; el schedule propio se puede
  eliminar si se fusiona en el digest.
- Env nuevas: `ADMIN_DIGEST_EMAIL`, y `app.url` ya existe (`System.getProperty("app.url")`).

---

## 7. App React (`web/`)

Espejo de las convenciones de `misnotas`:

- **Stack**: Vite 5 + React 18 + TS + Tailwind 3 + Zustand + primitivos shadcn/Radix +
  `lucide-react` + `sonner` (toasts). Sin router: **routing por hash** en `App.tsx`
  (`#/` = panel, `#/curso/{eventId}` = detalle).
- **`web/package.json`** propio. Scripts: `dev` (Vite :5173), `build` (`tsc --noEmit && vite build`), `preview`.
- **`web/vite.config.ts`**: alias `@` → `src`, proxy `'/api'`, `'/confirm'`, `'/static'` → `http://localhost:8080` (la app Java local).
- **`web/index.html`**: script no-flash de tema (copiar de `misnotas`).
- **`web/tailwind.config.ts`** + **`src/index.css`**: tokens por CSS vars, dark por `class`.
  Paleta: arrancar con neutral shadcn (consola de trabajo), no la "arena + salvia" de
  misnotas — decisión de diseño abierta.

### Estructura

```
web/
  index.html
  package.json  vite.config.ts  tailwind.config.ts  postcss.config.js  tsconfig.json
  src/
    main.tsx
    App.tsx                      # auth gate + hash routing
    index.css
    types.ts                    # Course, Student, ScheduleDay, SendResult, Config...
    lib/cn.ts
    services/api.ts             # fetch con header x-app-password, maneja 401
    store/
      useAuth.ts                # igual que misnotas (check / login / logout, localStorage)
      useOpsStore.ts            # courses[], activeCourse, config; acciones que llaman api
    components/
      ui/                       # button, input, select, dialog, badge, dropdown-menu (de misnotas)
      layout/TopBar.tsx  ThemeToggle.tsx
    features/
      auth/Login.tsx            # igual que misnotas
      dashboard/Panel.tsx       # worklist de cursos próximos
      dashboard/CourseRow.tsx   # fila/card con estado + acción pendiente destacada
      course/CourseView.tsx     # página de un curso
      course/ScheduleEditor.tsx # tabla editable: por día, select de hora + select de ubicación
      course/StudentsTable.tsx  # participantes + estado de confirmación
      course/SendActions.tsx    # botones "Enviar detalles finales" / "Enviar Día 2" + panel de resultado
```

### Pantalla 1 — Panel de operaciones (`#/`)

- Trae `GET /api/courses?days=14`.
- Arriba: franja "Requiere atención" = cursos en ≤72h con `status` distinto de
  `EMAILS_SENT`/`ALL_CONFIRMED`.
- Lista: una fila por curso → fecha (día y "en X días"), tipo, `N participantes`, badge de
  `status`, y un CTA contextual ("Cargar detalles" / "Enviar emails" / "Ver"). Click → detalle.
- Orden por fecha ascendente.

### Pantalla 2 — Curso (`#/curso/{eventId}`)

- Trae `GET /api/courses/{eventId}?date=...` + `GET /api/config` (cacheado en el store).
- **Cronograma** (`ScheduleEditor`): una fila por día del curso (`Offering.days`), cada una
  con `<Select>` de hora (de `config.timeSlots`) y `<Select>` de ubicación
  (`config.locations`). Todos los días visibles a la vez, editables siempre. Botón
  **Guardar** → `PUT /api/courses/{eventId}/schedule`. Precargado con `schedule.days` si ya
  hay detalles.
- **Participantes** (`StudentsTable`): email, nombre, badge de estado (Nuevo / Pendiente /
  Confirmado).
- **Acciones** (`SendActions`):
  - *Enviar detalles finales* → `POST .../send-confirmations` con `{times,locs}` actuales.
    Deshabilitado si no hay cronograma guardado. Al volver: panel con `sent` / `failed` /
    `skipped`, toast, y refetch del curso.
  - *Enviar recordatorio Día 2* → `POST .../send-day2`. Visible solo si `type == "Freediver
    Course"` y `days == 2`. Mismo panel de resultado.
- Sin confirmación modal para guardar; **sí** confirmación ("¿Enviar a N participantes?")
  antes de cualquier `send-*`.

---

## 8. Build y deploy

- **`Dockerfile`** (hoy runtime-only) pasa a multi-stage:
  1. `node:20-alpine` → `cd web && npm ci && npm run build` → `web/dist`.
  2. `eclipse-temurin:17-jre-alpine` → copia `target/courses.app-<v>.jar` + `web/dist` a
     `/app/web/dist`.
- `docker-entrypoint.sh`: sin cambios (sigue escribiendo `credentials.json` y
  `config.properties` desde env). Nuevas env: `AUTH_PASSWORD`, `ADMIN_DIGEST_EMAIL`.
- El jar se sigue compilando local (`mvn clean package -DskipTests`) antes de `docker build`
  (igual que hoy). Ojo: hoy el `Dockerfile` NO buildea el jar; mantener ese flujo o cambiarlo.
- **`StaticHandler`**: apuntar a `web/dist` (hoy sirve `src/main/resources/static`).
  Agregar `SpaHandler` (o reusar `MainHandler`) para el fallback a `index.html`.
- **`.gitignore`**: agregar `web/node_modules/`, `web/dist/`. Sacar del control de versiones
  `dependency-reduced-pom.xml`.
- **Dev local**: terminal 1 → correr `Main` (Java, :8080). Terminal 2 → `cd web && npm run dev`
  (:5173, proxy a :8080).
- **Railway**: sigue siendo **una sola instancia** (cachés en memoria + schedulers +
  digest). No escalar horizontal.

---

## 9. Riesgos / cosas a decidir al ejecutar

- Agregar lib JSON al `pom` (Jackson/Gson) — se va con Telegram.
- Extraer `TimeSlots` / `Locations` de las clases de menú **antes** de borrarlas.
- `CalendarService.saveDetails` nuevo, desacoplado del envío.
- `MainHandler` / `GoogleAuthUrlBuilder`: confirmar que el OAuth está muerto y limpiarlo.
- Diseño visual del front (paleta neutra vs. tomar la de misnotas).
- ¿El digest se manda siempre o solo si hay algo? (propuesta: solo si hay algo).
- Tests: hoy no hay ninguno corriendo (JUnit comentado, `*Test.java` en `src/main`). Sumar
  al menos cobertura de la lógica de `send-confirmations` (armado de emails, skip de
  pendientes, marcado post-envío).
- `Main` reescrito: schedulers (`DailyReminderService`) + `HttpServer` + carga inicial de
  `ContactsService` / `SettingsService`. Sin `TelegramCenter`.

---

## 10. Orden de implementación sugerido

1. `git checkout -b feat/react-ui`.
2. Extraer `main/courses/TimeSlots.java` y `main/courses/Locations.java` desde
   `CourseStartTimeMenu` / `CourseLocationMenu`; re-apuntar `EventDetailsParser` y
   `Day2ReminderService`. Agregar Jackson al `pom`.
3. Borrar la capa Telegram (sección 3). Reescribir `Main` como servidor HTTP puro.
   Objetivo: **compila y arranca**, `ConfirmHandler` sigue funcionando.
4. `CalendarService.saveDetails`. Capa `main/api/` + auth por contraseña. Probar con `curl`
   contra Calendar/Sheets reales (`GET /api/courses`, `PUT .../schedule`,
   `POST .../send-confirmations` con un evento de prueba).
5. Repurposear `DailyReminderService` → digest por email.
6. Scaffold `web/` (Vite/React/Tailwind/Zustand) + `useAuth` + `Login` + `TopBar`.
7. `Panel` conectado a `GET /api/courses`.
8. `CourseView`: `ScheduleEditor` + `StudentsTable` + `SendActions` (confirmations + day2).
9. `Dockerfile` multi-stage + `StaticHandler`/`SpaHandler`.
10. Limpieza: borrar leftovers (`Test.java`, `EmailExtractor`, `RecentCoursesService`,
    tests viejos), `.gitignore`, actualizar `README`/crear uno.

---

## 11. Referencia: patrones de `misnotas` a copiar

`/home/maxi/Workspace/misnotas`

- `server/index.mjs`: auth por `x-app-password`, `MessageDigest.isEqual`/`timingSafeEqual`,
  backoff anti fuerza-bruta, `Cache-Control: no-store` en `/api`, fallback SPA.
- `src/App.tsx`: gate de auth + routing por hash.
- `src/store/useAuth.ts`: `status` (`checking|in|out`), `check()`, `login()`, `logout()`,
  contraseña en `localStorage`.
- `src/services/guidesApi.ts`: wrapper de `fetch` con el header, manejo de 401.
- `src/store/useEditorStore.ts`: Zustand sin persistencia local (la verdad es el servidor).
- `src/components/ui/*`: primitivos shadcn (button, dialog, dropdown-menu, input, badge).
- `src/index.css` + `tailwind.config.ts`: tokens por CSS vars, dark por `class`, no-flash.
- `Dockerfile`: multi-stage node build → runtime.
