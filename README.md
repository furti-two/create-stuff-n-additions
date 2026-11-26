# Create Stuff 'N Additions: refactor plan (NeoForge 1.21.1, Create 6.0.6)

## 1. What the mod adds (new set of equipment, tools and gadget)

| Family | Items | Power | Stored as |
|---|---|---|---|
| Jetpacks (chestplate) | Andesite, Copper, Brass, Netherite | Andesite = heat (lava), Copper = hydraulic (water), Brass/Netherite = steam (lava + water) | `IFluidHandlerItem` (`CustomFluidHandlerItemStack`, CUSTOM_DATA `Fluid<i>`/`Amount<i>`) |
| Exoskeletons (chestplate) | Andesite, Copper, Brass | same mapping | same |
| Tanks | Small/Medium/Large Fueling (lava), Small/Medium/Large Filling (water), Creative Filling | single tank | same |
| Gadgets | Block Picker (water), Flamethrower & Grapplin Whisk (heat), Portable Drill (steam) | | same |

Flight: SPACE key -> `FlyingMessage` + local call set `CsaFlying` in entity persistent data on both sides; the chestplate `inventoryTick` runs the tier's
`*PropelerBodyTickEventProcedure` (4 near-identical 220-line copies differing only by constants). Exoskeletons apply effects every tick and drain
every N ticks (3 copies). Refuel paths: Q-drop fuel with the item in offhand (`FuelAndFillPrProcedure`), tank right-click, Create spout (works through the capability), and right-click stacking in inventory (`overrideOtherStackedOnMe`).

## 2. How Create does it (reference: `com.simibubi.create.content.equipment.armor`)
- `BacktankItem extends BaseArmorItem`; air in a synced data component; `BacktankUtil` is the single API for read/consume/bar.
- Worn rendering: one `BacktankArmorLayer` added in `EntityRenderersEvent.AddLayers` to every humanoid renderer; geometry from `CachedBuffers.block(state)` /
  `CachedBuffers.partial(PartialModel, state)`, transformed from `model.body.translateAndRotate`, animated with `AnimationTickHolder`.
- Partials: `PartialModel.of(rl)` as `static final` fields, class-loaded from the client init; Flywheel registers and bakes them.
- `CustomRenderedArmorItem` + `HumanoidArmorLayerMixin` let an item replace vanilla armor rendering.
- First person: one `RenderArmEvent` handler, state cached in `clientTick`.

## 3. Problems found

Bugs (behaviour visible to players):
1. **Inventory right-click refuel loses fuel.** All 7 chestplates and 3 gadgets write `tagFuel`/`tagWater` into CUSTOM_DATA, but the real storage is
   the capability (`Fluid0/Amount0`). Consumed buckets/coal vanish, the tank does not fill.
2. **Exoskeleton critical-hit knockback never triggers**: `ExoskeletonHitPrProcedure` reads the dead `tagFuel`/`tagWater` keys.
3. **Andesite/Copper jetpack propellers never spin fast while flying**: renderers test `tagFuel`/`tagWater` too.
4. Idle spin speed formula `Math.round(amount / (cap*5))` only ever yields 0 or 1 -> spin is either -0.5 or -4.5, not proportional.

Performance:
5. `tagCooldown` (every tick) and `tagRotate` (andesite, every tick) are written into the ItemStack -> the equipped stack changes every tick on both
   sides -> server re-syncs the slot/equipment every tick for every wearer, and `copyTag()` is called 5-15 times per tick per wearer.
6. `getHumanoidArmorModel` bakes a new model (`bakeLayer`) every frame for every wearer; the andesite one bakes it 3 times per call.
7. First-person handlers bake a model every frame.
8. 7 separate render layers added to every humanoid renderer; each re-queries the capability several times per frame.
9. Propellers/cogs are rendered through a fake registered block (`propeller_render_prop` with an int blockstate) instead of partial models.

## 4. Target architecture

```
fuel/
  FuelType            enum HEAT(lava), HYDRAULIC(water), STEAM(lava+water): tank layout, colours, labels
  FuelableItem        interface: getFuelType(), getCapacity(stack); default bar/tooltip/refuel/consume helpers
  FuelStorage         static API over the capability (like BacktankUtil): amount, ratio, hasFuel, consume, fill
  FuelRefuelHandler   shared "stacked on me" + Q-drop refuel logic (buckets, potions, sponge, burnables)
equipment/
  PoweredChestplateItem   abstract ArmorItem + FuelableItem: armour material helper, tooltip, tick dispatch,
                          interval-based drain without writing the stack, `isFlying` written only on change
  JetpackItem / JetpackProfile          one flight algorithm, per-tier constants in a profile
  ExoskeletonItem / ExoskeletonProfile  effects + drain interval + sound
client/
  CSAPartialModels                partial models replacing the fake block states
  PoweredChestplateLayer          single layer, dispatches to a per-item `ChestplateRenderer`
  JetpackRenderers / ExoskeletonRenderers   the propeller layouts, data-driven
  ArmorModelCache                 bakes each armour model once per resource reload
  PoweredArmFirstPersonRenderer   one RenderArmEvent handler with cached model
```

Compatibility rules:
- Item ids, registry names and the CUSTOM_DATA fluid format stay identical: existing worlds keep their items and fuel.
- Legacy `tagFuel`/`tagWater` values found on a stack are migrated once into the capability on first tick, then removed.
- Gameplay constants (speeds, heights, cooldowns, effects) are copied exactly from the current procedures.
