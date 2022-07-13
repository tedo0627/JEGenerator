import net.minecrell.gitpatcher.PatchExtension

plugins {
    id("net.minecraftforge.gitpatcher") version "0.10.+"
}

configure<PatchExtension> {
    submodule = "php-build-scripts"
    target = file("build-script")
    patches = file("patches")
}

tasks.register("rebuildPatches") {
    dependsOn(tasks.makePatches)
}