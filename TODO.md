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
- X Add RenderSettings Codec
- X Add single entity type
- X Add synched data for Material (key for Container)
- _ Descriptions
- _ Bedrock Golem JSON
- _ Coral Golem JSON
- X Dispenser Golem JSON
- _ Localisation


## Bugs

- X Properly read ParticleType
- _ Dispenser Golem: not detecting and picking up arrows
- _ Furnace Golem: change texture based on fuel
- _ Hay Golem: crop boost
- _ Honeycomb golem: summon bees
- X Fix Kitty layer rendering
- _ Fix golem book textures

## New Features

- _ (Polished) Deepslate Golem
- _ Amethyst Block Golem (+Budding Amethyst?)
- _ Copper Golem
- _ Raw Copper Golem, Raw Gold Golem, Raw Iron Golem
- _ Moss Block Golem
- _ Calcite Golem?
- _ Tuff Golem?
- _ Tinted Glass? (add to Glass Golem)
- _ Add "/golem" command (replacement for "/summon" that is specialized for golems)
- _ Disable summoning golem entity type once that is done
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
    "knockback": 1.0, // attack knockback multiplier
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
	  "potion_array": [ // applies a random effect from this array
	    {
  	  	  "Potion": "minecraft:poison",
  	  	  "Amplifier": 0,
  	  	  "Duration": 400
  	    }
	  ],
	  "potion_target": "enemy",
	  "potion_chance": 0.75
	},
	
    // To do when attacked
	{
	  "type": "golems:hurt",
	  "fire": { // sets the enemy on fire
	    "target": "enemy",
		"chance": 0.9,
		"time": 4 // number of seconds of fire
	  }, 
	  "effect" { // applies a potion effect
		"target": "self",
		"chance": 0.25,
		"effects" [ // applies a random effect from this array
	      {
  	  	    "Potion": "minecraft:regeneration",
  	  	    "Amplifier": 0,
  	  	    "Duration": 400
  	      }
	    ],
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
	  "burn_interval": 10 // number of ticks before depleting fuel
	},
	
	// Place plants or other blocks
	{
	  "type": "golems:place_blocks",
	  "interval": 30,
	  "blocks": [
	    "minecraft:brown_mushroom",
		"#minecraft:blocks/small_flowers" // Block tag
	  ],
	  "supports": [ // blocks that can have the blocks placed on top of them
	    "minecraft:stone",
		"#minecraft:dirt" // Block tag
	  ]
	}
	
	// Split into a number of mini golems upon death
	{
	  "type": "golems:split_on_death",
	  "children": 2
	}
  ]
}
```

#### Multi-textured example data/golems/golems/mushroom.json

The below information will allow multitexture behavior if present. Here is an example for the mushroom golem. 

```
{
  [...],
  "texture_count": 2, // the number of textures
  "cycle": false, // true if interacting will change texture
  "textures": { // map of texture IDs (keys must be unique)
	"0": {
	  "blocks": [ // can be single entry or array
		"minecraft:mushroom_stem",
		"minecraft:brown_mushroom_block"
	  ],
      "loot_table": "golems:mushroom/brown", // if unspecified, uses main loot table
	  "light": 0 // light value when using this texture ID
	},
	"1": {
	  "blocks": "minecraft:red_mushroom_block",
	  "loot_table": "golems:mushroom/red",
	  "light": 0
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
