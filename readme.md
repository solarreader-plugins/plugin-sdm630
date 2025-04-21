![Release](https://img.shields.io/github/v/release/solarreader-plugins/plugin-sdm630)
![License](https://img.shields.io/github/license/solarreader-plugins/plugin-sdm630)
![Last Commit](https://img.shields.io/github/last-commit/solarreader-plugins/plugin-sdm630)
![Issues](https://img.shields.io/github/issues/solarreader-plugins/plugin-sdm630)

# SDM630 Plugin for Solarreader

This plugin integrates the **SDM630 Modbus energy meter** with **Solarreader**, enabling detailed three-phase power
monitoring and energy data acquisition.

The SDM630 is a high-precision 3-phase DIN rail-mounted energy meter by *Eastron* that supports **Modbus RTU over RS485
**. It's widely used in commercial, industrial, and solar applications.

## Features

- Connects to SDM630 via Modbus RTU (RS485)
- Reads real-time 3-phase values, including:
    - Phase voltages (L1, L2, L3)
    - Phase currents (L1, L2, L3)
    - Active, reactive, and apparent power (per phase and total)
    - Power factor and frequency
    - Import/export energy totals (kWh)

## Requirements

- RS485-to-USB or RS485 serial adapter
- SDM630 Modbus-enabled energy meter
- Main project **Solarreader**, which can be found here:  
  [https://github.com/solarreader-core/solarreader](https://github.com/solarreader-core/solarreader)



