package endfield.world;

import arc.Core;
import arc.Events;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Strings;
import arc.util.Structs;
import endfield.entities.Entitys2;
import endfield.game.TeamPayloadData;
import endfield.graphics.PositionLightning;
import endfield.util.CollectionList;
import endfield.world.blocks.defense.CommandableBlock;
import kotlin.Pair;
import mindustry.Vars;
import mindustry.game.EventType.ResetEvent;
import mindustry.io.SaveFileReader;
import mindustry.io.SaveVersion;
import mindustry.world.Block;

import static endfield.Vars2.MOD_NAME;

public final class Worlds {
	public static final CollectionList<CommandableBlock.CommandableBuild> commandableBuilds = new CollectionList<>(CommandableBlock.CommandableBuild.class);

	public static TeamPayloadData teamPayloadData = new TeamPayloadData();

	/** Don't let anyone instantiate this class. */
	private Worlds() {}

	public static void load() {
		Events.on(ResetEvent.class, event -> {
			commandableBuilds.clear();
			teamPayloadData.teamPayloadData.clear();

			PositionLightning.reset();

			Entitys2.reset();
		});
	}

	/** Not needed for now. */
	public static void init() {
		SaveVersion.addCustomChunk(MOD_NAME + "-team-payload-data", teamPayloadData);
	}

	public static void addAll() {
		SaveFileReader.fallback.putAll(
				"heavy-industry-link-block-1", "endfield-link-block-1",
				"heavy-industry-link-block-liquid-1", "endfield-link-block-liquid-1",
				"heavy-industry-placeholder-block-1", "endfield-placeholder-block-1",
				"heavy-industry-link-block-2", "endfield-link-block-2",
				"heavy-industry-link-block-liquid-2", "endfield-link-block-liquid-2",
				"heavy-industry-placeholder-block-2", "endfield-placeholder-block-2",
				"heavy-industry-link-block-3", "endfield-link-block-3",
				"heavy-industry-link-block-liquid-3", "endfield-link-block-liquid-3",
				"heavy-industry-placeholder-block-3", "endfield-placeholder-block-3",
				"heavy-industry-link-block-4", "endfield-link-block-4",
				"heavy-industry-link-block-liquid-4", "endfield-link-block-liquid-4",
				"heavy-industry-placeholder-block-4", "endfield-placeholder-block-4",
				"heavy-industry-cliff", "endfield-cliff",
				"heavy-industry-cliff-helper", "endfield-cliff-helper",
				"heavy-industry-core-zone-center", "endfield-core-zone-center",
				"heavy-industry-core-zone-dot", "endfield-core-zone-dot",
				"heavy-industry-dark-panel-7", "endfield-dark-panel-7",
				"heavy-industry-dark-panel-8", "endfield-dark-panel-8",
				"heavy-industry-dark-panel-9", "endfield-dark-panel-9",
				"heavy-industry-dark-panel-10", "endfield-dark-panel-10",
				"heavy-industry-dark-panel-11", "endfield-dark-panel-11",
				"heavy-industry-dark-panel-damaged", "endfield-dark-panel-damaged",
				"heavy-industry-metal-tiles-15", "endfield-metal-tiles-15",
				"heavy-industry-metal-tiles-16", "endfield-metal-tiles-16",
				"heavy-industry-metal-tiles-17", "endfield-metal-tiles-17",
				"heavy-industry-metal-tiles-18", "endfield-metal-tiles-18",
				"heavy-industry-asphalt", "endfield-asphalt",
				"heavy-industry-asphalt-tiles", "endfield-asphalt-tiles",
				"heavy-industry-shale-vent", "endfield-shale-vent",
				"heavy-industry-basalt-spikes", "endfield-basalt-spikes",
				"heavy-industry-basalt-plates", "endfield-basalt-plates",
				"heavy-industry-basalt-rock", "endfield-basalt-rock",
				"heavy-industry-basalt-wall", "endfield-basalt-wall",
				"heavy-industry-basalt-graphitic-wall", "endfield-basalt-graphitic-wall",
				"heavy-industry-basalt-pyratitic-wall", "endfield-basalt-pyratitic-wall",
				"heavy-industry-snowy-sand", "endfield-snowy-sand",
				"heavy-industry-snowy-sand-wall", "endfield-snowy-sand-wall",
				"heavy-industry-arkycite-sand", "endfield-arkycite-sand",
				"heavy-industry-arkycite-sand-wall", "endfield-arkycite-sand-wall",
				"heavy-industry-arkycite-sand-boulder", "endfield-arkycite-sand-boulder",
				"heavy-industry-darksand-boulder", "endfield-darksand-boulder",
				"heavy-industry-concrete-blank", "endfield-concrete-blank",
				"heavy-industry-concrete-fill", "endfield-concrete-fill",
				"heavy-industry-concrete-number", "endfield-concrete-number",
				"heavy-industry-concrete-stripe", "endfield-concrete-stripe",
				"heavy-industry-concrete", "endfield-concrete",
				"heavy-industry-stone-full-tiles", "endfield-stone-full-tiles",
				"heavy-industry-stone-full", "endfield-stone-full",
				"heavy-industry-stone-half", "endfield-stone-half",
				"heavy-industry-stone-tiles", "endfield-stone-tiles",
				"heavy-industry-concrete-wall", "endfield-concrete-wall",
				"heavy-industry-pit", "endfield-pit",
				"heavy-industry-water-pit", "endfield-water-pit",
				"heavy-industry-gravel", "endfield-gravel",
				"heavy-industry-deep-slate", "endfield-deep-slate",
				"heavy-industry-deep-slate-brick", "endfield-deep-slate-brick",
				"heavy-industry-deep-slate-wall", "endfield-deep-slate-wall",
				"heavy-industry-deep-slate-brick-wall", "endfield-deep-slate-brick-wall",
				"heavy-industry-soft-rare-earth", "endfield-soft-rare-earth",
				"heavy-industry-pattern-rare-earth", "endfield-pattern-rare-earth",
				"heavy-industry-soft-rare-earth-wall", "endfield-soft-rare-earth-wall",
				"heavy-industry-pooled-brine", "endfield-pooled-brine",
				"heavy-industry-pooled-crystal-fluid", "endfield-pooled-crystal-fluid",
				"heavy-industry-pooled-deep-crystal-fluid", "endfield-pooled-deep-crystal-fluid",
				"heavy-industry-metal-floor-water", "endfield-metal-floor-water",
				"heavy-industry-metal-floor-water-2", "endfield-metal-floor-water-2",
				"heavy-industry-metal-floor-water-3", "endfield-metal-floor-water-3",
				"heavy-industry-metal-floor-water-4", "endfield-metal-floor-water-4",
				"heavy-industry-metal-floor-water-5", "endfield-metal-floor-water-5",
				"heavy-industry-metal-floor-damaged-water", "endfield-metal-floor-damaged-water",
				"heavy-industry-stone-water", "endfield-stone-water",
				"heavy-industry-shale-water", "endfield-shale-water",
				"heavy-industry-basalt-water", "endfield-basalt-water",
				"heavy-industry-mud-water", "endfield-mud-water",
				"heavy-industry-corrupted-moss", "endfield-corrupted-moss",
				"heavy-industry-corrupted-spore-moss", "endfield-corrupted-spore-moss",
				"heavy-industry-corrupted-spore-rocks", "endfield-corrupted-spore-rocks",
				"heavy-industry-corrupted-spore-pine", "endfield-corrupted-spore-pine",
				"heavy-industry-corrupted-spore-fern", "endfield-corrupted-spore-fern",
				"heavy-industry-corrupted-spore-plant", "endfield-corrupted-spore-plant",
				"heavy-industry-corrupted-spore-tree", "endfield-corrupted-spore-tree",
				"heavy-industry-mycelium", "endfield-mycelium",
				"heavy-industry-mycelium-spore", "endfield-mycelium-spore",
				"heavy-industry-mycelium-shrubs", "endfield-mycelium-shrubs",
				"heavy-industry-mycelium-pine", "endfield-mycelium-pine",
				"heavy-industry-crystals", "endfield-crystals",
				"heavy-industry-crystals-boulder", "endfield-crystals-boulder",
				"heavy-industry-ore-silicon", "endfield-ore-silicon",
				"heavy-industry-ore-crystal", "endfield-ore-crystal",
				"heavy-industry-ore-uranium", "endfield-ore-uranium",
				"heavy-industry-ore-chromium", "endfield-ore-chromium",
				"heavy-industry-copper-wall-huge", "endfield-copper-wall-huge",
				"heavy-industry-copper-wall-gigantic", "endfield-copper-wall-gigantic",
				"heavy-industry-armored-wall", "endfield-armored-wall",
				"heavy-industry-armored-wall-large", "endfield-armored-wall-large",
				"heavy-industry-armored-wall-huge", "endfield-armored-wall-huge",
				"heavy-industry-armored-wall-gigantic", "endfield-armored-wall-gigantic",
				"heavy-industry-titanium-wall-huge", "endfield-titanium-wall-huge",
				"heavy-industry-titanium-wall-gigantic", "endfield-titanium-wall-gigantic",
				"heavy-industry-door-huge", "endfield-door-huge",
				"heavy-industry-door-gigantic", "endfield-door-gigantic",
				"heavy-industry-plastanium-wall-huge", "endfield-plastanium-wall-huge",
				"heavy-industry-plastanium-wall-gigantic", "endfield-plastanium-wall-gigantic",
				"heavy-industry-thorium-wall-huge", "endfield-thorium-wall-huge",
				"heavy-industry-thorium-wall-gigantic", "endfield-thorium-wall-gigantic",
				"heavy-industry-phase-wall-huge", "endfield-phase-wall-huge",
				"heavy-industry-phase-wall-gigantic", "endfield-phase-wall-gigantic",
				"heavy-industry-surge-wall-huge", "endfield-surge-wall-huge",
				"heavy-industry-surge-wall-gigantic", "endfield-surge-wall-gigantic",
				"heavy-industry-uranium-wall", "endfield-uranium-wall",
				"heavy-industry-uranium-wall-large", "endfield-uranium-wall-large",
				"heavy-industry-chromium-wall", "endfield-chromium-wall",
				"heavy-industry-chromium-wall-large", "endfield-chromium-wall-large",
				"heavy-industry-chromium-door", "endfield-chromium-door",
				"heavy-industry-chromium-door-large", "endfield-chromium-door-large",
				"heavy-industry-heavy-alloy-wall", "endfield-heavy-alloy-wall",
				"heavy-industry-heavy-alloy-wall-large", "endfield-heavy-alloy-wall-large",
				"heavy-industry-charge-wall", "endfield-charge-wall",
				"heavy-industry-charge-wall-large", "endfield-charge-wall-large",
				"heavy-industry-shaped-wall", "endfield-shaped-wall",
				"heavy-industry-old-tracks", "endfield-old-tracks",
				"heavy-industry-beryllium-wall-huge", "endfield-beryllium-wall-huge",
				"heavy-industry-beryllium-wall-gigantic", "endfield-beryllium-wall-gigantic",
				"heavy-industry-tungsten-wall-huge", "endfield-tungsten-wall-huge",
				"heavy-industry-tungsten-wall-gigantic", "endfield-tungsten-wall-gigantic",
				"heavy-industry-blast-door-large", "endfield-blast-door-large",
				"heavy-industry-blast-door-huge", "endfield-blast-door-huge",
				"heavy-industry-reinforced-surge-wall-huge", "endfield-reinforced-surge-wall-huge",
				"heavy-industry-reinforced-surge-wall-gigantic", "endfield-reinforced-surge-wall-gigantic",
				"heavy-industry-carbide-wall-huge", "endfield-carbide-wall-huge",
				"heavy-industry-carbide-wall-gigantic", "endfield-carbide-wall-gigantic",
				"heavy-industry-shielded-wall-large", "endfield-shielded-wall-large",
				"heavy-industry-shielded-wall-huge", "endfield-shielded-wall-huge",
				"heavy-industry-aparajito", "endfield-aparajito",
				"heavy-industry-aparajito-large", "endfield-aparajito-large",
				"heavy-industry-titanium-drill", "endfield-titanium-drill",
				"heavy-industry-large-water-extractor", "endfield-large-water-extractor",
				"heavy-industry-slag-extractor", "endfield-slag-extractor",
				"heavy-industry-oil-rig", "endfield-oil-rig",
				"heavy-industry-blast-ore-well", "endfield-blast-ore-well",
				"heavy-industry-ion-drill", "endfield-ion-drill",
				"heavy-industry-cutting-drill", "endfield-cutting-drill",
				"heavy-industry-beam-drill", "endfield-beam-drill",
				"heavy-industry-spore-farm", "endfield-spore-farm",
				"heavy-industry-heavy-plasma-bore", "endfield-heavy-plasma-bore",
				"heavy-industry-unit-miner-point", "endfield-unit-miner-point",
				"heavy-industry-unit-miner-center", "endfield-unit-miner-center",
				"heavy-industry-unit-miner-depot", "endfield-unit-miner-depot",
				"heavy-industry-inverted-junction", "endfield-inverted-junction",
				"heavy-industry-item-liquid-junction", "endfield-item-liquid-junction",
				"heavy-industry-multi-sorter", "endfield-multi-sorter",
				"heavy-industry-plastanium-router", "endfield-plastanium-router",
				"heavy-industry-plastanium-bridge", "endfield-plastanium-bridge",
				"heavy-industry-stack-helper", "endfield-stack-helper",
				"heavy-industry-chromium-efficient-conveyor", "endfield-chromium-efficient-conveyor",
				"heavy-industry-chromium-armor-conveyor", "endfield-chromium-armor-conveyor",
				"heavy-industry-chromium-tube-conveyor", "endfield-chromium-tube-conveyor",
				"heavy-industry-chromium-tube-sorter", "endfield-chromium-tube-sorter",
				"heavy-industry-chromium-stack-conveyor", "endfield-chromium-stack-conveyor",
				"heavy-industry-chromium-stack-router", "endfield-chromium-stack-router",
				"heavy-industry-chromium-stack-bridge", "endfield-chromium-stack-bridge",
				"heavy-industry-chromium-router", "endfield-chromium-router",
				"heavy-industry-chromium-junction", "endfield-chromium-junction",
				"heavy-industry-chromium-item-bridge", "endfield-chromium-item-bridge",
				"heavy-industry-phase-item-node", "endfield-phase-item-node",
				"heavy-industry-rapid-directional-unloader", "endfield-rapid-directional-unloader",
				"heavy-industry-duct-junction", "endfield-duct-junction",
				"heavy-industry-duct-distributor", "endfield-duct-distributor",
				"heavy-industry-duct-multi-sorter", "endfield-duct-multi-sorter",
				"heavy-industry-armored-duct-bridge", "endfield-armored-duct-bridge",
				"heavy-industry-rapid-duct-unloader", "endfield-rapid-duct-unloader",
				"heavy-industry-liquid-sorter", "endfield-liquid-sorter",
				"heavy-industry-liquid-valve", "endfield-liquid-valve",
				"heavy-industry-liquid-overflow-valve", "endfield-liquid-overflow-valve",
				"heavy-industry-liquid-underflow-valve", "endfield-liquid-underflow-valve",
				"heavy-industry-liquid-unloader", "endfield-liquid-unloader",
				"heavy-industry-chromium-armor-conduit", "endfield-chromium-armor-conduit",
				"heavy-industry-chromium-liquid-bridge", "endfield-chromium-liquid-bridge",
				"heavy-industry-chromium-armor-liquid-container", "endfield-chromium-armor-liquid-container",
				"heavy-industry-chromium-armor-liquid-tank", "endfield-chromium-armor-liquid-tank",
				"heavy-industry-liquid-mass-driver", "endfield-liquid-mass-driver",
				"heavy-industry-turbo-pump-small", "endfield-turbo-pump-small",
				"heavy-industry-turbo-pump", "endfield-turbo-pump",
				"heavy-industry-phase-liquid-node", "endfield-phase-liquid-node",
				"heavy-industry-reinforced-liquid-overflow-valve", "endfield-reinforced-liquid-overflow-valve",
				"heavy-industry-reinforced-liquid-underflow-valve", "endfield-reinforced-liquid-underflow-valve",
				"heavy-industry-reinforced-liquid-unloader", "endfield-reinforced-liquid-unloader",
				"heavy-industry-reinforced-liquid-sorter", "endfield-reinforced-liquid-sorter",
				"heavy-industry-reinforced-liquid-valve", "endfield-reinforced-liquid-valve",
				"heavy-industry-small-reinforced-pump", "endfield-small-reinforced-pump",
				"heavy-industry-large-reinforced-pump", "endfield-large-reinforced-pump",
				"heavy-industry-network-power-node", "endfield-network-power-node",
				"heavy-industry-smart-power-node", "endfield-smart-power-node",
				"heavy-industry-heavy-armored-power-node", "endfield-heavy-armored-power-node",
				"heavy-industry-micro-armored-power-node", "endfield-micro-armored-power-node",
				"heavy-industry-power-analyzer", "endfield-power-analyzer",
				"heavy-industry-gas-generator", "endfield-gas-generator",
				"heavy-industry-coal-pyrolyzer", "endfield-coal-pyrolyzer",
				"heavy-industry-large-thermal-generator", "endfield-large-thermal-generator",
				"heavy-industry-radiation-generator", "endfield-radiation-generator",
				"heavy-industry-liquid-generator", "endfield-liquid-generator",
				"heavy-industry-uranium-reactor", "endfield-uranium-reactor",
				"heavy-industry-hyper-magnetic-reactor", "endfield-hyper-magnetic-reactor",
				"heavy-industry-huge-battery", "endfield-huge-battery",
				"heavy-industry-armored-coated-battery", "endfield-armored-coated-battery",
				"heavy-industry-smart-beam-node", "endfield-smart-beam-node",
				"heavy-industry-beam-diode", "endfield-beam-diode",
				"heavy-industry-beam-insulator", "endfield-beam-insulator",
				"heavy-industry-reinforced-power-analyzer", "endfield-reinforced-power-analyzer",
				"heavy-industry-large-silicon-smelter", "endfield-large-silicon-smelter",
				"heavy-industry-large-kiln", "endfield-large-kiln",
				"heavy-industry-large-pulverizer", "endfield-large-pulverizer",
				"heavy-industry-large-melter", "endfield-large-melter",
				"heavy-industry-large-cryofluid-mixer", "endfield-large-cryofluid-mixer",
				"heavy-industry-large-pyratite-mixer", "endfield-large-pyratite-mixer",
				"heavy-industry-large-blast-mixer", "endfield-large-blast-mixer",
				"heavy-industry-large-cultivator", "endfield-large-cultivator",
				"heavy-industry-stone-crusher", "endfield-stone-crusher",
				"heavy-industry-fractionator", "endfield-fractionator",
				"heavy-industry-large-plastanium-compressor", "endfield-large-plastanium-compressor",
				"heavy-industry-large-surge-smelter", "endfield-large-surge-smelter",
				"heavy-industry-blast-silicon-smelter", "endfield-blast-silicon-smelter",
				"heavy-industry-crystalline-circuit-constructor", "endfield-crystalline-circuit-constructor",
				"heavy-industry-crystalline-circuit-printer", "endfield-crystalline-circuit-printer",
				"heavy-industry-crystal-activator", "endfield-crystal-activator",
				"heavy-industry-large-phase-weaver", "endfield-large-phase-weaver",
				"heavy-industry-phase-fusion-instrument", "endfield-phase-fusion-instrument",
				"heavy-industry-clarifier", "endfield-clarifier",
				"heavy-industry-corkscrew-compressor", "endfield-corkscrew-compressor",
				"heavy-industry-atmospheric-collector", "endfield-atmospheric-collector",
				"heavy-industry-atmospheric-cooler", "endfield-atmospheric-cooler",
				"heavy-industry-crystal-synthesizer", "endfield-crystal-synthesizer",
				"heavy-industry-uranium-synthesizer", "endfield-uranium-synthesizer",
				"heavy-industry-chromium-synthesizer", "endfield-chromium-synthesizer",
				"heavy-industry-heavy-alloy-smelter", "endfield-heavy-alloy-smelter",
				"heavy-industry-metal-analyzer", "endfield-metal-analyzer",
				"heavy-industry-nitrification-reactor", "endfield-nitrification-reactor",
				"heavy-industry-nitrated-oil-precipitator", "endfield-nitrated-oil-precipitator",
				"heavy-industry-blast-reagent-mixer", "endfield-blast-reagent-mixer",
				"heavy-industry-slag-centrifuge", "endfield-slag-centrifuge",
				"heavy-industry-gallium-nitride-smelter", "endfield-gallium-nitride-smelter",
				"heavy-industry-vent-heater", "endfield-vent-heater",
				"heavy-industry-chemical-silicon-smelter", "endfield-chemical-silicon-smelter",
				"heavy-industry-large-electric-heater", "endfield-large-electric-heater",
				"heavy-industry-large-oxidation-chamber", "endfield-large-oxidation-chamber",
				"heavy-industry-large-surge-crucible", "endfield-large-surge-crucible",
				"heavy-industry-large-carbide-crucible", "endfield-large-carbide-crucible",
				"heavy-industry-lighthouse", "endfield-lighthouse",
				"heavy-industry-mend-dome", "endfield-mend-dome",
				"heavy-industry-sector-structure-mender", "endfield-sector-structure-mender",
				"heavy-industry-large-shield-generator", "endfield-large-shield-generator",
				"heavy-industry-paralysis-mine", "endfield-paralysis-mine",
				"heavy-industry-detonator", "endfield-detonator",
				"heavy-industry-bomb-launcher", "endfield-bomb-launcher",
				"heavy-industry-large-radar", "endfield-large-radar",
				"heavy-industry-reinforced-overdrive-projector", "endfield-reinforced-overdrive-projector",
				"heavy-industry-bin", "endfield-bin",
				"heavy-industry-cargo", "endfield-cargo",
				"heavy-industry-machinery-unloader", "endfield-machinery-unloader",
				"heavy-industry-rapid-unloader", "endfield-rapid-unloader",
				"heavy-industry-core-storage", "endfield-core-storage",
				"heavy-industry-core-cripple", "endfield-core-cripple",
				"heavy-industry-reinforced-core-storage", "endfield-reinforced-core-storage",
				"heavy-industry-payload-junction", "endfield-payload-junction",
				"heavy-industry-payload-rail", "endfield-payload-rail",
				"heavy-industry-reinforced-payload-junction", "endfield-reinforced-payload-junction",
				"heavy-industry-reinforced-payload-rail", "endfield-reinforced-payload-rail",
				"heavy-industry-unit-maintenance-depot", "endfield-unit-maintenance-depot",
				"heavy-industry-titan-reconstructor", "endfield-titan-reconstructor",
				"heavy-industry-large-unit-repair-tower", "endfield-large-unit-repair-tower",
				"heavy-industry-senior-assembler-module", "endfield-senior-assembler-module",
				"heavy-industry-matrix-processor", "endfield-matrix-processor",
				"heavy-industry-huge-logic-display", "endfield-huge-logic-display",
				"heavy-industry-buffrerd-memory-cell", "endfield-buffrerd-memory-cell",
				"heavy-industry-buffrerd-memory-bank", "endfield-buffrerd-memory-bank",
				"heavy-industry-heat-sink", "endfield-heat-sink",
				"heavy-industry-cooler-fan", "endfield-cooler-fan",
				"heavy-industry-water-block", "endfield-water-block",
				"heavy-industry-laser-ruler", "endfield-laser-ruler",
				"heavy-industry-icon-display", "endfield-icon-display",
				"heavy-industry-icon-display-large", "endfield-icon-display-large",
				"heavy-industry-character-display", "endfield-character-display",
				"heavy-industry-character-display-large", "endfield-character-display-large",
				"heavy-industry-label-message", "endfield-label-message",
				"heavy-industry-reinforced-icon-display", "endfield-reinforced-icon-display",
				"heavy-industry-reinforced-icon-display-large", "endfield-reinforced-icon-display-large",
				"heavy-industry-reinforced-character-display", "endfield-reinforced-character-display",
				"heavy-industry-reinforced-character-display-large", "endfield-reinforced-character-display-large",
				"heavy-industry-dissipation", "endfield-dissipation",
				"heavy-industry-cobweb", "endfield-cobweb",
				"heavy-industry-coil-blaster", "endfield-coil-blaster",
				"heavy-industry-electromagnetic-storm", "endfield-electromagnetic-storm",
				"heavy-industry-frost", "endfield-frost",
				"heavy-industry-electric-arrow", "endfield-electric-arrow",
				"heavy-industry-stabber", "endfield-stabber",
				"heavy-industry-autocannon-b6", "endfield-autocannon-b6",
				"heavy-industry-autocannon-f2", "endfield-autocannon-f2",
				"heavy-industry-shellshock", "endfield-shellshock",
				"heavy-industry-rocket-launcher", "endfield-rocket-launcher",
				"heavy-industry-large-rocket-launcher", "endfield-large-rocket-launcher",
				"heavy-industry-rocket-silo", "endfield-rocket-silo",
				"heavy-industry-caelum", "endfield-caelum",
				"heavy-industry-mammoth", "endfield-mammoth",
				"heavy-industry-dragon-breath", "endfield-dragon-breath",
				"heavy-industry-breakthrough", "endfield-breakthrough",
				"heavy-industry-cloud-breaker", "endfield-cloud-breaker",
				"heavy-industry-turbulence", "endfield-turbulence",
				"heavy-industry-iron-stream", "endfield-iron-stream",
				"heavy-industry-minigun", "endfield-minigun",
				"heavy-industry-hurricane", "endfield-hurricane",
				"heavy-industry-judgement", "endfield-judgement",
				"heavy-industry-evil-spirits", "endfield-evil-spirits",
				"heavy-industry-starfall", "endfield-starfall",
				"heavy-industry-nuke-launcher-platform", "endfield-nuke-launcher-platform",
				"heavy-industry-solstice", "endfield-solstice",
				"heavy-industry-annihilate", "endfield-annihilate",
				"heavy-industry-executor", "endfield-executor",
				"heavy-industry-heat-death", "endfield-heat-death",
				"heavy-industry-rupture", "endfield-rupture",
				"heavy-industry-rift", "endfield-rift",
				"heavy-industry-large-launch-pad", "endfield-large-launch-pad",
				"heavy-industry-large-landing-pad", "endfield-large-landing-pad",
				"heavy-industry-unit-initer", "endfield-unit-initer",
				"heavy-industry-reinforced-item-source", "endfield-reinforced-item-source",
				"heavy-industry-reinforced-liquid-source", "endfield-reinforced-liquid-source",
				"heavy-industry-reinforced-power-source", "endfield-reinforced-power-source",
				"heavy-industry-reinforced-payload-source", "endfield-reinforced-payload-source",
				"heavy-industry-adaptive-source", "endfield-adaptive-source",
				"heavy-industry-random-source", "endfield-random-source",
				"heavy-industry-static-drill", "endfield-static-drill",
				"heavy-industry-omni-node", "endfield-omni-node",
				"heavy-industry-infini-mender", "endfield-infini-mender",
				"heavy-industry-infini-overdrive", "endfield-infini-overdrive",
				"heavy-industry-team-changer", "endfield-team-changer",
				"heavy-industry-barrier-projector", "endfield-barrier-projector",
				"heavy-industry-entity-remove", "endfield-entity-remove",
				"heavy-industry-invincible-wall", "endfield-invincible-wall",
				"heavy-industry-invincible-wall-large", "endfield-invincible-wall-large",
				"heavy-industry-invincible-wall-huge", "endfield-invincible-wall-huge",
				"heavy-industry-invincible-wall-gigantic", "endfield-invincible-wall-gigantic",
				"heavy-industry-dps-wall", "endfield-dps-wall",
				"heavy-industry-dps-wall-large", "endfield-dps-wall-large",
				"heavy-industry-dps-wall-huge", "endfield-dps-wall-huge",
				"heavy-industry-dps-wall-gigantic", "endfield-dps-wall-gigantic",
				"heavy-industry-must-die-turret", "endfield-must-die-turret",
				"heavy-industry-one-shot-turret", "endfield-one-shot-turret",
				"heavy-industry-point-turret", "endfield-point-turret",
				"heavy-industry-next-wave", "endfield-next-wave"
		);
	}

	public static void exportBlockData() {
		StringBuilder data = new StringBuilder();

		CollectionList<Pair<String, Block>> blocks = new CollectionList<>(Pair.class);

		Seq<Block> seq = Vars.content.blocks();
		for (Block block : seq) {
			blocks.add(new Pair<>(block.name, block));
		}

		for (var entry : SaveFileReader.fallback) {
			Block block = Vars.content.block(entry.value);
			if (block != null) {
				blocks.add(new Pair<>(entry.key, block));
			}
		}

		blocks.sort(Structs.comparingInt(pair -> pair.getSecond().id));

		for (Pair<String, Block> pair : blocks) {
			String name = pair.getFirst();
			Block block = pair.getSecond();

			data
					.append(name).append(' ')//name
					.append(block.synthetic() ? '1' : '0').append(' ')//synthetic
					.append(block.solid ? '1' : '0').append(' ')//solid
					.append(block.size).append(' ')//size
					.append(block.mapColor.rgba() >>> 8).append('\n');//mapColor
		}

		Vars.platform.showFileChooser(false, Core.bundle.get("text.export-data"), "dat", file -> {
			try {
				file.writeBytes(data.toString().getBytes(Strings.utf8), false);
				Core.app.post(() -> Vars.ui.showInfo(Core.bundle.format("text.export-data-format", file.name())));
			} catch (Throwable e) {
				Log.err(e);

				Vars.ui.showException(e);
			}
		});
	}
}
