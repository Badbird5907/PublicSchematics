# Public Schematics
Basically //schematic for everyone, allows players to create and download schematics (S3/Disk).

Also has support for replacing blocks, preventing abuse.

For example ores -> stone/deepslate. (see [config.yml](https://github.com/Badbird5907/PublicSchematics/blob/master/src/main/resources/config.yml))

![replacement](https://user-images.githubusercontent.com/50347938/228418645-baf7e75a-2d31-4667-b644-7c11801ceae6.png)

# Usage
Configure the plugin in config.yml.

Use /downloadschematic (/ds for short) to generate a schematic from your current selection.
The server will then generate a schematic file and upload it to S3 (if configured), or save to disk.

# TODO
 - [ ] Add support for object lifecycle management, so that schematics can be deleted after a certain amount of time 
 - [ ] Add permissions
 - [ ] Rewrite command stuff, that was written in a hurry
 - [ ] Configurable messages
