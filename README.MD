# Public Schematics
Basically //schematic for everyone, allows players to create and download schematics.

# Usage
Configure the plugin in config.yml.

Use /downloadschematic (/ds for short) to generate a schematic from your current selection.
The server will then generate a schematic file and upload it to S3 (if configured), or save to disk.

# TODO
 - [ ] Add support for object lifecycle management, so that schematics can be deleted after a certain amount of time 
 - [ ] Add permissions
 - [ ] Rewrite command stuff, that was written in a hurry
