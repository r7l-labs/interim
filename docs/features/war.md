# Wars — Full Documentation

This document explains how the built-in war system works in Interim, how to declare a war, what each war goal means, what the plugin currently enforces, and recommended admin workflows.

**Quick summary:** Wars are declared by a nation's capital mayor (via the War GUI). Declaring costs money, makes the two nations enemies, and creates a persisted `War` record. The GUI exposes war goals and information; surrender is implemented. Some scoring and automatic resolution behavior is shown in the UI but not fully automated by the plugin (see "What is automatic" and "What is manual").

**Where data is stored:** plugin data folder → `wars/wars.json` (managed by the plugin `DataManager`).

**Permission:**
- `interim.war` — access to the war system UI and related features.

## Roles and who can declare
- Only a nation's capital mayor (the mayor of the nation's capital town) may declare war through the GUI. The GUI enforces this by enabling the "Declare War" action only for that role.

## How to declare a war (step-by-step)
1. Open the main plugin menu: `/interim` or use the configured menu command.
2. Click the "War System" button (requires `interim.war`).
3. In the War Menu click "Declare War" (must be capital mayor).
4. Select a target nation from the eligible list (nations that are not already your ally or enemy).
5. Choose a War Goal on the "Select War Goal" screen.
6. The plugin checks your nation's bank for the declaration cost (default shown in UI: $5,000). If you can pay, the cost is withdrawn and the war is created.
7. The plugin adds each nation to the other's enemy list and broadcasts an announcement to online players.

Notes:
- The GUI also creates a `War` object with a default wager amount (config `war.minimum-wager`, default $2000) which is stored on the war record.
- Config keys referenced by the GUI: `war.declaration-cost` (default 5000.0), `war.minimum-wager` (default 2000.0), and UI text for costs/values.

## War goals: explanation and effects
When declaring a war you choose a goal. The plugin provides the following preset goals (text and victory conditions are taken from the UI):

- Territory Conquest
  - Goal: Capture enemy towns
  - Victory: Control 60% of the opponent's towns (UI) or reach 500 war points
  - Bonus: +100 points per town (UI)

- Economic Dominance
  - Goal: Plunder enemy resources
  - Victory: Steal $50,000 (UI) or reach 500 war points
  - Bonus: +50 points per $5,000 (UI)

- Political Subjugation
  - Goal: Force vassalization
  - Victory: Defeat the capital mayor 10 times (UI) or reach 500 war points
  - Bonus: Enemy becomes vassal (UI)

- Resource Control
  - Goal: Control strategic points
  - Victory: Hold 5 key territories (UI) or reach 500 war points
  - Bonus: +20 points per hour held (UI)

- Total War
  - Goal: Complete annihilation
  - Victory: Reach 500 war points
  - Bonus: Kill/capture points are doubled (UI)

Important: the UI describes scoring mechanics (e.g., "Kill enemy: +10 points", "Capture town: +50 points", "Hold territory: +5 points/hour", "First to 500 points wins") and various bonuses. Those scoring rules are presented to the player in the GUIs (War Menu / War Detail). However, the plugin codebase currently does not automatically award war points on player kills, capture events or territory holding — see the next section for exactly what is automatic and what is manual.

## Costs, wagers and treasury
- Declaration cost (default): `war.declaration-cost` (default 5000.0). The declaring nation's bank must have the amount; it is withdrawn immediately.
- Minimum wager (default): `war.minimum-wager` (default 2000.0) — used as the default wager value stored on new wars.
- Daily upkeep (shown in UI: default $500) — the GUI mentions upkeep but automatic upkeep charging is not implemented in the plugin code as of now.

## What the plugin DOES automatically right now
- Creates and persists a `War` object (stored in `wars/wars.json`) with: attacker nation UUID, defender nation UUID, start time, active flag, attacker/defender points, war goal, wager amount, per-player kills/deaths counters, captured towns set.
- Deducts the declaration cost from the attacker's nation bank on declaration and saves data.
- Adds each nation to the other's enemy list (`Nation.addEnemy(...)`).
- Broadcasts a war announcement to all online players.
- Provides a full set of GUIs to view active wars, war details, war history, and basic actions (declare, view, surrender, offer peace UI, statistics).
- Surrender flow: capital mayor may surrender via the surrender GUI — implemented consequences:
  - The war is ended (`war.setActive(false)` and end time set).
  - The surrendering nation loses captured territory (the GUI notes this; plugin marks war as ended but does not automatically reassign claims).
  - The surrendering nation pays the war wager (if they have the funds) — plugin withdraws `war.getWagerAmount()` and deposits it to the opponent.
  - The nations remove each other as enemies.
  - The plugin persists the change and broadcasts the surrender announcement.

## What the plugin DOES NOT (yet) do automatically
- Automatically award war points on kills, town captures, or territory holding. (The War model supports storing kills, deaths and captured towns, but there are no listeners wired to call `war.addKill(...)`, `war.addCapturedTown(...)`, or to increment points automatically.)
- Automatically enforce daily upkeep withdrawals or a 30-day cooldown after surrender. The UI may show these values (informational), but enforcement is not implemented.
- Automatically end a war when a goal or point threshold is reached. The war object includes `attackerPoints` and `defenderPoints` fields and a `getWinner()` helper, but there is no scheduler or listener that closes the war based on the points or duration.
- Automatically reassign claims/territory when a town is "captured" — captured towns are tracked in the `War` object, but claim ownership transfer is not automatic.

If you want these behaviors automated, I can implement them (examples: award +10 points on kill; award +50 on capture; end war at 500 points and transfer captured claims to the winner).

## How to view war information in-game
- Open the plugin menu (`/interim`), click War System, then:
  - "Active Wars" — list of active wars your nation participates in.
  - "War Details" — detailed scoreboards and top killers for a given war.
  - "War History" — past wars and winners.
  - "War Statistics" — global leaderboards and aggregated stats.

## Surrender (implemented behavior)
- Only a capital mayor may confirm surrender via the Surrender UI.
- Confirming surrender ends the war and pays the wager amount to the opponent if the surrendering nation has enough funds.
- Enemy status is removed between the two nations.
- The UI warns about consequences: losing captured territory, paying reparations, a 30-day cooldown (informational).

## Admin actions and recommendations
- There are currently no dedicated admin war console commands implemented to forcibly end or edit wars. If you need to intervene an administrator can:
  - Edit `plugins/Interim/wars/wars.json` carefully (plugin must be reloaded or server restarted to pick up manual edits), or
  - Use a future admin GUI/command — I can add admin commands for: force-end war, adjust points, force-capture towns, or refund/withdraw nation banks.

Recommendations for server admins:
- If you want automatic scoring (kills => points, capture => points), ask me to implement listeners that link kill events and claim-capture flows into active wars.
- If you want auto-end (first to 500 points, or goal-specific rules), I can add a scheduler or event-driven checks that close wars and apply configurable rewards/consequences.

## Internals & data model
- The `War` model tracks:
  - `uuid`, `attackerNation` (UUID), `defenderNation` (UUID)
  - `startTime`, `endTime`, `active` flag
  - `attackerPoints`, `defenderPoints` (integers)
  - `warGoal` (string), `wagerAmount` (double)
  - `playerKills` and `playerDeaths` maps (UUID -> int)
  - `capturedTowns` set (town UUIDs captured during the war)
- The `DataManager` persists these fields to `wars/wars.json` and loads them on plugin start.

## Examples
- Example: Declare a "Total War"
  1. Capital mayor opens the War Menu and selects a target nation.
  2. Selects "Total War" (UI displays default costs and goal: first to 500 points).
  3. Nation bank is checked for the declaration cost (default $5,000). If sufficient, the cost is withdrawn and the war is created and announced.
  4. The two nations are marked as enemies; the war is listed in "Active Wars".

- Example: Surrender
  1. Capital mayor opens War Menu → Surrender.
  2. Confirms surrender; plugin ends war, transfers the wager amount from surrendering nation to opponent (if funds available), removes enemy status, and archives the war in history.

## Next steps (optional features I can implement)
- Automatic point awarding for kills and captures (with configurable point values).
- Automatic town claim transfers on capture (with configurable protections/notifications).
- Auto-end war when victory conditions are met and automatic transfer of wager/rewards.
- Admin commands: `interim war end <war-uuid>`, `interim war force-declare ...`, `interim war adjust-points`, `interim war reload`.
- Cooldown enforcement after surrender.

If you want, I can implement a prioritized list of the above features — tell me which behaviors you want automated (for example: "award +10 points on kill; award +50 on capture; end war at 500 points and transfer captured claims to the winner") and I'll implement and wire the event listeners and admin commands.

---

If you'd like I can now:
- add automatic kill->points tracking and a scheduler to auto-end wars when a goal is reached, or
- add admin commands to force-end or edit wars, or
- generate a short player-facing help message / scoreboard that explains war scoring in-game.

Tell me which follow-up you'd like and I will implement it.
# Wars in Interim

This document explains how wars work in the Interim plugin — rules, triggers, and how players/admins can interact with wars.

## Overview
A War represents an active conflict between two Nations (or Towns when implemented). It is intended to be a controlled PvP and territory conflict mechanism. Wars are tracked by the plugin and persisted to disk.

## Starting a War
- Only players with the appropriate permissions (e.g., nation leaders) can declare war.
- Declaring war will create a `War` object in the plugin's data and notify involved parties.

## War Rules
- PvP is allowed in war zones between participants regardless of normal town/nation protections.
- Non-participants are still protected unless explicitly targeted by war mechanics.
- Damage, claiming, and other effects during war may be subject to special rules implemented by server administrators.

## Territory and Claims
- Claims remain owned by towns during wars. The plugin does not automatically transfer ownership on war events.
- Admins can use server commands to adjust claims or award territory manually after war resolution.

## War Resolution
- Wars may be resolved by surrender, a time limit, or by manual admin intervention.
- Plugin records basic metadata about wars for review and auditing.

## Administrative Notes
- Administrators can start/end wars using the admin command set.
- For automation or advanced behavior (e.g., auto-claim on victory), server owners can extend the plugin or use external moderation processes.

## Future Enhancements
- Auto-claiming territory on victory.
- War-specific claim permissions, sieges, and objectives.
- Stat tracking and leaderboards for wars.

If you want a more detailed war system (e.g., automatic conquest mechanics), tell me how you'd like wars to behave and I can implement it.