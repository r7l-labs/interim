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