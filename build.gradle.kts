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

import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("org.spongepowered.plugin")
}

val cwl_ver: String by project
val sponge_api_ver: String by project

apply(plugin = "java")

group = "ru.pearx.cwl"
version = cwl_ver
description = "Customize your whitelist message, generate the whitelist & synchronize it automatically!"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    sourceSets {
        create("reference")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    compile("org.spongepowered:spongeapi:$sponge_api_ver")
    compile(java.sourceSets["reference"].output)
}

tasks {
    val processReference by creating(Copy::class) {
        from("src/reference/java")
        into("build/src/reference/java")
        filter<ReplaceTokens>("tokens" to mapOf("VERSION" to project.version, "DESCRIPTION" to project.description))
    }
    "compileReferenceJava"(JavaCompile::class) {
        dependsOn(processReference)
        setSource(processReference.destinationDir)
    }
}