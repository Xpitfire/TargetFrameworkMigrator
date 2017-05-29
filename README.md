# TargetFrameworkMigrator
Simple tool to migrate the target framework for Visual Studio Projects Solution wide.

## Requirements
[Java 8 or higher](https://java.com/en/download/)

## Usage
[Download](https://github.com/Xpitfire/TargetFrameworkMigrator/releases) release java file.

Open command prompt or PowerShell and execute:
```
java -jar migrator.jar -path=<path-to-root-dir> -recursive=<true/false> -version=<version-string>
```

Parameter | Description
--- | ---
-path | root path of the solution
-version |  parameter for defining the destination path
-recursive | optional: by default recursive is set to false, to prevent unwanted recursive descent

## Example

```
java -jar MonitoringRenamer.jar -path=C:/Worspace/Demo -recursive=true -version=v4.6.2
```
