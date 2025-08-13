# Events Feature — Thymeleaf MVC TODOs

This checklist tracks Event features for a server-side Thymeleaf app (not REST). Do not change models. Keep the UI minimal.

## Phase 1 — Read-only pages

- [x] Controllers
  - [x] Convert `EventController` to `@Controller` (return view names)
  - [x] Route: `GET /events` → returns `events/list` with `Model` attribute `events`
  - [x] Route: `GET /events/{id}` → returns `events/detail` with `Model` attribute `event`
- [x] Views (minimal Bootstrap)
  - [x] `src/main/resources/templates/events/list.html`
    - [x] Table of events: title, date/time, location, price
    - [x] Action: View (links to `/events/{id}`)
  - [x] `src/main/resources/templates/events/detail.html`
    - [x] Show event fields
    - [x] Back link to `/events`
- [x] Navigation
  - [x] Add link to Events list on `index.html`
- [x] Security
  - [x] Ensure `GET /events/**` is accessible to authenticated USER/ADMIN

## Phase 2 — Admin create

- [x] Controllers (admin routes)
  - [x] Route: `GET /admin/events/new` → returns `admin/events/form` with empty model
  - [x] Route: `POST /admin/events` → create; on success redirect to `/events` with flash
- [x] Views
  - [x] `src/main/resources/templates/admin/events/form.html`
    - [x] Reusable for create/edit
    - [x] Fields bound to Event properties (title, description, eventDate, location, capacity, price, cancelled)
    - [x] Submit and Cancel buttons
    - [x] Show validation errors if present
- [x] Validation
  - [x] Use minimal server-side checks (without changing models)
- [x] Security
  - [x] Ensure `/admin/**` is restricted to ADMIN

## Phase 3 — Admin edit

- [x] Controllers
  - [x] Route: `GET /admin/events/{id}/edit` → returns `admin/events/form` with event loaded
  - [x] Route: `POST /admin/events/{id}` → update; on success redirect to detail or list with flash
- [x] Views
  - [x] Reuse `admin/events/form.html` with pre-filled values
- [x] Validation
  - [x] Preserve user input on error; display messages

## Phase 4 — Admin delete

- [x] Controllers
  - [x] Route: `POST /admin/events/{id}/delete` → delete; on success redirect to `/events` with flash
- [x] Views
  - [x] Delete button on detail or list (visible to admins only)

## Cross-cutting tasks

- [ ] Flash messages
  - [ ] Use `RedirectAttributes` for success/error flash; render in pages
- [ ] Date/time input
  - [ ] Use `<input type="datetime-local">` and ensure binding without model changes
- [ ] Formatting
  - [ ] Friendly date/time and price formats on list/detail
- [ ] Access control in views
  - [ ] Hide admin controls if current user is not admin
- [ ] Basic error handling
  - [ ] 404 page when event not found; simple message template

## Acceptance criteria

- [ ] Authenticated USER/ADMIN can view list and detail pages
- [ ] Only ADMIN can create, edit, delete via `/admin/events/...`
- [ ] Forms show validation errors; success and error flashes visible
- [ ] UI remains simple/minimal and easy to navigate

## Next actions (when approved to start)

1) Implement Phase 1 (read-only list and detail) with minimal templates.
2) Add admin create (Phase 2), then edit (Phase 3), then delete (Phase 4).
3) Add flashes, formatting, and view access-control polish.
