# Missing Flag Assets

The section `prefix` stays as the album/team code. The `flag_asset` should point to the flag file used by the UI.

Most downloaded files in `src/main/resources/assets/flags` use ISO 3166-1 alpha-3 filenames, so the seeder was updated to use ISO filenames where the team maps cleanly to a country.

## Missing ISO Assets

| Section | Expected file | Search query |
|---|---|---|
| Qatar | `QAT.png` | `QAT flag png` |
| Haiti | `HTI.png` | `HTI flag png` |
| Paraguay | `PRY.png` | `PRY flag png` |
| Curaçao | `CUW.png` | `CUW flag png` |
| Ivory Coast | `CIV.png` | `CIV flag png` |
| Cape Verde | `CPV.png` | `CPV flag png` |
| Uruguay | `URY.png` | `URY flag png` |
| Senegal | `SEN.png` | `SEN flag png` |
| Iraq | `IRQ.png` | `IRQ flag png` |
| Algeria | `DZA.png` | `DZA flag png` |
| Congo DR | `COD.png` | `COD flag png` |
| Ghana | `GHA.png` | `GHA flag png` |
| Panama | `PAN.png` | `PAN flag png` |

## Missing Team-Specific Assets

England and Scotland do not have their own ISO 3166-1 alpha-3 country codes. The ISO file `GBR.png` exists, but using it would show the United Kingdom flag instead of the team flag.

| Section | Expected file | Search query |
|---|---|---|
| England | `ENG.png` | `England flag png` |
| Scotland | `SCO.png` | `Scotland flag png` |
