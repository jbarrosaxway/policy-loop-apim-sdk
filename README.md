# Circuit Loop Filter for Axway API Gateway

This project provides a custom Circuit Loop Filter for Axway API Gateway, allowing you to implement looping logic in your policies with configurable exit conditions.

## 🚀 Quick Start Guide

### Installation from GitHub Release

1. **Download the latest release ZIP**
2. **Extract and copy the files:**
   ```bash
   # Copy main JAR
   cp policy-loop-filter-apim-sdk-*.jar /opt/Axway/apigateway/groups/group-2/instance-1/ext/lib/
   ```

3. **Restart the gateway:**
   - Use the appropriate method for your installation (service, script, etc.)

4. **Add to Policy Studio:**
   - Open Policy Studio
   - Go to **Window > Preferences > Runtime Dependencies**
   - Add the JAR to the classpath
   - Restart Policy Studio with `-clean`

5. **Use the filter:**
   - Search for **"Circuit Loop Filter"** in the palette (Utility category)
   - Configure the required parameters
   - Test the integration

---

## API Management Version Compatibility

This artifact has been successfully tested with the following versions:
- **Axway API Gateway 7.7.0.20230830** ✅
- **Axway API Gateway 7.7.0.20240830** ✅
- **Axway API Gateway 7.7.0.20250530** ✅

## Overview

The Circuit Loop Filter provides powerful looping capabilities for Axway API Gateway policies:

### **Key Features:**
- **Multiple Exit Conditions**: Configure when the loop should stop
- **Flexible Loop Types**: While and do/while loop support
- **Configurable Limits**: Set maximum iterations and time limits
- **Error Handling**: Customize success/error behavior for each exit condition
- **Policy Studio Integration**: Full graphical interface with visual configuration

### **Exit Conditions:**
1. **Policy Returns False**: Child policy indicates loop should stop
2. **Loop Condition False**: Expression evaluation returns false
3. **Maximum Iterations**: Configurable iteration limit reached
4. **Time Limit Exceeded**: Maximum loop duration expired

---

## 📦 GitHub Releases

### **Automatic Downloads**

Releases are automatically created on GitHub and include:

#### **Files Available in Each Release:**
- **Main JAR** - `policy-loop-filter-apim-sdk-*.jar` (built for multiple Axway versions)
- **Policy Studio Resources** - `src/main/resources/yaml/` with filter definitions
- **Gradle Wrapper** - `gradlew`, `gradlew.bat` and `gradle/` folder
- **Gradle Configuration** - `build.gradle` with installation tasks
- **Linux Script** - `install-linux.sh` for automated installation

#### **Installation from Release:**

**Windows (Recommended):**
```bash
# Extract the release ZIP
# Navigate to the extracted folder
# Run the Gradle task:
.\gradlew "-Dproject.path=C:\Users\jbarros\apiprojects\my-axway-project" installWindowsToProject
```

**Linux:**
```bash
# Extract the release ZIP
# Run the installation script:
./install-linux.sh
```

### **Supported Versions:**

Supported versions are defined in **[📋 axway-versions.json](axway-versions.json)**:

| Version | Description |
|---------|-------------|
| **7.7.0.20230830** | Stable August 2023 version - libs detected automatically |
| **7.7.0.20240830** | Stable August 2024 version - libs detected automatically |
| **7.7.0.20250530** | Stable May 2025 version - libs detected automatically |

**Default version:** `7.7.0.20240830`

---

## Build and Installation

### 🔧 Dynamic Configuration

The project supports **dynamic configuration** of the Axway API Gateway path:

```bash
# Default configuration
./gradlew clean build installLinux

# Custom configuration
./gradlew -Daxway.base=/opt/axway/Axway-7.7.0.20240830 clean build installLinux

# Check current configuration
./gradlew setAxwayPath
```

### Linux
```bash
# Build the JAR (Linux only)
./gradlew buildJarLinux

# Automated build and installation
./gradlew clean build installLinux

# With custom path
./gradlew -Daxway.base=/path/to/axway clean build installLinux
```

### Windows
```bash
# Install only YAML files in Policy Studio project
./gradlew installWindows

# Install in specific project (with path)
./gradlew "-Dproject.path=C:\Users\jbarros\apiprojects\my-axway-project" installWindowsToProject

# Interactive installation (if path not specified)
./gradlew installWindowsToProject
```

### 🐳 **Docker**

#### **Build with Docker**

This project uses Docker images for automated build, configured in **[📋 axway-versions.json](axway-versions.json)**.

**Image contents:**
- Axway API Gateway (specific version)
- Java 11 OpenJDK
- Gradle for build
- All required dependencies

#### **Build using Docker**

```bash
# Build the JAR using the published image (default version)
./scripts/build-with-docker-image.sh

# Or manually:
docker run --rm \
  -v "$(pwd):/workspace" \
  -v "$(pwd)/build:/workspace/build" \
  -w /workspace \
  <docker-image> \
  bash -c "
    export JAVA_HOME=/opt/java/openjdk-11
    export PATH=\$JAVA_HOME/bin:\$PATH
    gradle clean build
  "
```

> 💡 **Tip**: GitHub Actions uses the published image `axwayjbarros/policy-loop-filter-apim-sdk:1.0.0`.

#### **Test Published Image**

```bash
# Test the published image
docker pull axwayjbarros/policy-loop-filter-apim-sdk:1.0.0
docker run --rm axwayjbarros/policy-loop-filter-apim-sdk:1.0.0 java -version
docker run --rm axwayjbarros/policy-loop-filter-apim-sdk:1.0.0 ls -la /opt/Axway/
```

> ⚠️ **Note**: This image is **for build only**, not for application runtime.

#### **Using the Image for Build**

The image `axwayjbarros/policy-loop-filter-apim-sdk:1.0.0` is **for build only**, not for runtime. It contains all Axway API Gateway libraries needed to compile the project:

```bash
# Build using the image (libraries only)
docker run --rm \
  -v "$(pwd):/workspace" \
  -v "$(pwd)/build:/workspace/build" \
  -w /workspace \
  axwayjbarros/policy-loop-filter-apim-sdk:1.0.0 \
  bash -c "
    export JAVA_HOME=/opt/java/openjdk-11
    export PATH=\$JAVA_HOME/bin:\$PATH
    gradle clean build
  "
```

#### **Image Specifications:**
- **Base**: Axway API Gateway 7.7.0.20240830-4-BN0145-ubi9
- **Java**: OpenJDK 11.0.27
- **Libraries**: All Axway API Gateway libs available
- **Usage**: Build only, not for application runtime

#### **GitHub Actions**

The project uses the image for automated build:

- **Continuous Build**: `.github/workflows/build-jar.yml`
- **Release**: `.github/workflows/release.yml`
- **Image**: `axwayjbarros/policy-loop-filter-apim-sdk:1.0.0`

### ⚠️ **Important: JAR Build**

**The JAR build must be done on Linux** due to Axway API Gateway dependencies. For Windows:

1. **Build on Linux:**
   ```bash
   ./gradlew buildJarLinux
   ```

2. **Copy JAR to Windows:**
   ```bash
   # Copy the file: build/libs/policy-loop-filter-apim-sdk-1.0.1.jar
   # To the Windows environment
   ```

3. **Install YAML on Windows:**
   ```bash
   ./gradlew installWindows
   ```

### 🔄 **Linux vs Windows Process**

| Linux | Windows |
|-------|---------|
| ✅ JAR build | ❌ JAR build |
| ✅ Full installation | ✅ YAML installation |
| ✅ Native dependencies | ⚠️ External JARs |
| ✅ Automatic configuration | ⚠️ Manual configuration |

**Linux**: Full process (JAR + YAML + installation)  
**Windows**: YAML only (JAR must be built on Linux)

### Useful Commands
```bash
# List all available tasks
./gradlew showTasks

# Check Axway configuration
./gradlew setAxwayPath

# Build only
./gradlew clean build
```

---

## Manual Installation (Alternative)

### Linux

1. **Automated build and installation:**
   ```bash
   ./gradlew clean build
   ./scripts/linux/install-filter.sh
   ```

2. **Configure Policy Studio:**
   - Open Policy Studio
   - Go to **Window > Preferences > Runtime Dependencies**
   - Add the JAR: `/opt/axway/Axway-7.7.0.20240830/apigateway/groups/group-2/instance-1/ext/lib/policy-loop-filter-apim-sdk-1.0.1.jar`
   - Restart Policy Studio with `-clean`

### Windows

1. **Install YAML files (interactive):**
   ```bash
   ./gradlew installWindows
   ```
   Gradle will prompt for the Policy Studio project path.

2. **Install YAML files in a specific project:**
   ```bash
   ./gradlew -Dproject.path=C:\path\to\project installWindowsToProject
   ```

3. **Configure Policy Studio:**
   - Open Policy Studio
   - Go to **Window > Preferences > Runtime Dependencies**
   - Add the JAR: `policy-loop-filter-apim-sdk-1.0.1.jar`
   - Restart Policy Studio with `-clean`

---

## Usage

### **Basic Configuration**

1. **Install JARs:**
   - Copy `policy-loop-filter-apim-sdk-<version>.jar` to `/opt/Axway/apigateway/groups/group-2/instance-1/ext/lib/`
   - Restart the gateway

2. **Add to Policy Studio:**
   - Go to **Window > Preferences > Runtime Dependencies**
   - Add the JAR to the classpath
   - Restart Policy Studio with `-clean`

3. **Configure filter:**
   - Search for **"Circuit Loop Filter"** in the palette (Utility category)
   - Configure the required parameters
   - Test the integration

### **Filter Configuration**

The Circuit Loop Filter provides the following configuration options:

#### **Loop Type**
- **While Loop**: Condition is checked before each iteration
- **Do/While Loop**: Condition is checked after each iteration

#### **Exit Conditions**
1. **Policy Returns False**: Child policy indicates loop should stop
2. **Loop Condition**: Expression that must be true to continue looping
3. **Maximum Iterations**: Hard limit on number of loop iterations
4. **Maximum Time**: Time limit for the entire loop execution

#### **Error Handling**
- **Success on Exit**: Configure which exit conditions should be treated as success
- **Error on Exit**: Configure which exit conditions should be treated as errors

#### **Child Policy**
- **Delegate Policy**: The policy that will be executed in each loop iteration

### **Example Use Cases**

1. **Retry Logic**: Retry failed operations until success or max attempts
2. **Polling**: Check for status changes at regular intervals
3. **Batch Processing**: Process items in batches until completion
4. **Conditional Looping**: Continue until specific business conditions are met

---

## Project Structure

```
policy-loop-filter-apim-sdk/
├── README.md                                # Main documentation
├── build.gradle                             # Gradle build configuration
├── axway-versions.json                      # Supported Axway versions
├── scripts/                                 # Utility and build scripts
│   ├── build-with-docker-image.sh           # Build JAR with Docker
│   ├── install-linux.sh                     # Linux install script
│   └── linux/
│       └── install-filter.sh                # Linux filter install (used by Gradle)
├── src/
│   └── main/
│       ├── java/                            # Java source code
│       │   └── com/axway/loop/
│       │       ├── CircuitLoopFilter.java   # Main filter class
│       │       ├── CircuitLoopProcessor.java # Core loop logic
│       │       ├── CircuitLoopFilterUI.java # Policy Studio UI
│       │       └── CircuitLoopFilterPage.java # Configuration page
│       └── resources/
│           └── yaml/
│               ├── System/
│               │   └── Internationalization Default.yaml
│               └── META-INF/
│                   └── types/
│                       └── Entity/
│                           └── Filter/
│                               └── CircuitLoopFilter.yaml
└── build/                                   # Build output (generated)
    └── libs/
        └── policy-loop-filter-apim-sdk-<version>.jar
```

---

## Tests

### Test Status

| Test Type | Status |
|-----------|--------|
| **Entity Store (YAML)** | ✅ Tested |
| **Entity Store (XML)** | ❌ **Not tested** |
| **Policy Studio Integration** | ✅ Tested |
| **Loop Logic** | ✅ Tested |

### Next Required Tests

1. **Test Entity Store XML** - Validate compatibility with XML format
2. **Performance Tests** - Evaluate performance with different loads
3. **Concurrency Tests** - Multiple simultaneous loop executions
4. **Edge Cases** - Test with extreme values and conditions

---

## Troubleshooting

### Common Issues

1. **Filter does not appear in the palette:**
   - Check if the JAR was added to the classpath
   - Restart Policy Studio with `-clean`
   - Verify the filter is in the "Utility" category

2. **Loop not executing:**
   - Check loop condition expression
   - Verify child policy configuration
   - Check maximum iteration and time limits

3. **Infinite loops:**
   - Verify exit conditions are properly configured
   - Check loop condition expression logic
   - Ensure child policy can return false

### Logs

The filter generates detailed logs using `Trace` logging:
- **Loop Start**: "Starting Circuit Loop Filter execution"
- **Iteration**: "Loop iteration X of Y"
- **Exit Condition**: "Loop exiting due to: [condition]"
- **Success/Error**: "Loop completed with status: [success/error]"

### Debug Information

Enable debug logging to see detailed execution flow:
- Loop condition evaluation
- Child policy execution results
- Exit condition checks
- Timing information

---

## Security

- **No External Dependencies**: The filter only uses Axway API Gateway libraries
- **Policy Isolation**: Each loop iteration runs in isolated policy context
- **Resource Limits**: Configurable limits prevent resource exhaustion
- **Audit Logging**: All loop executions are logged for audit purposes

---

## 🚀 **CI/CD Pipeline**

### **GitHub Actions**

The project includes automated workflows that use Docker for build:

#### **CI (Continuous Integration)**
- **Trigger**: Push to `main`, `develop` or Pull Requests
- **Actions**:
  - ✅ Login to Axway registry (for base image)
  - ✅ Build Docker build image (with Axway + Gradle)
  - ✅ Build JAR inside Docker container
  - ✅ Upload JAR as artifact
  - ✅ JAR tests

#### **Release**
- **Trigger**: Tag push (`v*`)
- **Actions**:
  - ✅ Login to Axway registry
  - ✅ Build Docker build image
  - ✅ Build JAR inside container
  - ✅ Generate changelog
  - ✅ Create GitHub Release
  - ✅ Upload JAR to release
  - ✅ JAR tests

### **Build Flow**

```
1. Login to Axway Registry
   ↓
2. Build Docker image (with Axway + Gradle)
   ↓
3. Run JAR build inside container
   ↓
4. Generate final JAR
   ↓
5. Upload to GitHub Release/Artifacts
```

### **Why use Docker?**

- ✅ Consistent environment: Always the same Axway environment
- ✅ Guaranteed dependencies: Axway + Gradle + Java 11
- ✅ Isolation: Build isolated in container
- ✅ Reproducibility: Always the same result
- ✅ Does not publish image: Only used for build

### **Generated Artifacts**

#### **Main JAR**
```
policy-loop-filter-apim-sdk-1.0.1.jar
├── Circuit Loop Java Filter
├── Policy Studio UI classes
├── YAML configurations
└── All required dependencies
```

#### **Location**
- **GitHub Releases**: Available for download
- **GitHub Actions Artifacts**: During CI/CD
- **Local**: `build/libs/policy-loop-filter-apim-sdk-*.jar`

### How to Use

#### Download the JAR
1. Go to **Releases** on GitHub
2. Download the JAR of the desired version
3. Follow the installation guide

#### Local Build
```bash
# Build the JAR (requires local Axway)
./gradlew buildJarLinux

# Or using the automated Docker build (recommended)
./scripts/build-with-docker-image.sh
```

---

## Contributing

Please read [Contributing.md](https://github.com/Axway-API-Management-Plus/Common/blob/master/Contributing.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Team

![alt text][Axwaylogo] Axway Team

[Axwaylogo]: https://github.com/Axway-API-Management/Common/blob/master/img/AxwayLogoSmall.png  "Axway logo"

## License
[Apache License 2.0](LICENSE)
