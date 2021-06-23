# Custom GUI

## Warning: this is super rushed i know its bad lol

## File Format
  Data is stored in the json, formatted like this:
  ```
	{
		"cgui": {
	
		}
	}
  ```

## Animating GUIs
  Create a json file with the same name as the texture you want to animate, in the same directory. (Ex textures/gui/icons.png -> textures/gui/icons.json)
  A typical animation file would look something like this:
  ```
{
    "cgui": {
        "type": "animation",
        "rate": 10,
        "frames": [
            "frame_1.png",
            "frame_2.png",
            "frame_3.png"
        ]
    }
}
  ```
  - `rate` is how fast it animates
  - `frames` is an array of strings, each corrisponding to a texture for that frame

## Swapping out GUI textures
  Put your texture in `textures/gui/container`, and create a json file with the same name as your texture.
  To create the condition to use this texture, you can use the `block` member to determine which block in minecraft you need to interact with to see the menu
  An example of a custom dropper gui texture looks like this:
  ```
{
    "cgui": {
        "type": "container",
        "block": "minecraft:dropper"
    }
}
  ```

  Also, you can use blockstates to determine the texture, so obscure things like single chests facing west can have their own texture if you want
  ```
{
	"cgui": {
		"type": "container",
		"block": "minecraft:chest",
		"state": {
			"type": "single",
			"facing": "west"
		}
	}
  }
  ```
