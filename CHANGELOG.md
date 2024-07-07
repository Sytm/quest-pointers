# Changelog

## [Unreleased]

## 1.1.0

### Added
- Ability to set custom model data for the hologram item. Example: `PAPER|12345`
- Ability to use a subset of enabled pointers for specific targets. **This changes the command**
  - New installations automatically use the new command format
  - If upgrading and you want this, add `commandVersion: 1` to your config, but make sure all your usages
    of this command use the updated format
  - If you didn't update your commands you can add `commandVersion: 0` to your config to continue using the old format.
    (If the value is missing from the config, a value of 0 is assumed)

## 1.0.2

### Fixed
- Thread check is triggered when a player sees the blinking block and teleports away on Folia [#1](https://github.com/Sytm/quest-pointers/issues/1)

## 1.0.1

### Changed
- The movement of the hologram pointer has been smoothed, but the plugin now requires at least 1.20.2

### Fixed
- Default colors for beacon beams are not applied

## 1.0.0
Initial release of this plugin