/*
 *     Copyright © 2018 mrAppleXZ.
 *     This file is part of Custom Whitelist.
 *     Custom Whitelist is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Custom Whitelist is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Custom Whitelist.  If not, see <https://www.gnu.org/licenses/>.
 */

val sponge_plugin_ver: String by settings

pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if(requested.id.id == "org.spongepowered.plugin")
            {
                useVersion(sponge_plugin_ver)
            }
        }
    }
}

rootProject.name = "cwl"