# TO DO 1.17.1 PORT

List of bugs, fixes, and features for the 1.17 version of Extra Golems

## Steps

- X Update mappings
- X Fix initial compile errors
- X Add JSON resource loader
- X Separate GolemContainer and RenderSettings
- X Add GolemCointainer Codec
  - X Add GolemAttributes and Codec
  - X Add GolemMultitextureSettings and Codec
- X Implement multitexture settings in golem
- X Implement multitexture settings in renderer
- X Implement loot tables
- X Implement multitexture loot tables
- X Change base_block texture to an array (first found is used)
- X Add GolemBehavior for all special behavior
  - X aoe_dry
  - X aoe_freeze
  - X aoe_grow
  - X arrows
  - X explode
  - X hurt
  - X attack
  - X place_blocks
  - X passive_effect
  - X split
  - X teleport
  - X use_fuel
  - X craft_menu
  - X change texture
  - X burn in sun
  - X follow mob
  - X tempt
- X Add RenderSettings Codec
- X Add single entity type
- X Add synched data for Material (key for Container)
- X Descriptions
- X Bedrock Golem JSON
- X Coral Golem JSON
- X Dispenser Golem JSON
- X Fix localisation (golem name keys)



## Bugs

- X Properly read ParticleType
- X Dispenser Golem: not detecting and picking up arrows
- X Furnace Golem: change texture based on fuel
- X Hay Golem: crop boost
- X Honeycomb golem: summon bees
- X Fix Kitty layer rendering
- X Fix golem book textures
- X Spawn Bedrock Golem item
- _ Golem book: next-page buttons not showing on page 5&6

## New Features

- X Brick Golem
- X (Polished) Deepslate Golem
- X Amethyst Block Golem (+Budding Amethyst?)
- X Copper Golem (oxidizes; chance to summon lightning when raining)
- X Waxed Copper Golem (does not oxidize; chance to summon lightning when raining)
- X Raw Copper Golem, Raw Gold Golem, Raw Iron Golem
- X Moss Block Golem (place Moss Carpet)
- X Tinted Glass? (add to Glass Golem)
- _ Golem names: Amethyst, Moss, Raw Iron, Raw Copper, Raw Gold, Waxed Copper, Copper, Polished Deepslate
- _ Loot tables: Amethyst, Moss, Raw Iron, Raw Copper, Raw Gold, Waxed Copper, Copper, Polished Deepslate
- _ Descriptions: summon_x, follow_x, burn_in_sun
- X Add "/golem" command (replacement for "/summon" that is specialized for golems)
- X Disable summoning golem entity type once that is done
- _ Re-add HWYLA integration when available
- _ Make datapacks for other mods (Mekanism, Immersive Engineering, Thermal, CLib, Quark)


## Datapack format

Used to save data that must be synced between server and client through a datapack.

### Example data/golems/golems/clay.json

The below example includes all functionality that will be available for each golem.

```
{
  "attributes": {
    "health": 20.0,
    "attack": 2.0,
    "speed": 0.25, // movement speed
    "knockback_resistance": 0.4,
    "armor": 0, // damage resistance
    "attack_knockback": 1.0, // attack knockback multiplier
    "immune_to_fire": false,
    "immune_to_explosions": false,
    "hurt_by_water": false, // true if hurt by touching water
    "hurt_by_fall": false, // true if hurt when falling
	"hurt_by_heat": false // true if hurt while in warm biome
  },
  "swim_ability": "sink", // "sink", "float", or "swim"
  "glow": 11,
  "power": 15,
  "hidden": false, // true to omit from golem book
  "sound": "minecraft:block.gravel.step",
  "blocks": [
	"minecraft:clay_block",
	"#minecraft:blocks/clay" // example of block tag, doesn't actually exist
  ],
  "heal_items": { // map of <item, percent health restored>
    "minecraft:clay": 0.5,
	"minecraft:clay_ball": 0.1,
	"#minecraft:items/clay" // example of item tag, doesn't actually exist
  }
  // examples of each goal that can be added to any golem
  "behavior": [ // List of Compound Tags, can be empty
  
	// To do when attacking
	{
	  "type": "golems:attack",
	  "fire": { // sets the enemy on fire
	    "target": "enemy",
		"chance": 0.9,
		"time": 2 // number of seconds of fire
	  },
	  "effect": { // applies a potion effect
		"target": "enemy",
		"chance": 0.25,
		"effects" [ // applies a random effect from this array
	      {
  	  	    "Potion": "minecraft:poison",
  	  	    "Amplifier": 0,
  	  	    "Duration": 120
  	      }
	    ],
	  },
	  "summon": { // summons an entity
	    "target": "enemy", // spawns at target's location; if enemy, sets anger target
		"summon_pos": "enemy", // whether to spawn entity on self or enemy
		"chance": 0.1,
		"bonus_chance_in_rain": 0.5, // extra chance to apply while raining
	    "entity": { // CompoundTag
	      "id": "minecraft:bee"
	    }
	  }
	},
	
    // To do when attacked
	{
	  "type": "golems:hurt",
	  "fire": { // sets the enemy on fire
	    "target": "enemy",
		"chance": 0.9,
		"time": 4 // number of seconds of fire
	  }, 
	  "effect": { // applies a potion effect
		"target": "self",
		"chance": 0.25,
		"effects" [ // applies a random effect from this array
	      {
  	  	    "Potion": "minecraft:regeneration",
  	  	    "Amplifier": 0,
  	  	    "Duration": 400
  	      }
	    ],
	  },
	  "summon": { // summons an entity
	    "target": "enemy", // spawns at target's location; if enemy, sets anger target
		"summon_pos": "enemy", // whether to spawn entity on self or enemy
		"chance": 0.1,
		"bonus_chance_in_rain": 0.5, // extra chance to apply while raining
	    "entity": { // CompoundTag
	      "id": "minecraft:bee"
	    }
	  }
	},
	
	// Add potion effect with chance each tick
	{
	  "type": "golems:passive_effect",
	  "night_only": false, // only add potion effect at night or in dimension with no night
	  "effect": { // applies a potion effect
		"target": "self",
		"chance": 0.04,
		"effects": [ // applies a random effect from this array
	      {
  	  	    "Potion": "minecraft:regeneration",
  	  	    "Amplifier": 0,
  	  	    "Duration": 400
  	      }
	    ],
	  }
	}
	
	// Cause explosions
	{
	  "type": "golems:explode",
	  "range": 1.5,
	  "fuse": 60,
	  "chance_on_hurt": 0.1, // percent chance to apply when hurt
	  "chance_on_attack": 0.02 // percent chance to apply when attacking
	},
	
	// Teleport randomly (or toward enemies)
	{
	  "type": "golems:teleport",
	  "range": 32, // range is 64 for enderman
	  "chance_on_idle": 0.2, // percent chance to apply each tick
	  "chance_on_hurt": 0.8 // percent chance to apply when hurt
	  "chance_on_target": 0.5 // percent chance to apply when far from target
	},
	
	// Open crafting menu
	{
	  "type": "golems:crafting_menu"
      // no parameters
	}
	
    // Freeze surrounding blocks
	{
      "type": "golems:aoe_freeze",
	  "range": 4,
	  "interval": 4,
	  "frosted": false
	},
	
	// Dry surrounding water/waterlogged blocks
	{
      "type": "golems:aoe_dry",
	  "range": 4,
	  "interval": 5
	},
	
	// Store and shoot arrows
	{
	  "type": "golems:shoot_arrows",
	  "damage": 4.25
	},
	
	// Store and use fuel
	{
	  "type": "golems:use_fuel",
	  "max_fuel": 102400,
	  "burn_interval": 10, // number of ticks before depleting fuel
	  "texture_fueled": 1, // only used when multitexture is present
	  "texture_empty": 0
	},
	
	// Place plants or other blocks
	{
	  "type": "golems:place_blocks",
	  "interval": 30,
	  "blocks": [
	    "minecraft:brown_mushroom",
		"#minecraft:blocks/small_flowers" // Block tag
	  ]
	},
	
	// Split into a number of mini golems upon death
	{
	  "type": "golems:split_on_death",
	  "children": 2
	},
	
	// Burn in sunlight
	{
      "type": "golems:burn_in_sun",
      "chance": 0.5, // chance per tick
      "priority": 1 // goal priority
    },
	
	// Follow entity of type
	{
      "type": "golems:follow",
      "entity": "minecraft:wolf",
      "priority": 3
    },
	
	// Follow entity holding item
	{
      "type": "golems:tempt",
      "item": "#minecraft:coals", // item name or tag
      "priority": 3
    },
	
	// Change texture based on criteria
	{
	  "type": "golems:change_texture",
	  // fuel and fuel_empty only trigger when fuel changes
	  "fuel": {
	    "chance": 1.0,
	    "textures": {
	      "0": 1 // when golem has fuel and texture 0, change to texture 1
	    }
	  },
	  "fuel_empty": {
	    "chance": 1.0,
	    "textures": {
	      "1": 0 // when golem has no fuel and texture 1, change to texture 0
	    }
	  },
	  // wet and dry trigger every tick with chance
	  "wet": { // change texture every tick with chance
	    "chance": 0.1,
	    "textures": {
	      "0": 2, // when golem is wet and has texture 0, change to texture 1 
	      "1": 3 // when golem is wet and has texture 1, change to texture 3
	    }
	  },
	  "dry": { // change texture every tick with chance
	    "chance": 0.008,
	    "textures": {
	      "2": 0, // when golem is dry and has texture 2, change to texture 0 
	      "3": 1 // when golem is dry and has texture 3, change to texture 1
	    }
	  },
	  // tick triggers every tick with chance
	  "tick": { // change texture every tick with chance
	    "chance": 0.0007,
	    "textures": {
	      "0": 1, // when golem has texture 0, switch to texture 1
	      "1": 2,
	      "2": 3, 
	      "3": 3 // when golem has texture 3, do nothing (redundant; default behavior is do nothing)
	    }
	  }
	}
  ]
}
```

#### Multi-textured example data/golems/golems/mushroom.json

The below information will allow multitexture behavior if present. Here is an example for the mushroom golem. 

```
{
  [...],
  "multitexture": {
	"texture_count": 2, // the number of textures
    "cycle": false, // true if interacting will change texture
	"textures": {
	  "0": {
		"blocks": [  // can be single entry or array
		  "minecraft:mushroom_stem",
		  "minecraft:brown_mushroom_block"
		],
		"loot_table": "golems:mushroom/brown",  // if unspecified, uses main loot table
	    "light": 0 // light value when using this texture ID
	  },
	  "1": {
		"blocks": "minecraft:red_mushroom_block",
		"loot_table": "golems:mushroom/red"
	  }
	}
  }
}
```

## Rendering

Used to save information about rendering/texturing the golem

### Example assets/golems/golems/clay.json

The below example includes all functionality that will be available for each golem.

```
{
  "description": [ // array of text components to add to the golem book
    {
	  "key": "entitytip.translation_key",
	  "style": "§b"
	}
  ],
  "base": "minecraft:clay" // texture path assets/minecraft/textures/block/clay
  // for prefab textures - "base": "#golems:dispenser" // '#' indicates texture path assets/golems/textures/entity/dispenser.png
  "base_template": "golems:layer/mushroom", // template for rendering
  "base_color": 0, // color value to apply
  "use_biome_color": false, // when true, uses biome foliage color instead of base_color
  "base_light": true, // if not present, uses GolemBase#isProvidingLight
  "translucent": false,
  "layers": [ // SimpleTextureLayer
    {
	  "texture": "golems:layer/vines", // texture path "assets/golems/textures/entity/layer/vines.png"
	  "color": 8626266, // colorize vines
	  "light": true, // causes vines to glow
	  "translucent": false
	},
    {
	  "texture": "golems:layer/eyes/eyes", // texture path "assets/golems/textures/entity/layer/eyes/eyes.png"
	  "light": true // causes eyes to glow
	},
    {
	  "texture": "golems:layer/gold_edging" // texture path "assets/golems/textures/entity/layer/gold_edging.png"
	}
  ]
  
}
```

### Multi-textured Example assets/golems/golems/furnace.json

The below information will be expected when `"multitexture"` is present. Here is an example for the mushroom golem. 

```
{
  [...],
  "multitexture": {
    "base_map": { // map of textureID->block/prefab texture
      0: "#golems:furnace/lit", // '#' indicates prefab texture
	  1: "#golems:furnace/unlit" // texture path "assets/golems/textures/entity/furnace/unlit.png"
    }
  }
}
```
