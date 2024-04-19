plugins {
    `maven-publish`
    kotlin("jvm") version "1.9.22"
    id("dev.architectury.loom")
    id("me.modmuss50.mod-publish-plugin")
    id("me.fallenbreath.yamlang") version "1.3.1"
}

class ModData {
    val id = property("mod.id").toString()
    val name = property("mod.name").toString()
    val version = property("mod.version").toString()
    val group = property("mod.group").toString()
}

val mod = ModData()

val loader = loom.platform.get().name.lowercase()
val isFabric = loader == "fabric"
val mcVersion = stonecutter.current.project.substringBeforeLast('-')
val mcDep = property("mod.mc_dep").toString()

version = "${mod.version}+$mcVersion"
group = mod.group
base { archivesName.set("${mod.id}-$loader") }

stonecutter.expression {
    when (it) {
        "fabric" -> loader == "fabric"
        "forge" -> loader == "forge"
        "neoforge" -> loader == "neoforge"
        else -> null
    }
}

repositories {
    exclusiveContent {
        forRepository { maven("https://www.cursemaven.com") { name = "CurseForge" } }
        filter { includeGroup("curse.maven") }
    }
    exclusiveContent {
        forRepository { maven("https://api.modrinth.com/maven") { name = "Modrinth" } }
        filter { includeGroup("maven.modrinth") }
    }
    maven("https://jitpack.io") { name = "Jitpack" }
    maven("https://maven.terraformersmc.com/releases/") { name = "TerraformersMC" }
    maven("https://maven.kikugie.dev/releases")
    maven("https://maven.neoforged.net/releases/")
    maven("https://thedarkcolour.github.io/KotlinForForge/")
}

dependencies {
    fun ifStable(dep: String, action: (String) -> Any?) {
        if (stonecutter.current.project.startsWith("1.20.5")) modCompileOnly(dep)
        else action(dep)
    }

    minecraft("com.mojang:minecraft:${mcVersion}")
    mappings("net.fabricmc:yarn:${mcVersion}+build.${property("deps.yarn_build")}:v2")
    val mixinExtras = "io.github.llamalad7:mixinextras-%s:${property("deps.mixin_extras")}"
    val mixinSquared = "com.github.bawnorton.mixinsquared:mixinsquared-%s:${property("deps.mixin_squared")}"
    implementation(annotationProcessor(mixinSquared.format("common"))!!)
    if (isFabric) {
        ifStable("dev.kikugie:crash-pipe:0.1.0", ::modLocalRuntime) // Very important asset
        modLocalRuntime(fabricApi.module("fabric-registry-sync-v0", property("deps.fapi").toString()))
        modImplementation(fabricApi.module("fabric-resource-loader-v0", property("deps.fapi").toString()))
        modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
        modImplementation("net.fabricmc:fabric-language-kotlin:${property("deps.flk")}+kotlin.1.9.22")
        modCompileOnly("com.terraformersmc:modmenu:${property("deps.modmenu")}")
        include(implementation(mixinSquared.format("fabric"))!!)
    } else {
        if (loader == "forge") {
            "forge"("net.minecraftforge:forge:${mcVersion}-${property("deps.fml")}")
            compileOnly(annotationProcessor(mixinExtras.format("common"))!!)
            include(implementation(mixinExtras.format("forge"))!!)
        } else
            "neoForge"("net.neoforged:neoforge:${property("deps.fml")}")
        implementation("thedarkcolour:kotlinforforge${if (loader == "neoforge") "-neoforge" else ""}:${property("deps.kff")}")
        include(implementation(mixinSquared.format(loader))!!)
    }
    // Config
    ifStable("maven.modrinth:yacl:${property("deps.yacl")}") {
        modCompileOnly(it)
        modLocalRuntime(it)
    }

    // Compat
//    if (stonecutter.current.isActive) modLocalRuntime("net.fabricmc.fabric-api:fabric-api:${property("deps.fapi")}") // Uncomment when a compat mod complaints about no fapi
    modCompileOnly("maven.modrinth:stacked-armor-trims:1.1.0")
    modCompileOnly("maven.modrinth:allthetrims:${if (isFabric) "3.4.2" else "NXPVk0Ym"}")
    modCompileOnly("maven.modrinth:betterend:3.2.5")
}

loom {
    accessWidenerPath.set(rootProject.file("src/main/resources/elytratrims.accesswidener"))

    if (loader == "forge") {
        forge {
            convertAccessWideners.set(true)
            mixinConfigs("${mod.id}.mixins.json")
            mixinConfigs("${mod.id}-compat.mixins.json")
        }
    }

    runConfigs["client"].apply {
        ideConfigGenerated(true)
        vmArgs("-Dmixin.debug.export=true")
        programArgs("--username=KikuGie") // Mom look I'm in the codebase!
        runDir = "../../run"
    }
}

if (stonecutter.current.isActive) {
    rootProject.tasks.register("buildActive") {
        group = "project"

        dependsOn(tasks.named("build"))
    }
}

tasks.processResources {
    inputs.property("version", mod.version)
    inputs.property("mc", mcDep)

    val map = mapOf(
        "version" to mod.version,
        "mc" to mcDep,
        "fml" to if (loader == "neoforge") "1" else "45",
        "mnd" to if (loader == "neoforge") "" else "mandatory = true"
    )

    filesMatching("fabric.mod.json") { expand(map) }
    filesMatching("META-INF/mods.toml") { expand(map) }
}

yamlang {
    targetSourceSets.set(mutableListOf(sourceSets["main"]))
    inputDir.set("assets/${mod.id}/lang")
}

java {
    withSourcesJar()
    val version = if (mcVersion.startsWith("1.20.5")) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
    sourceCompatibility = version
}

kotlin {
    jvmToolchain(if (mcVersion.startsWith("1.20.5")) 21 else 17)
}

tasks.named("publishMods") {
    mustRunAfter("publish")
}

publishMods {
    file = tasks.remapJar.get().archiveFile
    additionalFiles.from(tasks.remapSourcesJar.get().archiveFile)
    displayName = "${mod.name} ${loader.replaceFirstChar { it.uppercase() }} ${mod.version} for $mcVersion"
    version = mod.version
    changelog = rootProject.file("CHANGELOG.md").readText()
    type = if (loader == "neoforge") BETA else STABLE
    modLoaders.add(loader)

    dryRun = providers.environmentVariable("MODRINTH_TOKEN")
        .getOrNull() == null || providers.environmentVariable("CURSEFORGE_TOKEN").getOrNull() == null

    modrinth {
        projectId = property("publish.modrinth").toString()
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        minecraftVersions.add(mcVersion)
        if (isFabric) requires {
            slug = "fabric-api"
        }
    }

    curseforge {
        projectId = property("publish.curseforge").toString()
        accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
        minecraftVersions.add(mcVersion)
        if (isFabric) requires {
            slug = "fabric-api"
        }
    }
}

publishing {
    repositories {
        maven("https://maven.kikugie.dev/releases") {
            name = "kikugieMaven"
            credentials(PasswordCredentials::class.java)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }

    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "${property("mod.group")}.${mod.id}"
            artifactId = mod.version
            version = mcVersion

            from(components["java"])
        }
    }
}
