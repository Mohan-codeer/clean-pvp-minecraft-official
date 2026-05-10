# Minecraft Forge 1.8.9 Mod Template

A ready-to-use template for developing Minecraft mods using Forge 1.8.9. This template provides a clean starting point with all necessary configurations and an example mod structure.

## 📋 Prerequisites

- **Java Development Kit (JDK) 8** - Required for Minecraft 1.8.9 development
- **Git** (optional) - For version control
- An IDE of your choice:
  - IntelliJ IDEA (recommended)
  - Eclipse
  - Visual Studio Code

## 🚀 Getting Started

### 1. Clone or Download

Clone this template or download it as a ZIP file to your local machine.

### 2. Setup the Development Environment

On **Linux/Mac**:
```bash
./gradlew setupDecompWorkspace
```

On **Windows**:
```bash
gradlew.bat setupDecompWorkspace
```

This process may take several minutes as it downloads dependencies and decompiles Minecraft.

### 3. Generate IDE-specific Files

#### For IntelliJ IDEA:
```bash
./gradlew idea
```
Then open the generated `.ipr` file or import the project as a Gradle project.

#### For Eclipse:
```bash
./gradlew eclipse
```
Then import the project into Eclipse workspace.

### 4. Build the Mod

To compile your mod into a JAR file:
```bash
./gradlew build
```

The compiled mod will be located in `build/libs/`.

## 🎮 Running the Mod

### From IDE

After setup, you'll have run configurations available:
- **Minecraft Client** - Launches the game with your mod loaded
- **Minecraft Server** - Launches a test server with your mod

### From Command Line

**Client**:
```bash
./gradlew runClient
```

**Server**:
```bash
./gradlew runServer
```

## 📁 Project Structure

```
.
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/examplemod/
│       │       └── ExampleMod.java       # Main mod class
│       └── resources/
│           └── mcmod.info                # Mod metadata
├── build.gradle                          # Build configuration
├── gradle/                               # Gradle wrapper files
├── gradlew                               # Gradle wrapper script (Unix)
└── gradlew.bat                          # Gradle wrapper script (Windows)
```

## 🔧 Customizing Your Mod

### 1. Update build.gradle

Change the following values in `build.gradle`:

```groovy
version = "1.0"                    // Your mod version
group = "com.yourname.modid"       // Your package name
archivesBaseName = "modid"         // Your mod's file name
```

### 2. Update mcmod.info

Edit `src/main/resources/mcmod.info` with your mod's information:

```json
{
  "modid": "yourmodid",
  "name": "Your Mod Name",
  "description": "Description of your mod",
  "version": "${version}",
  "mcversion": "${mcversion}",
  "url": "https://yourwebsite.com",
  "authorList": ["YourName"],
  "credits": "Credits here"
}
```

### 3. Rename Package and Class

1. Rename the package from `com.example.examplemod` to your own package name
2. Rename `ExampleMod.java` to your mod's main class name
3. Update the `@Mod` annotation with your mod ID and version:

```java
@Mod(modid = YourMod.MODID, version = YourMod.VERSION)
public class YourMod {
    public static final String MODID = "yourmodid";
    public static final String VERSION = "1.0";
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
        // Your initialization code here
    }
}
```

## 📦 Adding Dependencies

To add external libraries or other mods as dependencies, edit the `dependencies` block in `build.gradle`:

```groovy
dependencies {
    // Example: Add a mod as a dependency
    // deobfCompile "com.mod-buildcraft:buildcraft:6.0.8:dev"
    
    // Example: Add a regular library
    // compile 'com.google.code.gson:gson:2.8.5'
}
```

## 🔨 Development Tips

### Hot Reloading
After making changes to your code, you can rebuild without restarting the game using:
```bash
./gradlew build
```

### Changing Mappings
The MCP mappings can be updated in `build.gradle`:
```groovy
mappings = "stable_22"  // or snapshot_YYYYMMDD
```

After changing mappings, re-run the setup:
```bash
./gradlew setupDecompWorkspace --refresh-dependencies
```

### Debug Mode
Run the client or server in debug mode from your IDE to set breakpoints and inspect variables during runtime.

## 📚 Useful Resources

- [Forge Documentation](https://docs.minecraftforge.net/)
- [Forge Forums](https://forums.minecraftforge.net/)
- [McJty's Modding Tutorials](https://wiki.mcjty.eu/modding/index.php?title=Main_Page)
- [Minecraft Forge GitHub](https://github.com/MinecraftForge/MinecraftForge)
- [Forge Javadocs](https://nekoyue.github.io/ForgeJavaDocs/)

## ⚙️ Forge Version

This template uses:
- **Minecraft Version**: 1.8.9
- **Forge Version**: 11.15.1.2318
- **MCP Mappings**: stable_22
- **ForgeGradle**: 2.1.6

## 📝 License

This template includes various licenses:
- MinecraftForge is licensed under the [Minecraft Forge License](MinecraftForge-License.txt)
- FML components are licensed under the [FML License](LICENSE-fml.txt)
- Additional library licenses are included in their respective files

Please ensure your mod complies with Minecraft's EULA and Forge's licensing terms.

## 🤝 Contributing

Feel free to fork this template and customize it for your needs. If you make improvements that could benefit others, consider sharing them!

## 🐛 Troubleshooting

### Common Issues

**Problem**: `./gradlew` command not found  
**Solution**: Make sure you're in the project root directory and the file has execute permissions:
```bash
chmod +x gradlew
```

**Problem**: Build fails with "unsupported class file version"  
**Solution**: Ensure you're using JDK 8, not a newer version.

**Problem**: Game crashes on startup  
**Solution**: Check your mod ID matches in both `@Mod` annotation and `mcmod.info`. Make sure there are no spaces or special characters.

**Problem**: Changes aren't reflected in game  
**Solution**: Stop the game, rebuild with `./gradlew build`, and restart.

---

**Happy Modding! 🎮✨**
