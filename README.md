# CleanPVP

Client-side Fabric mod for a clean technical PvP HUD. It includes draggable widgets for:

- Keystrokes + mouse clicks
- CPS (left/right)
- FPS
- Armor status + durability
- Potion effects

Layouts persist in `config/cleanpvp-hud.json`.

## Controls

- `Right Shift`: Open HUD editor
- `Esc`: Save and close editor

In the editor:

- Drag widgets with left click
- Toggle widgets from the right panel
- Switch style modes:
  - `Color`: `SOLID` / `RAINBOW`
  - `Fill`: `OUTLINE_ONLY` / `SOLID_BOX`
  - `Solid Color`: cycle color presets
  - `Key Size`: `SMALL` / `MEDIUM` / `LARGE`

## UI Theme

- Replaces menu panorama/texture backgrounds with a green -> blue gradient
- Replaces vanilla button textures with gradient buttons
- Main title screen heading is rendered as `CLEAN PVP`

## Recommended mod stack for low-end PvP (MC 26.1.2, Fabric)

- Fabric API
- Sodium
- Lithium
- ImmediatelyFast
- Entity Culling
- Sodium Extra
- Reese's Sodium Options
- Mod Menu

Avoid running multiple full HUD suites at once (to prevent overlap and performance cost).

## Setup

For setup instructions, see the [Fabric Documentation page](https://docs.fabricmc.net/develop/getting-started/creating-a-project#setting-up).

## License

This template is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.
